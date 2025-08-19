package com.rs.kotlin.game.player.interfaces

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.game.player.TickManager
import com.rs.java.utils.HexColours
import kotlin.math.ceil
import kotlin.math.roundToInt

class HealthOverlay {

    fun sendOverlay(player: Player, target: Entity) {
        checkCombatLevel(player, target)
        updateHealthOverlay(player, target, false)
        if (player.toggles("HEALTHBAR", false) && (!player.interfaceManager.containsTab(getHealthOverlayId(player)))) {
            player.interfaceManager.sendTab(getHealthOverlayId(player), 3037)
            val pixels: Int = (target.hitpoints.toDouble() / target.getMaxHitpoints() * 126.0).roundToInt()
            player.packets.sendRunScript(6252, pixels)
            player.packets.sendRunScript(6253, 0)
            target.temporaryAttribute()["last_hp_${player.index}"] = target.hitpoints
        }
    }

    private fun checkForClose(player: Player): Boolean {
        if (player.isDead || player.temporaryTarget == null || player.temporaryTarget.isDead) {
            return true
        }
        if (!player.temporaryTarget.withinDistance(player.temporaryTarget, 32)) {
            return true
        }
        if (!player.tickManager.isActive(TickManager.TickKeys.LAST_ATTACK_TICK)) {
            return true
        }
        return false
    }

    fun closeOverlay(player: Player) {
        if (player.interfaceManager.containsTab(getHealthOverlayId(player))) {
            if (checkForClose(player) && player.interfaceManager.containsTab(getHealthOverlayId(player))) {
                player.removeTemporaryTarget()
                player.interfaceManager.closeTab(player.interfaceManager.isResizableScreen, getHealthOverlayId(player))
            }
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
            val maxHp = target.getMaxHitpoints().toDouble()
            val currentHp = target.hitpoints
            val lastHp = target.temporaryAttribute()["last_hp_${player.index}"] as? Int ?: currentHp

            val damageTaken = (lastHp - currentHp).coerceAtLeast(0)

            val newPixels = (currentHp.toDouble() / maxHp * 126.0).roundToInt()
            val oldPixels = (lastHp.toDouble() / maxHp * 126.0).roundToInt()

            val pixelLoss = ((damageTaken.toDouble() / maxHp) * 126.0).roundToInt()

            val adjustedPixels = if (damageTaken > 0) {
                (oldPixels - pixelLoss).coerceAtLeast(newPixels).coerceAtLeast(0)
            } else {
                newPixels
            }

            player.packets.sendRunScript(6252, adjustedPixels)
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    player.packets.sendRunScript(6253, adjustedPixels)
                    stop()
                }
            }, 0, 1)
            target.temporaryAttribute()["last_hp_${player.index}"] = currentHp
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
            "${ceil(currentHp / 10.0).toInt()}/${ceil(maxHp / 10.0).toInt()}"
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