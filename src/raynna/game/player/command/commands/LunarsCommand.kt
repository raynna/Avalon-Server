package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class LunarsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to lunar spellbook."
    override val usage = "::lunars"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::lunars in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't switch spellbook here.")
            return true
        }
        val isLunar = player.combatDefinitions.getSpellBook() == 2
        if (isLunar) {
            player.message("Your spellbook is already set to lunar.");
            return true;
        }
        player.combatDefinitions.setSpellBook(2)
        return true
    }
}
