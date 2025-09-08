package com.rs.kotlin.game.world.activity

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.*
import com.rs.java.game.item.Item
import com.rs.java.game.item.ItemId
import com.rs.java.game.npc.others.BarrowsBrother
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.actions.combat.Magic
import com.rs.java.utils.Utils
import com.rs.kotlin.game.world.area.Area

class BarrowsArea : Area(14231, 14131) {//regions

    enum class Hills(val outBound: WorldTile, val inside: WorldTile) {
        AHRIM_HILL(WorldTile(3566, 3288, 0), WorldTile(3557, 9703, 3)),
        DHAROK_HILL(WorldTile(3575, 3297, 0), WorldTile(3556, 9718, 3)),
        GUTHAN_HILL(WorldTile(3576, 3280, 0), WorldTile(3534, 9704, 3)),
        KARIL_HILL(WorldTile(3565, 3277, 0), WorldTile(3546, 9684, 3)),
        TORAG_HILL(WorldTile(3555, 3282, 0), WorldTile(3568, 9683, 3)),
        VERAC_HILL(WorldTile(3558, 3297, 0), WorldTile(3578, 9706, 3));
    }

    override fun update(): Area = this
    override fun name(): String = "Barrows"
    override fun member(): Boolean = false
    override fun environment(): Environment = Environment.MINIGAME

    override fun onEnter(player: Player) {
        if (player.hiddenBrother == -1) {
            player.hiddenBrother = Utils.random(6)
        }
        //player.packets.sendBlackOut(2)
        player.interfaceManager.sendOverlay(24, player.interfaceManager.hasRezizableScreen())
        resetHeadTimer(player)
        for (varbit in TUNNEL_CONFIG[player.tunnelIndex]) {
            player.varsManager.sendVarBit(varbit.toInt(), 1)
        }
        player.varsManager.sendVarBit(467, 1)
    }

    override fun onExit(player: Player) {
        player.packets.sendBlackOut(0)
        player.interfaceManager.closeOverlay(player.interfaceManager.hasRezizableScreen())
        player.packets.sendStopCameraShake()
        player.barrowsTarget?.finish()
        player.barrowsTarget = null
    }

    override fun onMoved(player: Player) {
        if (!player.interfaceManager.containsInterface(24)) {
            player.interfaceManager.sendOverlay(24, player.interfaceManager.hasRezizableScreen())
            player.refreshBarrowsBrothers()
        }
    }

    override fun onTick(player: Player) {
        var timer = player.barrowsTimer
        if (timer != null) {
            if (timer > 0) {
                player.barrowsTimer = timer - 1
                if (player.barrowsTimer == 0) resetHeadTimer(player)
                return
            }
        }

        if (player.hiddenBrother == -1) {
            player.applyHit(Hit(player, Utils.random(50) + 1, com.rs.java.game.Hit.HitLook.REGULAR_DAMAGE))
            resetHeadTimer(player)
            return
        }

        val headIndex = player.getAndIncreaseHeadIndex()
        if (headIndex == -1) {
            resetHeadTimer(player)
            return
        }

        player.packets.sendGlobalVar(1043, 0)
        player.headComponentId = 9 + Utils.random(10)
        player.packets.sendGlobalVar(1043, 4761 + headIndex)

        // drain prayer
        val activeLevel = player.prayer.prayerPoints
        if (activeLevel > 0) {
            val level = player.skills.getLevelForXp(Skills.PRAYER) * 10
            player.prayer.drainPrayer(level / 6)
        }
        player.barrowsTimer = 3
    }

