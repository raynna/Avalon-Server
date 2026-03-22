package raynna.game.player.command.commands

import raynna.core.cache.defintions.NPCDefinitions
import raynna.game.World
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

class SpawnNpcCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Spawns an temporary npc"
    override val usage = "::npc <id>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (args.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }
        val npcId = args[0].toInt()
        val definitions = NPCDefinitions.getNPCDefinitions(npcId)
        if (definitions == null) {
            player.message("There is no npc with this id $npcId")
            return false
        }
        World.spawnNPC(npcId, player, -1, true, true)
        player.message("You spawned an ${definitions.name}($npcId)(Combat Level: ${definitions.combatLevel}).")
        return true
    }
}
