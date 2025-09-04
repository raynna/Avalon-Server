package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class BankCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Opens your bank"
    override val usage = "::bank"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::bank in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::bank here.")
            return true
        }
        player.getBank().openBank()
        return true
    }
}
