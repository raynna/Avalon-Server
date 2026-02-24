package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm

class PackageBuilder {
    internal val displayDrops = mutableListOf<PackageDisplayDrop>()
    internal val factories = mutableListOf<(Player, DropSource) -> Drop>()

    fun drop(
        item: String,
        amount: IntRange = 1..1,
        meta: (DropMetadata.() -> Unit)? = null,
    ) {
        val id = Rscm.lookup(item)
        val md = DropMetadata().apply { meta?.invoke(this) }

        // for UI/export/collection scan
        displayDrops +=
            PackageDisplayDrop(
                itemId = id,
                amount = amount,
                metadata = md,
            )

        // for actual rolling
        factories += { _, source ->
            Drop(
                itemId = id,
                amount = amount.random(),
                source = source,
                metadata = md,
            )
        }
    }

    internal fun build(
        player: Player,
        source: DropSource,
    ): List<Drop> = factories.map { it(player, source) }
}

fun MutableList<WeightedEntry>.packageDrop(
    weight: Int,
    condition: ((Player) -> Boolean)? = null,
    block: PackageBuilder.() -> Unit,
) {
    val b = PackageBuilder().apply(block)

    add(
        PackageWeightedEntry(
            weight = weight,
            displayDrops = b.displayDrops.toList(), // ✅ important
            condition = condition,
            build = { player, source -> b.build(player, source) },
        ),
    )
}
