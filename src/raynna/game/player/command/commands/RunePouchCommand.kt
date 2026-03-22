package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.player.Inventory
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class RunePouchCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Gives you a fresh rune pouch"
    override val usage = "::runepouch"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::runepouch in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't spawn a rune pouch here.")
            return true
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.message("You don't have any space for a rune pouch in your inventory.");
            return true
        }
        player.inventory.addItem(Inventory.RUNE_POUCH, 1)
        return true
    }
}
