package raynna.game.player.command.commands

import raynna.game.World
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command
import raynna.game.player.command.CommandArguments

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
