package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player

open class WeightedDropEntry(
    itemId: Int,
    amount: IntRange,
    val numerator: Int,
    val denominator: Int,
    private val condition: ((Player) -> Boolean)? = null,
    private val customLogic: ((Player, Drop) -> Unit)? = null
) : DropEntry(itemId, amount) {

    val weight: Int = calculateWeight(numerator, denominator)

    override fun roll(player: Player): Drop? {
        if (condition != null && !condition.invoke(player)) return null
        val drop = Drop(itemId, rollAmount())
        customLogic?.invoke(player, drop)
        return drop
    }

    companion object {
        private const val WEIGHT_SCALE = 1000
        fun calculateWeight(numerator: Int, denominator: Int) =
            maxOf((numerator.toDouble() / denominator.toDouble() * WEIGHT_SCALE).toInt(), 1)
    }
}
