package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.Skills
import raynna.game.player.command.Command
import raynna.game.player.command.CommandArguments

class SaveCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Saves a gear preset"
    override val usage = "::save <name>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::save in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::save here.")
            return true
        }
        val cmdArgs = CommandArguments(args)

        val name = cmdArgs.getJoinedString(0)
        player.presetManager.savePreset(name)
        return true
    }
}
