package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player

interface WeightedEntry {
    val weight: Int
    fun roll(player: Player, source: DropSource): Drop?
}