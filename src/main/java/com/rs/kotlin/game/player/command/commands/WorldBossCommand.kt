package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.npc.worldboss.RandomWorldBossHandler
import com.rs.kotlin.game.player.command.Command

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
