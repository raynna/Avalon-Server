package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class CustomTitleCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Set a custom title"
    override val usage = "::customtitle"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        player.temporaryAttribute()["customtitle"] = java.lang.Boolean.TRUE
        player.packets.sendInputNameScript("Enter your custom title")
        player.packets.sendGameMessage("To get the title AFTER your name use commands ::customtitle.")
        return true
    }
}
