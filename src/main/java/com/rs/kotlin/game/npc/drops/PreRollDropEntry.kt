package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class PreRollDropEntry(
    itemId: Int?,
    amount: IntRange,
    val numerator: Int,
    val denominator: Int,
    val condition: ((Player) -> Boolean)? = null,
    val dynamicItem: ((Player) -> Int?)? = null,
    val displayItems: List<Int>? = null
) : DropEntry(itemId ?: -1, amount) {

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

        if (roll >= numerator)
            return null

        val finalItemId =
            dynamicItem?.invoke(player)
                ?: itemId.takeIf { it != -1 }
                ?: return null

        return Drop(finalItemId, rollAmount(), source = DropSource.PREROLL)
    }
}
