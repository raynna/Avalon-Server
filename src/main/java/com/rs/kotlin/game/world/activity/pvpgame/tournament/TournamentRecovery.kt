package com.rs.kotlin.game.world.activity.pvpgame.tournament

import com.rs.java.game.player.Player
import com.rs.Settings
import com.rs.java.game.WorldTile
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

        CommandRegistry.execute(player, "heal")
        player.message("Your character has been safely restored.")
    }
}