    override fun onObjectClick(player: Player, obj: WorldObject): Boolean {
        return when (obj.id) {
            in 6702..6707 -> { // crypt exit ladders
                val out = Hills.entries[obj.id - 6702].outBound
                player.nextWorldTile = WorldTile(out.x + 1, out.y + 1, out.plane)
                onLeaveCrypt(player)
                true
            }

            in 66115..66116 -> {
                for (hill in Hills.entries) {
                    if (Utils.inCircle(player.tile, hill.outBound.tile, 5)) {
                        player.useStairs(-1, hill.inside, 1, 2, "You've broken into a crypt.")
                        WorldTasksManager.schedule(object : WorldTask() {
                            override fun run() {
                                onEnterCrypt(player)
                            }
                        })
                    }
                }
                true
            }

            10284 -> { // reward chest
                if (player.hiddenBrother != -1 && !player.killedBarrowBrothers[player.hiddenBrother]) {
                    spawnBrother(player, 2025 + player.hiddenBrother, obj.tile)
                } else {
                    if (!player.inventory.hasFreeSlots()) {
                        player.packets.sendGameMessage("You don't have any inventory space.")
                        return true
                    }
                    Magic.sendNormalTeleportSpell(player, 0, 0.0, WorldTile(3565, 3306, 0))
                    if (player.isTeleportBlocked)
                        return true
                    WorldTasksManager.schedule(object : WorldTask() {
                        override fun run() {
                            sendReward(player)
                            player.resetBarrows()
                            player.refreshBarrowsBrothers()
                        }
                    }, 5)
                    //player.packets.sendCameraShake(3, 12, 25, 12, 25)
                    //player.packets.sendSpawnedObject(WorldObject(6775, 10, 0, 3551, 9695, 0))
                }
                true
            }

            in 6716..6749 -> { // tunnels
                val walkTo = when (obj.rotation) { // FIX: use WorldObject.rotation
                    0 -> WorldTile(obj.x + 5, obj.y, 0)
                    1 -> WorldTile(obj.x, obj.y - 5, 0)
                    2 -> WorldTile(obj.x - 5, obj.y, 0)
                    else -> WorldTile(obj.x, obj.y + 5, 0)
                }
                if (!World.isNotCliped(walkTo.plane, walkTo.x, walkTo.y, 1)) return true
                player.addWalkSteps(walkTo.x, walkTo.y, -1, false)
                player.lock(6)
                if (player.hiddenBrother != -1) {
                    val bro = player.getRandomBrother()
                    if (bro != -1) spawnBrother(player, 2025 + bro, walkTo)
                }
                true
            }

            else -> {
                val sarco = getSarcophagusId(obj.id)
                if (sarco != -1) {
                    if (sarco == player.hiddenBrother) {
                        player.dialogueManager.startDialogue("BarrowsD")
                    } else if (player.barrowsTarget != null || player.killedBarrowBrothers[sarco]) {
                        player.packets.sendGameMessage("You found nothing.")
                    } else {
                        spawnBrother(player, 2025 + sarco, player.tile)
                    }
                    true
                } else false
            }
        }
    }


    override fun onNPCKill(player: Player, npcId: Int) {
        if (npcId in CRYPT_NPCS) {
            player.setBarrowsKillCount(player.barrowsKillCount++)
            sendCreaturesSlainCount(player, player.barrowsKillCount)
        }
    }

    override fun onDeath(player: Player): Boolean {
        onExit(player)
        return true
    }

    override fun onTeleport(player: Player) {
        player.packets.sendStopCameraShake()
        onExit(player)
    }

    private fun onEnterCrypt(player: Player) {
        player.packets.sendBlackOut(2)
    }

    private fun onLeaveCrypt(player: Player) {
        player.packets.sendBlackOut(0)
    }

    private fun spawnBrother(player: Player, id: Int, tile: WorldTile) {
        player.barrowsTarget?.disapear()
        for (i in 0..<10) {
            val dir = Utils.random(Utils.DIRECTION_DELTA_X.size)
            val newX = player.x + Utils.DIRECTION_DELTA_X[dir]
            val newY = player.y + Utils.DIRECTION_DELTA_Y[dir]

            if (World.checkWalkStep(player.plane, newX, newY, dir, 1)) {
                val brother = BarrowsBrother(id, WorldTile(newX, newY, player.plane), player)
                brother.setTarget(player)
                brother.nextForceTalk = ForceTalk("You dare disturb my rest!")
                player.barrowsTarget = brother
                player.hintIconsManager.addHintIcon(brother, 1, -1, false)
                break
            }
        }
    }

    private fun resetHeadTimer(player: Player) {
        player.barrowsTimer = 25 + Utils.random(6)
    }

    private fun sendReward(player: Player) {
        val killed = player.killedBarrowBrothers.count { it }

        val odds = when (killed) {
            6 -> 5
            4 -> 7
            3 -> 9
            2 -> 11
            1 -> 13
            else -> -1
        }

        if (odds > 0 && Utils.roll(1, odds)) {
            val casket = Item(405)
            if (player.inventory.hasFreeSlots())
                player.inventory.addItem(casket.id, 1)
            else
                World.updateGroundItem(casket, player, player, 60, 1)

            player.packets.sendGameMessage("You received a barrows casket.")
        }

        repeat(10) {
            if (Utils.random(100) < killed * 10) { // e.g. more kills = more chance
                val reward = COMMON_REWARDS.random()
                if (player.inventory.hasFreeSlots())
                    player.inventory.addItem(reward.id, reward.amount)
                else
                    World.updateGroundItem(reward, player, player, 60, 1)
            }
        }

        // always some coins
        val randomCoins = Utils.random(25_000, 150_000)
        if (player.inventory.hasFreeSlots())
            player.inventory.addItem(995, randomCoins)
        else
            World.updateGroundItem(Item(995, randomCoins), player, player, 60, 1)
        player.teleportBlock(60)
        player.packets.sendBlackOut(0)
    }


