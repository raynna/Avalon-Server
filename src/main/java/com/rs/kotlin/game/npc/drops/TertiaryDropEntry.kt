package com.rs.kotlin.game.npc.drops

import com.rs.Settings
import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

open class TertiaryDropEntry(
    itemId: Int,
    amount: IntRange,
    private val numerator: Int,
    val denominator: Int,
    private val condition: ((Player) -> Boolean)? = null
) : DropEntry(itemId, amount) {

    init {
        require(numerator in 1..denominator) {
            "Invalid weight: $numerator/$denominator"
        }
    }

    override fun rollAmount(): Int {
        return ThreadLocalRandom.current()
            .nextInt(amount.first, amount.last + 1)
    }

    /** Boosted roll */
    fun roll(player: Player, multiplier: Double): Drop? {
        if (condition != null && !condition.invoke(player)) {
            return null
        }

        val effectiveDenominator =
            (denominator / multiplier)
                .toInt()
                .coerceAtLeast(1)

        val roll = ThreadLocalRandom.current()
            .nextInt(effectiveDenominator)

        if (roll < numerator) {
            return Drop(itemId, rollAmount(), source = DropSource.TERTIARY)
        }

        return null
    }

    /** Backwards compatible */
    override fun roll(player: Player): Drop? {
        return roll(player, 1.0)
    }
}
