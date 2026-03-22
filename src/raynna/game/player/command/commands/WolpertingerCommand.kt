package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.actions.skills.summoning.Summoning
import raynna.game.player.command.Command

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
