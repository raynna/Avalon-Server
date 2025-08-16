package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandRegistry

class CommandsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Opens a list of available commands in an interface."
    override val usage = "::commandlist"

    override fun execute(player: Player, args: List<String>): Boolean {
        val interfaceId = 275
        val titleComponentId = 1
        val startLine = 10

        player.interfaceManager.sendInterface(interfaceId)
        player.packets.sendTextOnComponent(interfaceId, titleComponentId, "*Commands*")

        val availableCommands = CommandRegistry.getAllPrimary()
            .filter { (_, cmd) -> player.playerRank.isAtLeast(cmd.requiredRank) }
            .toList()
            .sortedBy { it.first }

        var lineId = startLine
        for ((name, cmd) in availableCommands) {
            val textLine1 = "::${name} - ${cmd.description}"
            player.packets.sendTextOnComponent(interfaceId, lineId, textLine1)

            val textLine2 = "<shad=00000><col=ffaa00>Usage: ${cmd.usage}</col></shad>"
            player.packets.sendTextOnComponent(interfaceId, lineId + 1, textLine2)

            lineId += 2
        }

        for (i in lineId..310) {
            player.packets.sendTextOnComponent(interfaceId, i, "")
        }


        return true
    }
}
