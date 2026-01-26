package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class PreRollDropEntry(
    itemId: Int,
    amount: IntRange,
    val numerator: Int,
    val denominator: Int,
    private val condition: ((Player) -> Boolean)? = null
) : DropEntry(itemId, amount) {

    init {
        require(numerator in 1..denominator) {
            "Invalid preroll rate: $numerator/$denominator"
        }
    }

    fun roll(player: Player, multiplier: Double): Drop? {
        if (condition?.invoke(player) == false)
            return null

        val effectiveDenominator =
            (denominator / multiplier)
                .toInt()
                .coerceAtLeast(1)

        val roll = ThreadLocalRandom.current().nextInt(effectiveDenominator)

        return if (roll < numerator)
            Drop(itemId, rollAmount())
        else
            null
    }
}
