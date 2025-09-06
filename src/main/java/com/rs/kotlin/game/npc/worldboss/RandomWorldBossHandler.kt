package com.rs.kotlin.game.npc.worldboss

import com.rs.core.thread.CoresManager
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.world.util.Msg
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Random World Boss spawner/rotator.
 *
 * - Spawns a random boss at a random configured location after a random delay.
 * - Supports multi-spawns (Dagannoth Kings, Kalphite Queen + minions).
 * - Boss and minion hitpoints scale with active player count.
 * - If the boss isn't interacted with for [IDLE_TIMEOUT_MS], it despawns.
 */
object RandomWorldBossHandler {

    // ---------------------- CONFIG ----------------------

    private sealed class BossEntry {
        data class Single(val npcId: Int) : BossEntry()
        data class Group(val npcIds: List<Int>) : BossEntry()
        data class WithMinions(val bossId: Int, val minionIds: List<Int>, val minionCount: Int) : BossEntry()
    }

    private val BOSS_ENTRIES: List<BossEntry> = listOf(
        BossEntry.Single(6260), // General Graardor
        BossEntry.Single(6222), // Kree'arra
        BossEntry.Single(6203), // K'ril
        BossEntry.Single(6247), // Zilyana
        BossEntry.Single(50),   // King Black Dragon
        BossEntry.Single(8133), // Corporeal Beast
        BossEntry.WithMinions(1158, listOf(NPC.getNpc("npc.kalphite_guardian_lv141")), 6), // Kalphite Queen + 6 workers
        BossEntry.Group(listOf(2881, 2882, 2883)),    // Dagannoth Kings
        BossEntry.Single(2030), // Barrows brother
        BossEntry.Single(3200)  // Chaos Elemental
    )

    private data class SpawnLocation(val name: String, val tile: WorldTile)

    private val SPAWN_LOCS: List<SpawnLocation> = listOf(
        SpawnLocation("North of Falador (Multi)", WorldTile(2971, 3425, 0)),
        SpawnLocation("Keep Le Faye - North of Legends Guild", WorldTile(2750, 3401, 0)),
        SpawnLocation("Castle Wars", WorldTile(2459, 3098, 0)),
        SpawnLocation("Northern Karamja Coast", WorldTile(2781, 3127, 0)),
        SpawnLocation("Lumbridge Swamp", WorldTile(3206, 3158, 0)),
        SpawnLocation("Rimmington Mine", WorldTile(2974, 3240, 0)),
        SpawnLocation("South of Relekka", WorldTile(2662, 3610, 0)),
        SpawnLocation("Wilderness – Dark Warrior Hills", WorldTile(3100, 3619, 0)),
    )

    private const val MIN_RESPAWN_DELAY_MS: Long = 6 * 60 * 1000L
    private const val MAX_RESPAWN_DELAY_MS: Long = 14 * 60 * 1000L
    private const val IDLE_TIMEOUT_MS: Long = 5 * 60 * 1000L
    private const val GRACE_PERIOD_MS: Long = 30 * 1000L
    private const val AVOID_SAME_LOCATION_TWICE = true

    // ---------------------- STATE ----------------------

    @Volatile private var currentBoss: WorldBossNPC? = null
    @Volatile private var currentMinions: MutableList<WorldMinionNPC> = mutableListOf()
    @Volatile private var nextSpawnTask: ScheduledFuture<*>? = null
    @Volatile private var lastSpawnLocIndex: Int = -1

    // ---------------------- PUBLIC API ----------------------

    @JvmStatic
    @Synchronized
    fun start() {
        if (currentBoss == null && nextSpawnTask == null) {
            scheduleNextSpawn(randomRespawnDelay())
        }
    }

    @Synchronized
    fun forceRespawnNow() {
        cancelPending()
        spawnNewBoss(force = true)
    }

    @Synchronized
    fun forceDespawn(reason: String = "Force-despawned") {
        currentBoss?.let {
            it.markExternallyDespawning(reason)
            it.finish()
        } ?: scheduleNextSpawn(2000L)
    }

    // ---------------------- INTERNALS ----------------------

    @Synchronized
    private fun cancelPending() {
        nextSpawnTask?.cancel(false)
        nextSpawnTask = null
    }

