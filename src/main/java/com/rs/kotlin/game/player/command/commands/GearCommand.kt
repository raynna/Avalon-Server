package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.Skills
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandArguments

class GearCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Spawn a gear preset"
    override val usage = "::gear <name>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::gear in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::gear here.")
            return true
        }
        val cmdArgs = CommandArguments(args)

        val name = cmdArgs.getJoinedString(0)
        player.presetManager.loadPreset(name, null)
        return true
    }
}
