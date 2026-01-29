package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class CustomTitleCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Set a custom title"
    override val usage = "::customtitle"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        player.temporaryAttribute().remove("TITLE_COLOR_SET")
        player.temporaryAttribute().remove("TITLE_ORDER_SET")
        player.temporaryAttribute()["CUSTOM_TITLE_SET"] = java.lang.Boolean.TRUE
        player.packets.sendInputNameScript("Enter your custom title, or id 0-58")
        return true
    }
}
