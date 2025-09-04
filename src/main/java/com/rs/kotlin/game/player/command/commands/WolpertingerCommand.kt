package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.actions.skills.summoning.Summoning
import com.rs.kotlin.game.player.command.Command

class WolpertingerCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Spawns you a wolpertinger."
    override val usage = "::wolpertinger"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::wolpertinger in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::wolpertinger here.")
            return true
        }
        Summoning.spawnFamiliar(player, Summoning.Pouch.WOLPERTINGER);
        return true
    }
}
