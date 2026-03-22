package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.Skills
import raynna.game.player.TickManager
import raynna.game.player.command.Command
import kotlin.math.floor

class VoidCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Gives you all void pieces."
    override val usage = "::void"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::spec in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::void here.")
            return true
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.message("You don't have any inventory space for any void pieces.")
            return true;
        }
        val voidItems = Item.getIds(
            "item.void_knight_top", "item.void_knight_robe",
            "item.void_knight_gloves", "item.void_melee_helm",
            "item.void_ranger_helm", "item.void_mage_helm",
            "item.elite_void_knight_top", "item.elite_void_knight_robe", "item.void_knight_deflector_2")
        for (voidItem in voidItems) {
            val item = Item(voidItem)
            if (!player.getInventory().hasFreeSlots()) {
                player.bank.addItem(item, true)
                player.message("No space in inventory, ${item.name} has been added to your bank.")
                continue;
            }
            player.inventory.addItem(item)
        }
        player.message("All void items has been given to you.")
        return true
    }
}
