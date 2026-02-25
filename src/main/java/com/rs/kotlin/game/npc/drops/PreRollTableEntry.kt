package com.rs.kotlin.game.npc.drops

import java.util.concurrent.ThreadLocalRandom

class PreRollTableEntry(
    val table: WeightedTable,
    val numerator: Int,
    var denominator: Int,
    val displayAsTable: Boolean = false,
    val displayName: String? = null,
    val displayItemId: Int? = null,
) {
    init {
        require(numerator in 1..denominator) {
            "Invalid preroll rate: $numerator/$denominator"
        }
    }

    fun roll(
        multiplier: Double,
        context: DropContext,
    ): Drop? {
        val effectiveDenominator =
            (denominator / multiplier).toInt().coerceAtLeast(1)

        val roll = ThreadLocalRandom.current().nextInt(effectiveDenominator)
        if (roll >= numerator) return null

        return table.roll(context.copy(dropSource = DropSource.PREROLL))
    }
}
