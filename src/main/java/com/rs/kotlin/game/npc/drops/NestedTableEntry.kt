package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player

class NestedTableEntry(
    val table: WeightedTable,
    override val weight: Int
) : WeightedEntry {

    override fun roll(player: Player, source: DropSource): Drop? {
        if (weight <= 0) return null
        return table.roll(player, source)
    }
}