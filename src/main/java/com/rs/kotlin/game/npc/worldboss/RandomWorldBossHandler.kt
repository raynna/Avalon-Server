package com.rs.kotlin.game.npc.worldboss

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.core.thread.CoresManager
import com.rs.discord.DiscordAnnouncer.announceGlobalEvent
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.item.Item
import com.rs.java.game.item.ground.GroundItems
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.worldboss.RandomWorldBossHandler.IDLE_TIMEOUT_MS
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

    private sealed class BossEntry {
        data class Single(val npcId: Int) : BossEntry()
        data class Group(val npcIds: List<Int>, val displayName: String) : BossEntry()

        data class WithMinions(
            val bossId: Int,
            val minions: List<Minion>
        ) : BossEntry() {
            data class Minion(val npcId: Int, val count: Int = 1)

            companion object {
                fun of(bossKey: String, vararg minions: Minion): WithMinions =
                    WithMinions(Rscm.lookup(bossKey), minions.toList())

                fun minion(key: String, count: Int = 1): Minion =
                    Minion(Rscm.lookup(key), count)
            }
        }

        companion object {
            fun single(key: String): Single =
                Single(Rscm.lookup(key))

            fun group(vararg keys: String, displayName: String): Group =
                Group(keys.map(Rscm::lookup), displayName)
        }
    }

    private val BOSS_ENTRIES: List<BossEntry> = listOf(
        BossEntry.WithMinions.of(
            bossKey = "npc.general_graardor_lv624",
            BossEntry.WithMinions.minion("npc.sergeant_strongstack_lv141"),
            BossEntry.WithMinions.minion("npc.sergeant_steelwill_lv142"),
            BossEntry.WithMinions.minion("npc.sergeant_grimspike_lv142"),
        ),
        BossEntry.WithMinions.of(
            bossKey = "npc.kree_arra_lv580",
            BossEntry.WithMinions.minion("npc.wingman_skree_lv143"),
            BossEntry.WithMinions.minion("npc.flockleader_geerin_lv149"),
            BossEntry.WithMinions.minion("npc.flight_kilisa_lv159"),
        ),
        BossEntry.WithMinions.of(
            bossKey = "npc.k_ril_tsutsaroth_lv650",
            BossEntry.WithMinions.minion("npc.tstanon_karlak_lv145"),
            BossEntry.WithMinions.minion("npc.zakl_n_gritch_lv142"),
            BossEntry.WithMinions.minion("npc.balfrug_kreeyath_lv151"),
        ),
        BossEntry.WithMinions.of(
            bossKey = "npc.commander_zilyana_lv596",
            BossEntry.WithMinions.minion("npc.starlight_lv149"),
            BossEntry.WithMinions.minion("npc.growler_lv139"),
            BossEntry.WithMinions.minion("npc.bree_lv146"),
        ),
        BossEntry.WithMinions.of(
            bossKey = "npc.king_black_dragon_lv276",
            BossEntry.WithMinions.minion("npc.baby_black_dragon_lv83", 4),
        ),
        BossEntry.WithMinions.of(
            bossKey = "npc.kalphite_queen_lv333",
            BossEntry.WithMinions.minion("npc.kalphite_guardian_lv141", 1),
            BossEntry.WithMinions.minion("npc.kalphite_soldier_lv85", 2),
            BossEntry.WithMinions.minion("npc.kalphite_worker_lv28", 4),
        ),
        BossEntry.WithMinions.of(
            bossKey = "npc.tormented_demon_lv450",
            BossEntry.WithMinions.minion("npc.abyssal_demon_lv124", 4),
        ),
        BossEntry.group("npc.dagannoth_rex_lv303", "npc.dagannoth_supreme_lv303", "npc.dagannoth_prime_lv303", displayName = "Dagannoth Kings"),
        BossEntry.single("npc.chaos_elemental_lv305")
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
        SpawnLocation("Wilderness - Dark Warrior Hills", WorldTile(3100, 3619, 0)),
    )

    private const val MIN_RESPAWN_DELAY_MS: Long = 6 * 60 * 1000L
    private const val MAX_RESPAWN_DELAY_MS: Long = 14 * 60 * 1000L
    private const val IDLE_TIMEOUT_MS: Long = 5 * 60 * 1000L
    private const val GRACE_PERIOD_MS: Long = 30 * 1000L
    private const val AVOID_SAME_LOCATION_TWICE = true

    @Volatile private var currentBosses: MutableList<WorldBossNPC> = mutableListOf()
    @Volatile private var currentMinions: MutableList<WorldMinionNPC> = mutableListOf()
    @Volatile private var nextSpawnTask: ScheduledFuture<*>? = null
    @Volatile private var lastSpawnLocIndex: Int = -1

    @JvmStatic
    @Synchronized
    fun start() {
        if (currentBosses.isEmpty() && nextSpawnTask == null) {
            val delay = randomRespawnDelay()
            World.sendWorldMessage(
                "<img=7><col=36648b>News: The first world boss will spawn in ${formatTime(delay)}!",
                false
            )
            announceGlobalEvent(
                "World boss",
                "The first world boss will spawn in ${formatTime(delay)}!",
                null
            )
            scheduleNextSpawn(delay)
        }
    }

    @Synchronized
    fun forceRespawnNow() {
        cancelPending()
        spawnNewBoss(force = true)
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return buildString {
            if (minutes > 0) append("$minutes minute${if (minutes > 1) "s" else ""}")
            if (minutes > 0 && seconds > 0) append(" and ")
            if (seconds > 0) append("$seconds second${if (seconds > 1) "s" else ""}")
        }
    }

    @Synchronized
    fun forceDespawn(reason: String = "Force-despawned") {
        if (currentBosses.isNotEmpty()) {
            val snapshot = currentBosses.toList() // safe copy
            snapshot.forEach {
                try {
                    it.markExternallyDespawning(reason)
                    it.finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            currentBosses.clear()
        } else {
            scheduleNextSpawn(2000L)
        }
    }

    @Synchronized
    private fun cancelPending() {
        nextSpawnTask?.cancel(false)
        nextSpawnTask = null
    }

    @Synchronized
    private fun scheduleNextSpawn(delayMs: Long) {
        val safeDelay = max(1000L, delayMs)
        if (currentBosses.isNotEmpty() || nextSpawnTask != null) {
            World.sendWorldMessage(
                "<img=7><col=36648b>News: Another world boss will spawn in ${formatTime(safeDelay)}!",
                false
            )
            announceGlobalEvent("World boss", "Another world boss will spawn in ${formatTime(safeDelay)}!", null)
        }
        cancelPending()
        if (safeDelay > 5 * 60 * 1000L) {
            CoresManager.getSlowExecutor().schedule({
                World.sendWorldMessage(
                    "<img=7><col=36648b>News: A world boss will spawn in 5 minutes!",
                    false
                )
                announceGlobalEvent("World boss", "A world boss will spawn in 5 minutes!", null)
            }, safeDelay - 5 * 60 * 1000L, TimeUnit.MILLISECONDS)
        }

        if (safeDelay > 60 * 1000L) {
            CoresManager.getSlowExecutor().schedule({
                World.sendWorldMessage(
                    "<img=7><col=36648b>News: A world boss will spawn in 1 minute!",
                    false
                )
                announceGlobalEvent("World boss", "A world boss will spawn in 1 minute!", null)
            }, safeDelay - 60 * 1000L, TimeUnit.MILLISECONDS)
        }

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
        if (currentBosses.isNotEmpty() && currentBosses.any { !it.hasFinished() } && !force) return
        if (force && currentBosses.isNotEmpty()) {
            currentBosses.forEach { it.markExternallyDespawning("Force-respawned"); it.finish() }
            cleanupMinions()
            currentBosses.clear()
        }

        val entry = BOSS_ENTRIES.random()
        val locIndex = pickLocIndex()
        val loc = SPAWN_LOCS[locIndex]
        lastSpawnLocIndex = locIndex

        val spawnTile = findValidSpawnTile(loc.tile)

        when (entry) {
            is BossEntry.Single -> {
                val npc = spawnWorldBoss(entry.npcId, spawnTile)
                currentBosses.clear()
                currentBosses.add(npc)
                announce(npc, loc.name)
            }

            is BossEntry.Group -> {
                currentBosses.clear()
                val npcs = entry.npcIds.mapIndexed { i, id ->
                    spawnWorldBoss(id, spawnTile.transform(i, 0, spawnTile.plane))
                }
                currentBosses.addAll(npcs)
                if (entry.displayName.isNotEmpty())
                    announceGroup(entry.displayName, loc.name)
                else
                    announce(npcs.first(), "${loc.name} with allies!")
            }

            is BossEntry.WithMinions -> {
                val boss = spawnWorldBoss(entry.bossId, spawnTile)
                currentBosses.clear()
                currentBosses.add(boss)
                currentMinions.clear()

                val usedOffsets = mutableSetOf<Pair<Int, Int>>()

                for (minion in entry.minions) {
                    repeat(minion.count) {
                        var offset: Pair<Int, Int>
                        var tries = 0
                        do {
                            offset = Utils.random(-3, 3) to Utils.random(-3, 3)
                            tries++
                        } while ((offset.first == 0 && offset.second == 0 || offset in usedOffsets) && tries < 20)

                        usedOffsets.add(offset)

                        val (dx, dy) = offset
                        val tile = spawnTile.transform(dx, dy, boss.plane)
                        val npc = spawnMinion(minion.npcId, tile)
                        currentMinions.add(npc)
                    }
                }

                announce(boss, "${loc.name} surrounded by minions!")
            }
        }
    }



    private fun spawnWorldBoss(npcId: Int, tile: WorldTile): WorldBossNPC {
        return when (npcId) {
            8349 -> {
                TormentedDemonWorldBoss(tile, IDLE_TIMEOUT_MS, GRACE_PERIOD_MS, this)
            }
            8133 -> {
                WorldCorporealBeast(tile, IDLE_TIMEOUT_MS, GRACE_PERIOD_MS, this)
            }
            1158 -> {
                KalphiteQueenWorldBoss(tile, IDLE_TIMEOUT_MS, GRACE_PERIOD_MS, this)
            }
            else -> {
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

    private fun announceGroup(displayName: String, locationName: String) {
        World.sendWorldMessage(
            "<img=7><col=36648b>News: $displayName have emerged at $locationName!",
            false
        )
        announceGlobalEvent(
            "World boss",
            "$displayName have emerged at $locationName!",
            null
        )
    }

    private fun announce(npc: NPC, locationName: String) {
        World.sendWorldMessage(
            "<img=7><col=36648b>News: A ${npc.name} has emerged at $locationName!",
            false
        )
        announceGlobalEvent(
            "World boss",
            "A ${npc.name} has emerged at $locationName!",
            null
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
        val snapshot = currentMinions.toList()
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

        val drops = WorldBossTable.regular.rollDrops(player, boss.combatLevel, boss.dropRateMultiplier)
        for (drop in drops) {
            val item = Item(drop.itemId, drop.amount)
            if (item.isItem("item.magic_chest")) {
                player.message(Msg.rewardRare("You receive a mysterious Magic Chest!"))
                Msg.world(Msg.PURPLE, "${player.displayName} has received a Magic Chest!")
            } else {
                player.message(Msg.reward("You receive ${item.amount} x ${item.name}."))
                if (item.definitions.tipitPrice >= 1_000_000) {
                    Msg.world(Msg.RED, "${player.displayName} has received ${item.name} from killing the world boss!")
                }
            }
            GroundItems.updateGroundItem(item, boss.tile, player, 60, 1)
        }
    }

    fun onBossTopDamageReward(boss: WorldBossNPC, player: Player, damage: Int, maxHp: Int) {
        player.message(Msg.topDamager("You were the top damager on ${boss.name} and received an extra reward!"))
        Msg.world(Msg.ORANGE, "${player.displayName} has received a Magic Chest!")
        GroundItems.updateGroundItem(Item("item.magic_chest", 1), boss.tile, player, 60, 1)
    }

    @JvmStatic
    fun openChest(chest: Item, slot: Int, player: Player) {
        val drops = WorldBossTable.chest.rollDrops(player, 0)
        for (drop in drops) {
            val item = Item(drop.itemId, drop.amount)
            if (!player.inventory.canHold(item, item.amount)) {
                player.message(Msg.warn("You don’t have enough inventory space to open the Magic Chest."))
                return
            }
        }

        player.inventory.deleteItem(chest.id, 1)
        for (drop in drops) {
            val item = Item(drop.itemId, drop.amount)
            if (item.id == 995) {
                player.message(Msg.chestOpen("Your Magic Chest rewards you with ${Utils.formatAmount(item.amount.toLong())} x ${item.name}!"))
                player.moneyPouch.addMoney(item.amount, false)
                continue
            }
            player.inventory.addItem(item)
            player.message(Msg.chestOpen("Your Magic Chest rewards you with ${Utils.formatAmount(item.amount.toLong())} x ${item.name}!"))
            val price = item.definitions.tipitPrice
            if (price in 5_000_000..24_999_999) {
                Msg.world(Msg.RED, "${player.displayName} has received ${item.name} from a Magic Chest!")
            }
            if (item.definitions.tipitPrice >= 25_000_000) {
                Msg.world(Msg.PURPLE, "${player.displayName} has received ${item.name} from a Magic Chest!")
            }
        }
    }



    @Synchronized
    internal fun onBossDeath(boss: WorldBossNPC) {
        currentBosses.remove(boss)
        val remaining = currentBosses.size
        if (remaining > 0) {
            val groupName = boss.name
                .replace("prime", "Kings")
                .replace("supreme", "Kings")
                .replace("rex", "Kings")

            World.sendWorldMessage(
                "<img=7><col=ff0000>News: One of the $groupName has been slain! $remaining remaining...",
                false
            )
            announceGlobalEvent(
                "World boss",
                "One of the $groupName has been slain! $remaining remaining...",
                null
            )
            return
        }
        if (currentBosses.isEmpty()) {
            cleanupMinions()
            World.sendWorldMessage(
                "<img=7><col=ff0000>News: World boss has been slain!",
                false
            )
            announceGlobalEvent(
                "World boss",
                "World boss has been slain!",
                null
            )
            val totalDamage: MutableMap<Player, Int> = mutableMapOf()
            val allBosses = listOf(boss) // include this boss
            allBosses.forEach { b ->
                for ((p, dmg) in b.getDamageMap()) {
                    if (p.hasFinished()) continue
                    totalDamage.merge(p, dmg, Int::plus)
                }
            }

            if (totalDamage.isNotEmpty()) {
                val maxHpSum = allBosses.sumOf { it.maxHitpoints }
                val threshold = (maxHpSum * 0.10).toInt()

                var topPlayer: Player? = null
                var topDamage = 0

                for ((player, damage) in totalDamage) {
                    if (damage > topDamage) {
                        topDamage = damage
                        topPlayer = player
                    }
                    if (damage >= threshold) {
                        onBossReward(boss, player, damage, maxHpSum)
                    }
                }

                topPlayer?.let {
                    onBossTopDamageReward(boss, it, topDamage, maxHpSum)
                }
            }

            scheduleNextSpawn(randomRespawnDelay())
        }
    }


    @Synchronized
    fun onMinionDeath(minion: WorldMinionNPC) {
        currentMinions.remove(minion)
        if (isBossDead()) {
            return
        }

        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                if (!isBossDead()) {
                    val newMinion = spawnMinion(minion.id, minion.tile)
                    currentMinions.add(newMinion)
                }
                stop()
            }
        }, 50)
    }

    fun isBossDead(): Boolean {
        return currentBosses.isEmpty() || currentBosses.all { it.hasFinished() }
    }

    @Synchronized
    internal fun onBossFinished(reason: String) {
        val bossesToFinish = currentBosses.toList() // snapshot
        bossesToFinish.forEach {
            it.markExternallyDespawning(reason)
            it.finish()
        }
        currentBosses.clear()
        cleanupMinions()
        scheduleNextSpawn(randomRespawnDelay())
    }

    @Synchronized
    internal fun onBossIdleDespawn(npc: WorldBossNPC) {
        currentBosses.remove(npc)
        if (currentBosses.isEmpty()) {
            cleanupMinions()
            World.sendWorldMessage(
                "<img=7><col=8a2be2>${npc.name} fades away due to neglect. Another presence stirs...",
                false
            )
            announceGlobalEvent(
                "World boss",
                "${npc.name} fades away due to neglect. Another presence stirs...",
                null
            )
            scheduleNextSpawn(randomRespawnDelay())
        }
    }
}
