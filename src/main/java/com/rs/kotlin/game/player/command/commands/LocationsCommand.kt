package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class LocationsCommand(private val teleportCommand: TeleportCommand) : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Opens a list of all available teleport locations."
    override val usage = "::locations"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        val interfaceId = 275
        val titleComponentId = 1
        val startLine = 10

        player.interfaceManager.sendInterface(interfaceId)
        player.packets.sendTextOnComponent(interfaceId, titleComponentId, "*Teleport Locations*")

        val sortedTeleports = teleportCommand.teleportLocations.entries
            .sortedBy { it.value.second }

        var lineId = startLine
        for ((triggers, locationPair) in sortedTeleports) {
            val tileName = locationPair.second
            val triggersText = triggers.joinToString("/") { "::$it" }

            val textLine1 = "$triggersText - $tileName"
            player.packets.sendTextOnComponent(interfaceId, lineId, textLine1)

            val textLine2 = "<shad=00000><col=ffaa00>Usage: ${triggersText}</col></shad>"
            player.packets.sendTextOnComponent(interfaceId, lineId + 1, textLine2)

            lineId += 2
        }

        // Clear remaining lines
        for (i in lineId..310) {
            player.packets.sendTextOnComponent(interfaceId, i, "")
        }

        return true
    }
}
