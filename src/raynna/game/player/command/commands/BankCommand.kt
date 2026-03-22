package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class BankCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Opens your bank"
    override val usage = "::bank"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::bank in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::bank here.")
            return true
        }
        player.getBank().openBank()
        return true
    }
}
