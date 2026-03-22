package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class NormalPrayersCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to normal prayers."
    override val usage = "::normals"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::normals in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't switch prayer book here.")
            return true
        }
        val isAncientCurses = player.prayer.isAncientCurses
        if (!isAncientCurses) {
            player.message("Your prayerbook is already set to normal prayers.");
            return true;
        }
        player.prayer.setPrayerBook(false)
        return true
    }
}
