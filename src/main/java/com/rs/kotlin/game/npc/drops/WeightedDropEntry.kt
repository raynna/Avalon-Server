package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player

open class WeightedDropEntry(
    itemId: Int,
    amount: IntRange,
    val weight: Int = 1,
    private val condition: ((Player) -> Boolean)? = null,
    private val customLogic: ((Player, Drop?) -> Unit)? = null
) : DropEntry(itemId, amount) {

    override fun roll(player: Player): Drop? {
        if (condition != null && !condition.invoke(player)) return null
        val drop = Drop(itemId, rollAmount())
        customLogic?.invoke(player, drop)
        return drop
    }
}