    private fun sendCreaturesSlainCount(player: Player, count: Int) {
        player.packets.sendTextOnComponent(24, 6, Utils.format(count))
        player.packets.sendVarBit(464, count)
    }

    private fun getSarcophagusId(objectId: Int): Int = when (objectId) {
        66017 -> 0
        63177 -> 1
        66020 -> 2
        66018 -> 3
        66019 -> 4
        66016 -> 5
        else -> -1
    }

    companion object {
        private val CRYPT_NPCS = intArrayOf(
            1243, 1244, 1245, 1246, 1247,
            1618, 2031, 2032, 2033, 2034, 2035, 2036, 2037,
            4920, 4921, 5381, 5422, 7637
        )

        private val COMMON_REWARDS = arrayOf(
            Item(ItemId.MIND_RUNE, Utils.random(850)),
            Item(ItemId.CHAOS_RUNE, Utils.random(850)),
            Item(ItemId.DEATH_RUNE, Utils.random(600)),
            Item(ItemId.BLOOD_RUNE, Utils.random(850)),
            Item(ItemId.BOLT_RACK_4740, Utils.random(850))
        )

        private val TUNNEL_CONFIG = arrayOf(
            shortArrayOf(470, 479, 482, 476, 474),
            shortArrayOf(479, 477, 478, 480, 472),
            shortArrayOf(477, 471, 472, 476, 475, 478, 480, 477)
        )
    }
}

// -------------------- Player helpers --------------------

fun Player.getRandomBrother(): Int {
    val bros = (0..5).filter { !killedBarrowBrothers[it] && hiddenBrother != it }
    return if (bros.isEmpty()) -1 else bros.random()
}

fun Player.getAndIncreaseHeadIndex(): Int {
    val map = temporaryAttribute() ?: return -1
    val current = (map["BarrowsHead"] as? Int) ?: 0
    val next = if (current >= killedBarrowBrothers.size - 1) 0 else current + 1
    map["BarrowsHead"] = next
    return if (killedBarrowBrothers[current]) current else -1
}

var Player.headComponentId: Int
    get() = (temporaryAttribute()?.get("headComponentId") as? Int) ?: 0
    set(value) {
        temporaryAttribute()?.set("headComponentId", value)
    }

var Player.tunnelIndex: Int
    get() = (temporaryAttribute()?.get("tunnelIndex") as? Int) ?: 0
    set(value) {
        temporaryAttribute()?.set("tunnelIndex", value)
    }

// Track the active spawned Barrows Brother
var Player.barrowsTarget: BarrowsBrother?
    get() = temporaryAttribute()?.get("barrowsTarget") as? BarrowsBrother
    set(value) {
        val map = temporaryAttribute() ?: return
        if (value == null) map.remove("barrowsTarget")
        else map["barrowsTarget"] = value
    }

var Player.barrowsTimer: Int
    get() = (temporaryAttribute()?.get("barrowsTimer") as? Int) ?: 0
    set(value) {
        temporaryAttribute()?.set("barrowsTimer", value)
    }

fun Player.clearBarrowsTarget(brother: BarrowsBrother) {
    if (barrowsTarget == brother) {
        barrowsTarget = null
        hintIconsManager.removeUnsavedHintIcon()
    }
}

fun Player.refreshBarrowsBrothers() {
    for (i in killedBarrowBrothers.indices) {
        packets.sendVarBit(457 + i, if (killedBarrowBrothers[i]) 1 else 0)
    }
}

fun Player.markBrotherSlain(id: Int) {
    val index = id - 2025
    if (index in killedBarrowBrothers.indices) {
        killedBarrowBrothers[index] = true
        packets.sendVarBit(457 + index, 1)
    }
}

fun digIntoHill(player: Player): Boolean {
    for (hill in BarrowsArea.Hills.entries) {
        if (player.tile.withinDistance(hill.outBound, 2)) {
            player.nextWorldTile = hill.inside
            player.packets.sendGameMessage("You dig a hole and fall into a crypt!")
            return true
        }
    }
    return false
}
