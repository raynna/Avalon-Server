package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class AncientsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to ancient spellbook."
    override val usage = "::ancients"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::ancients in this mode.")
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
