package com.rs.kotlin.game.player.interfaces

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Keys
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.HexColours

class HealthOverlay {

    fun sendOverlay(player: Player, target: Entity) {
        checkCombatLevel(player, target)
        updateHealthOverlay(player, target, false)
        if (player.toggles("HEALTHBAR", false) && (!player.interfaceManager.containsTab(getHealthOverlayId(player)))) {
            player.message("sent tab")
            player.interfaceManager.sendTab(getHealthOverlayId(player), 3037)
            val pixels: Int = (target.hitpoints.toDouble() / target.getMaxHitpoints() * 126.0).toInt()
            player.packets.sendRunScript(6252, pixels)
            player.packets.sendRunScript(6253, 0)
        }
    }

    private fun checkForClose(player: Player): Boolean {
        if (player.isDead || player.temporaryTarget == null || player.temporaryTarget.isDead) {
            player.message("target or player is dead");
            return true
        }
        if (!player.temporaryTarget.withinDistance(player.temporaryTarget, 32)) {
            player.message("target isnt in range of player");
            return true
        }
        if (player.tickTimers.getOrDefault(Keys.IntKey.LAST_ATTACK_TICK, 0) <= 0) {
            player.message("lastAttack tick is 0")
            return true
        }
        return false
    }

    fun closeOverlay(player: Player) {
        if (checkForClose(player) && player.interfaceManager.containsTab(getHealthOverlayId(player))) {
            player.message("closing overlay");
            player.removeTemporaryTarget()
            player.interfaceManager.closeTab(player.interfaceManager.isResizableScreen, getHealthOverlayId(player))
        }
    }

    fun getHealthOverlayId(player: Player): Int {
        return if (player.interfaceManager.isResizableScreen) 1 else 30
    }

    fun updateHealthOverlay(player: Player, target: Entity, updateScript: Boolean) {
        when (target) {
            is Player -> {
                checkCombatLevel(player, target)

                val name = buildTargetName(target)
                player.packets.sendTextOnComponent(3037, 6, name)

                val hpText = buildHpText(player, target.hitpoints, target.maxHitpoints)
                player.packets.sendTextOnComponent(3037, 7, hpText)
            }

            is NPC -> {
                player.packets.sendTextOnComponent(3037, 6, target.name)

                checkCombatLevel(player, target)

                val hpText = buildHpText(player, target.hitpoints, target.maxHitpoints)
                player.packets.sendTextOnComponent(3037, 7, hpText)
            }
        }
        if (updateScript) {
            val pixels: Int = (target.hitpoints.toDouble() / target.getMaxHitpoints() * 126.0).toInt()
            player.packets.sendRunScript(6252, pixels)
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    player.packets.sendRunScript(6253, pixels)
                    stop()
                }
            }, 0, 1)
        }
    }

    private fun buildTargetName(target: Player): String {
        return buildString {
            if (target.appearence.title != -1) {
                append(target.appearence.titleName)
            }
            append(target.displayName)
        }
    }

    private fun buildHpText(viewer: Player, currentHp: Int, maxHp: Int): String {
        return if (viewer.toggles("ONEXHITS", false)) {
            "${currentHp / 10}/${maxHp / 10}"
        } else {
            "$currentHp/$maxHp"
        }
    }

    fun checkCombatLevel(player: Player, target: Entity) {
        when (target) {
            is NPC -> checkNpcCombatLevel(player, target)
            is Player -> checkPlayerCombatLevel(player, target)
        }
    }

    private fun checkNpcCombatLevel(player: Player, npc: NPC) {
        val playerLevel = getRelevantCombatLevel(player)
        val npcLevel = npc.combatLevel

        player.packets.sendHideIComponent(3037, 8, npcLevel == 0)
        player.packets.sendHideIComponent(3037, 9, npcLevel == 0)

        val levelText = "Level: ${getLevelColour(playerLevel, npcLevel)}$npcLevel"
        player.packets.sendTextOnComponent(3037, 9, levelText)
    }

    private fun checkPlayerCombatLevel(player: Player, target: Player) {
        val playerLevel = getRelevantCombatLevel(player)
        val targetLevel = target.skills.combatLevel

        player.packets.sendHideIComponent(3037, 8, false)
        player.packets.sendHideIComponent(3037, 9, false)

        val levelDisplay = if (player.isAtWild || player.isAtPvP) {
            "$targetLevel+${target.skills.summoningCombatLevel}"
        } else {
            "${target.skills.combatLevelWithSummoning}"
        }

        val levelText = "Lvl: ${getLevelColour(playerLevel, targetLevel)}$levelDisplay"
        player.packets.sendTextOnComponent(3037, 9, levelText)
    }

    private fun getRelevantCombatLevel(player: Player): Int {
        return if (player.isAtWild) {
            player.skills.combatLevel
        } else {
            player.skills.combatLevelWithSummoning
        }
    }

    private fun getLevelColour(playerLevel: Int, targetLevel: Int): String {
        return when {
            playerLevel > targetLevel -> HexColours.Colour.GREEN.hex
            playerLevel == targetLevel -> HexColours.Colour.YELLOW.hex
            else -> HexColours.Colour.RED.hex
        }
    }

}