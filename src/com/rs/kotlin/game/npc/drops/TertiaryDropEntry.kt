package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max

open class TertiaryDropEntry(itemId: Int, amount: Int, numerator: Int, denominator: Int) :
    DropEntry(itemId, amount, amount) {
    private val weight: Int

    init {
        require(!(numerator <= 0 || denominator <= 0 || numerator > denominator)) { "Invalid probability: $numerator/$denominator" }

        val computedWeight = Math.round(numerator.toDouble() / denominator * WEIGHT_BASE).toInt()
        this.weight = max(computedWeight.toDouble(), 1.0).toInt()
    }

    override fun roll(player: Player?): Drop? {
        if (!shouldDrop(player)) return null
        val roll = ThreadLocalRandom.current().nextInt(WEIGHT_BASE)
        if (roll < weight) {
            return Drop(itemId, minAmount, false)
        }
        return null
    }

    companion object {
        private const val WEIGHT_BASE = 32768
    }
}
