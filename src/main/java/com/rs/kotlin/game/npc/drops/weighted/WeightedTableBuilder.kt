package com.rs.kotlin.game.npc.drops.weighted

import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.DropMetadata
import com.rs.kotlin.game.npc.drops.NestedTableEntry
import com.rs.kotlin.game.npc.drops.weighted.WeightedTable

class WeightedTableBuilder(
    val table: WeightedTable,
    private val defaultMeta: (DropMetadata.() -> Unit)? = null,
) {
    fun add(
        item: String,
        amount: IntRange = 1..1,
        weight: Int = 1,
        meta: (DropMetadata.() -> Unit)? = null,
    ) {
        val metadata =
            DropMetadata().apply {
                defaultMeta?.invoke(this)
                meta?.invoke(this)
            }
        table.add(
            ItemWeightedEntry(
                itemId = Rscm.lookup(item),
                amount = amount,
                weight = weight,
                metadata = metadata,
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
