package com.rs.kotlin.game.npc.drops

class NestedTableEntry(
    val table: WeightedTable,
    override val weight: Int,
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? {
        if (weight <= 0) return null
        return table.roll(context)
    }
}
