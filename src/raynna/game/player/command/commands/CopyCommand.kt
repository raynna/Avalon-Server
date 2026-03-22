package raynna.game.player.command.commands

import raynna.app.Settings
import raynna.game.World
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.Skills
import raynna.game.player.command.Command
import raynna.game.player.command.CommandArguments
import raynna.game.world.util.Msg

class CopyCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Spawn a gear preset"
    override val usage = "::copy <name>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            Msg.warn(player,"You can't use ::copy in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            Msg.warn(player,"You can't use ::copy here.")
            return true
        }
        val cmdArgs = CommandArguments(args)

        val name = cmdArgs.getJoinedString(0)
        val target = World.getPlayer(name) ?: null
        if (target == null) {
            Msg.warn(player,"Couldn't find any player named $name.");
        }
        player.presetManager.copyPreset(target)
        return true
    }
}
