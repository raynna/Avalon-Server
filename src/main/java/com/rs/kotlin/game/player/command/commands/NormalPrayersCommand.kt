package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class NormalPrayersCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to normal prayers."
    override val usage = "::normals"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::normals in this mode.")
            return true
        }

        if (args.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }
        val isAncientCurses = player.prayer.isAncientCurses
        if (!isAncientCurses) {
            player.message("Your prayerbook is already set to normal prayers.");
            return true;
        }
        player.prayer.setPrayerBook(false)
        return true
    }
}
