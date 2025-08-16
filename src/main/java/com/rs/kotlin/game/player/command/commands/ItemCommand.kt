package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandArguments

class ItemCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Spawn an item by Id or Name."
    override val usage = "::item <id|name> +5"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::item in this mode.")
            return true
        }

        val cmdArgs = CommandArguments(args)

        val rawAmountArg = args.lastOrNull() ?: "1"
        val amount = if (rawAmountArg.startsWith("+")) {
            rawAmountArg.drop(1).toIntOrNull() ?: 1
        } else {
            rawAmountArg.toIntOrNull() ?: 1
        }

        val searchArgs = if (rawAmountArg.toIntOrNull() != null || rawAmountArg.startsWith("+")) {
            args.dropLast(1)
        } else args

        val searchTerm = searchArgs.joinToString(" ")
            .replace("_", " ")
            .lowercase()

        if (searchTerm.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }


        val itemDef = searchTerm.toIntOrNull()?.let { ItemDefinitions.getItemDefinitions(it) }
            ?: run {
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

        player.inventory.addItem(itemDef.id, amount)
        player.message("You spawn $amount x ${itemDef.name} (<col=ff0000>${itemDef.id}</col>).")
        return true
    }
}
