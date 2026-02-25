package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.Rscm

class WeightedTableBuilder(
    private val table: WeightedTable,
) {
    fun add(
        item: String,
        amount: IntRange = 1..1,
        weight: Int = 1,
    ) {
        table.add(
            ItemWeightedEntry(
                itemId = Rscm.lookup(item),
                amount = amount,
                weight = weight,
            ),
        )
    }

    fun table(
        nested: WeightedTable,
        weight: Int,
        asSubTable: Boolean = false,
        name: String? = null,
        icon: String? = null,
    ) {
        table.add(
            NestedTableEntry(
                table = nested,
                weight = weight,
                displayAsTable = asSubTable,
                displayName = name,
                displayItemId = icon?.let { Rscm.lookup(it) },
            ),
        )
    }
}
