package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command
import com.rs.java.game.player.content.ServerMessage
import com.rs.java.utils.Utils

class YellCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Yell a message to the whole world."
    override val usage = "::yell <message>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (args.isEmpty()) {
            player.message("You must type a message to yell.")
            return true
        }

        val message = args.joinToString(" ")
        ServerMessage.filterMessage(player, Utils.fixChatMessage(message), false)
        return true
    }
}
