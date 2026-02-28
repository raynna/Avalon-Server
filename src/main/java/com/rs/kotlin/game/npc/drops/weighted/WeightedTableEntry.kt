package com.rs.kotlin.game.npc.drops.weighted

import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropContext

class WeightedTableEntry(
    val weightedTable: WeightedTable,
    override val weight: Int,
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? = weightedTable.roll(context)
}
