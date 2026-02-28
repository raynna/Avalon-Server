package com.rs.kotlin.game.npc.drops.tertiary

import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropContext
import com.rs.kotlin.game.npc.drops.DropEntry
import com.rs.kotlin.game.npc.drops.DropMetadata
import com.rs.kotlin.game.npc.drops.DropSource
import java.util.concurrent.ThreadLocalRandom

open class TertiaryDropEntry(
    itemId: Int,
    amount: IntRange,
    private val numerator: Int,
    val denominator: Int,
    private val condition: ((DropContext) -> Boolean)? = null,
    metadata: DropMetadata = DropMetadata(),
) : DropEntry(
        itemId,
        amount,
        always = false,
        condition = null, // handled here instead
        metadata = metadata,
    ) {
    init {
        require(numerator in 1..denominator) {
            "Invalid rate: $numerator/$denominator"
        }
    }

    override fun rollAmount(): Int = ThreadLocalRandom.current().nextInt(amount.first, amount.last + 1)

    fun roll(
        context: DropContext,
        multiplier: Double,
    ): Drop? {
        if (condition?.invoke(context) == false) {
            return null
        }

        val effectiveDenominator =
            (denominator / multiplier)
                .toInt()
                .coerceAtLeast(1)

        val roll =
            ThreadLocalRandom
                .current()
                .nextInt(effectiveDenominator)

        if (roll >= numerator) {
            return null
        }

        val tertiaryContext =
            context.copy(dropSource = DropSource.TERTIARY)

        return Drop(
            itemId = itemId,
            amount = rollAmount(),
            context = tertiaryContext,
            isAlways = false,
            metadata = metadata,
        )
    }

    /** Non-boosted */
    override fun roll(context: DropContext): Drop? = roll(context, 1.0)
}
