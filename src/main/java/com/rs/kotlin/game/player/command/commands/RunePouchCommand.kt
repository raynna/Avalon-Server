package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Inventory
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class RunePouchCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Gives you a fresh rune pouch"
    override val usage = "::runepouch"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::runepouch in this mode.")
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
