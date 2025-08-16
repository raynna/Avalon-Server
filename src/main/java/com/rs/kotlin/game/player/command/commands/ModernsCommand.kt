package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class ModernsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to ancient spellbook."
    override val usage = "::moderns"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::moderns in this mode.")
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
