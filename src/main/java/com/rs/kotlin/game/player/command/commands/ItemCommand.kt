package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.utils.EconomyPrices
import com.rs.kotlin.game.player.command.Command

class ItemCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Spawn an item by Id or Name."
    override val usage = "::item <id|name> [amount] [noted]"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::item in this mode.")
            return true
        }

        if (args.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }

        val noted = args.any { it.equals("noted", ignoreCase = true) }

        val firstArg = args.first()
        val itemId = firstArg.toIntOrNull()

        val amountArg = args.drop(1).lastOrNull { it.toIntOrNull() != null || it.startsWith("+") }
        val amount = amountArg?.let { if (it.startsWith("+")) it.drop(1).toIntOrNull() ?: 1 else it.toIntOrNull() ?: 1 } ?: 1

        val searchArgs = if (itemId != null) {
            args.drop(1).filter { it.lowercase() != "noted" && it != amountArg }
        } else {
            args.filter { it.lowercase() != "noted" && it != amountArg }
        }
        val searchTerm = searchArgs.joinToString(" ").replace("_", " ").lowercase()

        val itemDef = itemId?.let { ItemDefinitions.getItemDefinitions(it) } ?: run {
            val results = ItemDefinitions.searchItems(searchTerm, 10)
            if (results.isEmpty()) {
                player.message("No item found for '$searchTerm'.")
                return true
            }
            if (results.size > 1) {
                player.message("Results for '$searchTerm':")
                results.forEach { player.message("- ${it.name} (<col=ff0000>${it.id}</col>)") }
            }
            results.first()
        }

        val finalItem = if (noted && itemDef.certId != -1) ItemDefinitions.getItemDefinitions(itemDef.certId) else itemDef
        if (noted && finalItem.id == itemDef.id) {
            player.message("${itemDef.name} cannot be spawned as noted.")
        }
        if (Settings.ECONOMY_MODE < Settings.FULL_SPAWN) {
            if (EconomyPrices.getPrice(itemId!!) > 0 && !player.rank.isDeveloper) {
                player.message("This item costs money, look for this item in shops.")
                return true
            }
        }
        player.inventory.addItem(finalItem.id, amount)
        player.message("You spawn $amount x ${finalItem.name} (<col=ff0000>${finalItem.id}</col>).")
        return true
    }


}
