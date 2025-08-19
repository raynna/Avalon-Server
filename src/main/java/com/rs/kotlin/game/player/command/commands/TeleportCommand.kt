package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command
import com.rs.java.game.player.actions.combat.Magic

class TeleportCommand : Command {

    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Teleport to various predefined locations."
    override val usage = "::<locations>"

    val teleportLocations: Map<List<String>, Pair<WorldTile, String>> = mapOf(
        listOf("wests", "west") to (WorldTile(2978, 3598, 0) to "west dragons"),
        listOf("bridspot", "brid") to (WorldTile(3013, 3553, 0) to "hybridding spot")
    )

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        return teleportByTrigger(player, trigger)
    }

    private fun teleportByTrigger(player: Player, trigger: String): Boolean {
        val entry = teleportLocations.entries.firstOrNull { it.key.contains(trigger.lowercase()) }
            ?: return true.also { player.message("Unknown teleport location: $trigger") }

        if (!player.canUseCommand()) {
            player.message("You can't use ::$trigger at this location.")
            return true
        }

        val (tile, name) = entry.value
        Magic.sendNormalTeleportSpell(player, 0, 0.0, tile)
        player.message("You have teleported to $name.")
        return true
    }

    fun getAllTriggers(): List<String> = teleportLocations.keys.flatten()
}

