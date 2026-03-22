package raynna.game.player.command.commands

import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.actions.combat.Magic
import raynna.game.player.teleport.Teleports.TeleportLocations
import raynna.game.player.command.Command
import raynna.game.world.util.Msg

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
