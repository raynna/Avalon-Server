package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class PrayerCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change between regular and curses prayerbook."
    override val usage = "::prayers"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::prayers in this mode.")
            return true
        }

        if (args.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }
        player.prayer.setPrayerBook(!player.prayer.isAncientCurses)
        return true
    }
}