    @Synchronized
    private fun scheduleNextSpawn(delayMs: Long) {
        cancelPending()
        val safeDelay = max(1000L, delayMs)
        nextSpawnTask = CoresManager.getSlowExecutor().schedule({
            try {
                spawnNewBoss()
            } catch (t: Throwable) {
                t.printStackTrace()
                scheduleNextSpawn(randomRespawnDelay())
            }
        }, safeDelay, TimeUnit.MILLISECONDS)
    }

    private fun randomRespawnDelay(): Long {
        val span = MAX_RESPAWN_DELAY_MS - MIN_RESPAWN_DELAY_MS
        if (span <= 0) return MIN_RESPAWN_DELAY_MS
        return MIN_RESPAWN_DELAY_MS + Utils.random(span.toInt())
    }

    @Synchronized
    private fun spawnNewBoss(force: Boolean = false) {
        if (currentBoss != null && !currentBoss!!.hasFinished() && !force) return
        if (force && currentBoss != null) {
            currentBoss!!.markExternallyDespawning("Force-respawned")
            currentBoss!!.finish()
            cleanupMinions()
            currentBoss = null
        }
        val entry = BOSS_ENTRIES.random()
        val locIndex = pickLocIndex()
        val loc = SPAWN_LOCS[locIndex]
        lastSpawnLocIndex = locIndex

        val spawnTile = findValidSpawnTile(loc.tile)

        when (entry) {
            is BossEntry.Single -> {
                val npc = spawnWorldBoss(entry.npcId, spawnTile)
                currentBoss = npc
                announce(npc, loc.name)
            }

            is BossEntry.Group -> {
                val npcs = entry.npcIds.mapIndexed { i, id ->
                    spawnWorldBoss(id, spawnTile.transform(i, 0, spawnTile.plane))
                }
                currentBoss = npcs.first()
                announce(npcs.first(), "${loc.name} with allies!")
            }

            is BossEntry.WithMinions -> {
                val boss = spawnWorldBoss(entry.bossId, spawnTile)
                currentBoss = boss
                currentMinions.clear()

                repeat(entry.minionCount) {
                    val offsetX = Utils.random(-3, 3)
                    val offsetY = Utils.random(-3, 3)
                    val minion = spawnMinion(entry.minionIds.random(), spawnTile.transform(offsetX, offsetY, boss.plane))
                    currentMinions.add(minion)
                }

                announce(boss, "${loc.name} surrounded by minions!")
            }
        }
    }

    private fun spawnWorldBoss(npcId: Int, tile: WorldTile): WorldBossNPC {
        return when {
            npcId == 8133 -> {
                WorldCorporealBeast(tile, IDLE_TIMEOUT_MS, GRACE_PERIOD_MS, this)
            } else -> {
                GenericWorldBossNPC(npcId, tile, IDLE_TIMEOUT_MS, GRACE_PERIOD_MS, this)
            }
        }.apply {
            isForceAgressive = true
            forceAgressiveDistance = 12
            isForceMultiAttacked = true
        }
    }

    private fun spawnMinion(npcId: Int, tile: WorldTile): WorldMinionNPC {
        return GenericWorldMinion(npcId, tile, IDLE_TIMEOUT_MS, GRACE_PERIOD_MS, this).apply {
            isForceAgressive = true
            forceAgressiveDistance = 12
            isForceMultiAttacked = true
        }
    }

    private fun announce(npc: NPC, locationName: String) {
        World.sendWorldMessage(
            "<img=7><col=36648b>News: A ${npc.name} has emerged at $locationName!",
            false
        )
    }

    private fun findValidSpawnTile(base: WorldTile, maxRadius: Int = 20): WorldTile {
        val plane = base.plane
        val baseX = base.x
        val baseY = base.y

        if (isTileWalkable(base)) return base

        for (radius in 1..maxRadius) {
            for (dx in -radius..radius) {
                val x = baseX + dx
                val y1 = baseY - radius
                val y2 = baseY + radius
                if (isTileWalkable(WorldTile(x, y1, plane))) return WorldTile(x, y1, plane)
                if (isTileWalkable(WorldTile(x, y2, plane))) return WorldTile(x, y2, plane)
            }
            for (dy in -radius + 1..radius - 1) {
                val y = baseY + dy
                val x1 = baseX - radius
                val x2 = baseX + radius
                if (isTileWalkable(WorldTile(x1, y, plane))) return WorldTile(x1, y, plane)
                if (isTileWalkable(WorldTile(x2, y, plane))) return WorldTile(x2, y, plane)
            }
        }
        return base
    }

