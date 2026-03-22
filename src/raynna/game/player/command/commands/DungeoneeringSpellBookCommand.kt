package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class DungeoneeringSpellBookCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Change to dungeoneering spellbook."
    override val usage = "::dungspells"

    override fun execute(
        player: Player,
        args: List<String>,
        trigger: String,
    ): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::dungspells in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't switch spellbook here.")
            return true
        }
        player.getCombatDefinitions().setSpellBook(3)
        player.packets.sendGameMessage("You switch your spellbook to Dungeoneering spells.")
        return true
    }
}
