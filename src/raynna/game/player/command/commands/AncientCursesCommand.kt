package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class AncientCursesCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to ancient curses."
    override val usage = "::curses"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::curses in this mode.")
            return true
        }
        val isAncientCurses = player.prayer.isAncientCurses
        if (isAncientCurses) {
            player.message("Your prayerbook is already set to ancient curses.");
            return true;
        }
        player.prayer.setPrayerBook(true)
        return true
    }
}
