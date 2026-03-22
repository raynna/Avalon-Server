package raynna.game.player.command.commands

import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command
import raynna.game.player.command.CommandArguments
import raynna.game.world.activity.pvpgame.*
import raynna.game.world.activity.pvpgame.tournament.TournamentScheduler

class TournamentCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Joins an active tournament."
    override val usage = "::tournament"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        val instance = TournamentScheduler.getInstance()
        if (instance == null) {
            player.message("No active tournament right now.")
        } else {
            if (player.familiar != null) {
                player.message("You should probably dismiss your familiar before joining.")
                return true;
            }
            instance.addPlayer(player)
            player.message("Joined current tournament lobby.")
        }
        return true
    }
}
