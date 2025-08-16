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
            val text = "::${name} - ${cmd.description}<br><col=aaaaaa>Usage: ${cmd.usage}</col>"
            player.packets.sendTextOnComponent(interfaceId, lineId, text)
            lineId++
        }

        for (i in lineId..150) {
            player.packets.sendTextOnComponent(interfaceId, i, "")
        }

        return true
    }
}
