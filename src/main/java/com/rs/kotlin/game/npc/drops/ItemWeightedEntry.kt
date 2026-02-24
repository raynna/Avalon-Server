package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player

class ItemWeightedEntry(
    val itemId: Int,
    val amount: IntRange,
    override val weight: Int = 1,
    private val condition: ((Player) -> Boolean)? = null,
    private val customLogic: ((Player, Drop?) -> Unit)? = null,
    val metadata: DropMetadata = DropMetadata(),
) : WeightedEntry {
    override fun roll(
        player: Player,
        source: DropSource,
    ): Drop? {
        if (condition != null && !condition.invoke(player)) return null

        val drop = Drop(itemId, amount.random(), source = source)
        customLogic?.invoke(player, drop)
        return drop
    }
}
