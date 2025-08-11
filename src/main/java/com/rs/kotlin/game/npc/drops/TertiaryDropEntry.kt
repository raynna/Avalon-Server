package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

open class TertiaryDropEntry(
    itemId: Int,
    amount: IntRange,
    private val numerator: Int,
    private val denominator: Int,
    private val condition: ((Player) -> Boolean)? = null
) : DropEntry(itemId, amount) {

    init {
        require(numerator in 1..denominator) { "Invalid weight: $numerator/$denominator" }
    }

    override fun rollAmount(): Int {
        return ThreadLocalRandom.current().nextInt(amount.first, amount.last + 1)
    }

    override fun roll(player: Player): Drop? {
        if (condition != null && !condition.invoke(player)) {
            return null
        }
        val roll = ThreadLocalRandom.current().nextInt(denominator)
        if (roll < numerator) {
            return Drop(itemId, rollAmount())
        }
        return null
    }
}

