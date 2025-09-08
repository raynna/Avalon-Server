package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.actions.combat.Magic
import com.rs.java.game.player.teleportation.Teleports.TeleportLocations
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.world.util.Msg

class BarrowsTeleportCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Teleports you to barrows"
    override val usage = "::barrows"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (!player.canUseCommand()) {
            player.message("You can't use ::barrows here.")
            return true
        }
        if (!Magic.canTeleport(player))
            return true
        Magic.teleport(player, TeleportLocations.BARROWS.location)
        Msg.warn(player, "You teleport to barrows.")
        return true
    }
}
