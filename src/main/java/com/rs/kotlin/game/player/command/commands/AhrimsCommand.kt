package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class AhrimsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Gives you all ahrims pieces."
    override val usage = "::ahrims"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::ahrims in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::ahrims here.")
            return true
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.message("You don't have any inventory space for any ahrims pieces.")
            return true;
        }
        val ahrimItems = Item.getIds(
            "item.ahrim_s_staff", "item.ahrim_s_hood",
            "item.ahrim_s_robe_top", "item.ahrim_s_robe_skirt")
        for (ahrimItem in ahrimItems) {
            val item = Item(ahrimItem)
            if (!player.getInventory().hasFreeSlots()) {
                player.bank.addItem(item, true)
                player.message("No space in inventory, ${item.name} has been added to your bank.")
                continue;
            }
            player.inventory.addItem(item)
        }
        player.message("All ahrims pieces has been given to you.")
        return true
    }
}
