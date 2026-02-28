package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.weighted.PackageWeightedEntry
import com.rs.kotlin.game.npc.drops.weighted.WeightedEntry

class PackageBuilder {
    internal val displayDrops = mutableListOf<PackageDisplayDrop>()
    internal val factories = mutableListOf<(DropContext) -> Drop>()

    fun drop(
        item: String,
        amount: IntRange = 1..1,
        meta: (DropMetadata.() -> Unit)? = null,
    ) {
        val id = Rscm.lookup(item)
        val md = DropMetadata().apply { meta?.invoke(this) }

        displayDrops +=
            PackageDisplayDrop(
                itemId = id,
                amount = amount,
                metadata = md,
            )

        // For actual rolling
        factories += { context ->
            Drop(
                itemId = id,
                amount = amount.random(),
                context = context,
                metadata = md,
            )
        }
    }

    fun dropMany(vararg entries: Pair<String, Int>) {
        for ((key, amt) in entries) drop(key, amt..amt)
    }

    internal fun build(context: DropContext): List<Drop> = factories.map { it(context) }
}

fun MutableList<WeightedEntry>.packageDrop(
    weight: Int,
    condition: ((DropContext) -> Boolean)? = null,
    block: PackageBuilder.() -> Unit,
) {
    val b = PackageBuilder().apply(block)

    add(
        PackageWeightedEntry(
            weight = weight,
            displayDrops = b.displayDrops.toList(),
            condition = condition,
            build = { context -> b.build(context) },
        ),
    )
}
