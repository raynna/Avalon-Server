package com.rs.kotlin.game.npc.drops.preroll

import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropContext
import com.rs.kotlin.game.npc.drops.DropMetadata
import com.rs.kotlin.game.npc.drops.DropSource
import com.rs.kotlin.game.npc.drops.weighted.WeightedTable
import java.util.concurrent.ThreadLocalRandom

class PreRollTableEntry(
    val table: WeightedTable,
    val numerator: Int,
    var denominator: Int,
    val displayAsTable: Boolean = false,
    val displayName: String? = null,
    val displayItemId: Int? = null,
    var metadata: DropMetadata = DropMetadata(),
) {
    init {
        require(numerator in 1..denominator) {
            "Invalid preroll rate: $numerator/$denominator"
        }
    }

    fun roll(
        context: DropContext,
        multiplier: Double,
    ): Drop? {
        val effectiveDenominator =
            (denominator / multiplier).toInt().coerceAtLeast(1)

        val roll = ThreadLocalRandom.current().nextInt(effectiveDenominator)
        if (roll >= numerator) return null
        val drop = table.roll(context.copy(dropSource = DropSource.PREROLL))
        drop?.let {
            it.metadata = metadata
        }
        return table.roll(context.copy(dropSource = DropSource.PREROLL))
    }
}
