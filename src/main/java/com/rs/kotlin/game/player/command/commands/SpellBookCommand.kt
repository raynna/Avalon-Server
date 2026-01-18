package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class SpellBookCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Change between magic spellbooks."
    override val usage = "::spellbook"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::spellbook in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't switch spellbook here.")
            return true
        }
        val spellBook = player.getCombatDefinitions().spellBook.toInt()
        player.getCombatDefinitions().setSpellBook(if (spellBook == 0) 1 else if (spellBook == 1) 2 else 0)
        player.packets.sendGameMessage("You switch your spellbook to ${(if (spellBook == 0) "Ancients" else if (spellBook == 1) "Lunars" else "Modern")}.")
        return true
    }
}
