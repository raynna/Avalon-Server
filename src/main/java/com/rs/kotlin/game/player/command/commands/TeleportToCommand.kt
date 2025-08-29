package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.World
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandArguments

class TeleportToCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Set a level for a skill"
    override val usage = "::setlevel <id> <level>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        val cmdArgs = CommandArguments(args)

        val username = cmdArgs.getString(0)
        val target = World.getPlayerByDisplayName(username)
        if (target == null) {
            player.message("Couldnt find player with name $username")
            return false
        }
        player.nextWorldTile = target
        return true

    }
}
