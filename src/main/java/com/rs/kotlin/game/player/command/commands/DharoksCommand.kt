package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class DharoksCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Gives you all dharoks pieces."
    override val usage = "::dharoks"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::dharoks in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::dharoks here.")
            return true
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.message("You don't have any inventory space for any dharoks pieces.")
            return true;
        }
        val dharokItems = Item.getIds(
            "item.dharok_s_greataxe", "item.dharok_s_helm",
            "item.dharok_s_platebody", "item.dharok_s_platelegs")
        for (dharokItem in dharokItems) {
            val item = Item(dharokItem)
            if (!player.getInventory().hasFreeSlots()) {
                player.bank.addItem(item, true)
                player.message("No space in inventory, ${item.name} has been added to your bank.")
                continue;
            }
            player.inventory.addItem(item)
        }
        player.message("All dharok pieces has been given to you.")
        return true
    }
}
