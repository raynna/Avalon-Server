package raynna.game.player.command

import raynna.game.player.Player
import raynna.game.player.Ranks

interface Command {
    val requiredRank: Ranks.Rank
    val description: String
    val usage: String

    fun execute(player: Player, args: List<String>, trigger: String): Boolean
}