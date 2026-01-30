package com.rs.kotlin.game.world.activity.pvpgame.tournament

import com.rs.java.game.player.Player
import com.rs.Settings
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Skills
import com.rs.kotlin.game.player.command.CommandRegistry
import com.rs.kotlin.game.world.activity.pvpgame.closePvPOverlay

object TournamentRecovery {

    fun restore(player: Player) {
        player.closePvPOverlay()
        player.interfaceManager.closeOverlay(false);
        player.tempInventory?.let {
            player.inventory.restoreSnapshot(it)
        }

        player.tempEquipment?.let {
            player.equipment.restoreSnapshot(it)
        }

        if (player.tempXpSnapshot != null && player.tempLevelSnapshot != null) {
            for (i in 0 until 25) {
                player.skills.setXp(i, player.tempXpSnapshot[i])
                player.skills.set(i, player.tempLevelSnapshot[i].toInt())
                player.skills.refresh(i)
            }
        }
        player.tempWorldTile?.let {
            player.nextWorldTile = it
        }

        player.tempInventory = null
        player.tempEquipment = null
        player.tempXpSnapshot = null
        player.tempLevelSnapshot = null
        player.tempWorldTile = null

        player.prayer.reset()
        player.appearence.generateAppearenceData()
        player.activeTournament = null
        player.prayer.restorePrayer(player.skills.getLevelForXp(Skills.PRAYER) * 10)
        if (player.poison.isPoisoned) player.poison.reset()
        if (player.newPoison.isPoisoned()) player.newPoison.reset()
        player.setRunEnergy(100)
        player.heal(player.maxHitpoints)
        player.skills.restoreSkills()
        player.getAppearence().generateAppearenceData()
        player.skills[Skills.SUMMONING] = player.skills.getLevelForXp(Skills.SUMMONING)
        player.skills.refresh(Skills.SUMMONING)
        player.getCombatDefinitions().resetSpecialAttack()
        player.message("Your character has been safely restored.")
    }
}
