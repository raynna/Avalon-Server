package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class LunarsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change to lunar spellbook."
    override val usage = "::lunars"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::lunars in this mode.")
            return true
        }
        val isLunar = player.combatDefinitions.getSpellBook() == 2
        if (isLunar) {
            player.message("Your spellbook is already set to lunar.");
            return true;
        }
        player.combatDefinitions.setSpellBook(2)
        return true
    }
}
