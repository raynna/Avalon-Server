package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.game.npc.drops.weighted.WeightedEntry
import com.rs.kotlin.game.npc.drops.weighted.WeightedTable

class NestedTableEntry(
    val table: WeightedTable,
    override val weight: Int,
    val displayAsTable: Boolean = false,
    val displayName: String? = null,
    val displayItemId: Int? = null,
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? = table.roll(context)
}
