package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm

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

        // For UI/export/collection scan
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