    private fun isTileWalkable(tile: WorldTile): Boolean {
        val dirs = intArrayOf(0, 1, 2, 3)
        return dirs.any { dir -> World.checkWalkStep(tile.plane, tile.x, tile.y, dir, 1) }
    }

    @Synchronized
    private fun pickLocIndex(): Int {
        if (SPAWN_LOCS.size == 1 || !AVOID_SAME_LOCATION_TWICE || lastSpawnLocIndex !in SPAWN_LOCS.indices) {
            return Utils.random(SPAWN_LOCS.size)
        }
        var idx: Int
        do {
            idx = Utils.random(SPAWN_LOCS.size)
        } while (idx == lastSpawnLocIndex)
        return idx
    }

    private fun cleanupMinions() {
        val snapshot = currentMinions.toList() // copy first
        snapshot.forEach { minion ->
            try {
                minion.finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        currentMinions.clear()
    }

    fun onBossReward(boss: WorldBossNPC, player: Player, damage: Int, maxHp: Int) {
        val pct = (damage * 100 / maxHp)
        player.message(Msg.info("You dealt $damage ($pct%) damage and earned loot!"))

        val drops = WorldBossTable.regular.rollDrops(player)
        for (drop in drops) {
            val item = Item(drop.itemId, drop.amount)
            World.updateGroundItem(item, boss.tile, player, 60, 1)

            if (item.isItem("item.magic_chest")) {
                player.message(Msg.rewardRare("You receive a mysterious Magic Chest!"))
                World.sendWorldMessage(Msg.newsEpic("${player.displayName} has received a Magic Chest!"), false)
            } else {
                player.message(Msg.reward("You receive ${item.amount} × ${item.name}."))
                if (item.definitions.tipitPrice >= 1_000_000) {
                    World.sendWorldMessage(
                        Msg.newsRare("${player.displayName} has received ${item.name} from killing the world boss!"),
                        false
                    )
                }
            }
        }
    }

    fun onBossTopDamageReward(boss: WorldBossNPC, player: Player, damage: Int, maxHp: Int) {
        player.message(Msg.topDamager("You were the top damager on ${boss.name} and received an extra reward!"))
        World.sendWorldMessage(Msg.newsEpic("${player.displayName} has received a Magic Chest!"), false)
        World.updateGroundItem(Item("item.magic_chest", 1), boss.tile, player, 60, 1)
    }

    @JvmStatic
    fun openChest(chest: Item, slot: Int, player: Player) {
        val drops = WorldBossTable.chest.rollDrops(player)
        val neededSlots = drops.size
        if (player.inventory.freeSlots < neededSlots) {
            player.message(Msg.warn("You need at least $neededSlots free inventory slots to open the Magic Chest."))
            return
        }

        player.inventory.deleteItem(chest.id, 1)
        for (drop in drops) {
            val item = Item(drop.itemId, drop.amount)
            player.inventory.addItem(item)
            player.message(Msg.chestOpen("Your Magic Chest rewards you with ${item.amount} × ${item.name}!"))

            if (item.definitions.tipitPrice >= 1_000_000) {
                World.sendWorldMessage(
                    Msg.newsRare("${player.displayName} has received ${item.name} from a Magic Chest!"),
                    false
                )
            }
        }
    }



    @Synchronized
    internal fun onBossDeath() {
        cleanupMinions()
        currentBoss = null
        scheduleNextSpawn(randomRespawnDelay())
    }

    @Synchronized
    internal fun onBossFinished(reason: String) {
        cleanupMinions()
        currentBoss = null
        scheduleNextSpawn(randomRespawnDelay())
    }

    @Synchronized
    internal fun onBossIdleDespawn(npc: WorldBossNPC) {
        if (currentBoss == npc) currentBoss = null
        cleanupMinions()
        World.sendWorldMessage(
            "<img=7><col=8a2be2>${npc.name} fades away due to neglect. Another presence stirs...",
            false
        )
        scheduleNextSpawn(randomRespawnDelay())
    }
}
