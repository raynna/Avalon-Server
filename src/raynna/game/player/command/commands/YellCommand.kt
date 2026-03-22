package raynna.game.player.command.commands

import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command
import raynna.game.player.content.ServerMessage
import raynna.util.Utils

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
