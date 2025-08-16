package com.rs.kotlin.game.player.command

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks

interface Command {
    val requiredRank: Ranks.Rank
    val description: String
    val usage: String
    fun execute(player: Player, args: List<String>): Boolean
}