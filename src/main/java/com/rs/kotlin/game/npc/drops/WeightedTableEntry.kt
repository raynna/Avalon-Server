package com.rs.kotlin.game.npc.drops

class WeightedTableEntry(
    val weightedTable: WeightedTable,
    override val weight: Int,
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? = weightedTable.roll(context)
}
