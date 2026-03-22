package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class AncientsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to ancient spellbook."
    override val usage = "::ancients"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::ancients in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't switch spellbook here.")
            return true
        }
        val isAncients = player.combatDefinitions.getSpellBook() == 1
        if (isAncients) {
            player.message("Your spellbook is already set to ancients.");
            return true;
        }
        player.combatDefinitions.setSpellBook(1)
        return true
    }
}
