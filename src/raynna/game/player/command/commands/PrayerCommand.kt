package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class PrayerCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change between regular and curses prayerbook."
    override val usage = "::prayers"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::prayers in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't switch prayer book here.")
            return true
        }
        player.prayer.setPrayerBook(!player.prayer.isAncientCurses)
        return true
    }
}
