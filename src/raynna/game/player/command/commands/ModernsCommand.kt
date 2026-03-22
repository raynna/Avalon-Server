package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class ModernsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to ancient spellbook."
    override val usage = "::moderns"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::moderns in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't switch spellbook here.")
            return true
        }
        val isModerns = player.combatDefinitions.getSpellBook() == 0
        if (isModerns) {
            player.message("Your spellbook is already set to moderns.");
            return true;
        }
        player.combatDefinitions.setSpellBook(0)
        return true
    }
}
