package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max

open class TertiaryDropEntry(itemId: Int, amount: Int, numerator: Int, denominator: Int) :
    DropEntry(itemId, amount, amount) {
    private val probability: Double

    init {
        require(!(numerator <= 0 || denominator <= 0 || numerator > denominator)) { "Invalid probability: $numerator/$denominator" }
        probability = numerator.toDouble() / denominator;
    }

    override fun roll(player: Player?): Drop? {
        if (!shouldDrop(player)) return null
        val roll = ThreadLocalRandom.current().nextDouble()
        if (roll < probability) return Drop(itemId, minAmount, false)
        return null
    }

    companion object {
    }
}
