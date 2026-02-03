package com.rs.kotlin.game.player.command.commands

import com.rs.core.cache.defintions.GraphicDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class GraphicCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Executes an gfx"
    override val usage = "::gfx <id>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (args.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }
        val gfxId = args[0].toInt()
        player.gfx(gfxId, 250, 0)
        val defs = GraphicDefinitions.getAnimationDefinitions(gfxId)

        if (defs.intValue != -1) {
            player.message(
                "Graphic $gfxId sound=${defs.intValue}, type=${defs.byteValue}"
            )
        } else {
            player.message("Graphic $gfxId has no embedded sound.")
        }
        player.message("Graphic $gfxId has been executed.")
        return true
    }
}
