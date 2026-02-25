package com.rs.kotlin.game.npc.drops

class NestedTableEntry(
    val table: WeightedTable,
    override val weight: Int,
    val displayAsTable: Boolean = false,
    val displayName: String? = null,
    val displayItemId: Int? = null,
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? = table.roll(context)
}
