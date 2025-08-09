package com.rs.kotlin.game.player.interfaces

import com.rs.java.game.Entity
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.HexColours

class HealthOverlay {
    fun getHealthOverlayId(player: Player): Int {
        return if (player.interfaceManager.isResizableScreen) 1 else 30
    }

    fun updateHealthOverlay(player: Entity, target: Entity) {
        val p1 = player as Player

        when (target) {
            is Player -> {
                checkCombatLevel(p1, target)

                val name = buildTargetName(target)
                p1.packets.sendTextOnComponent(3037, 6, name)

                val hpText = buildHpText(p1, target.hitpoints, target.maxHitpoints)
                p1.packets.sendTextOnComponent(3037, 7, hpText)
            }

            is NPC -> {
                p1.packets.sendTextOnComponent(3037, 6, target.name)

                checkCombatLevel(p1, target)

                val hpText = buildHpText(p1, target.hitpoints, target.maxHitpoints)
                p1.packets.sendTextOnComponent(3037, 7, hpText)
            }
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