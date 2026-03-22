package raynna.game.player.command.commands

import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.npc.worldboss.RandomWorldBossHandler
import raynna.game.player.command.Command

class WorldBossCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Spawns a worldboss"
    override val usage = "::worldboss"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        RandomWorldBossHandler.forceDespawn("Respawned by ${player.username}")
        RandomWorldBossHandler.forceRespawnNow()
        return true
    }
}
