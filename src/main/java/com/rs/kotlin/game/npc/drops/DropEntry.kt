package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

open class DropEntry @JvmOverloads constructor(
    @JvmField var itemId: Int,
    @JvmField protected var amount: IntRange,
    protected var always: Boolean = false,
    private val condition: ((Player) -> Boolean)? = null
) {
    private var extraDropEntry: DropEntry? = null

    open fun rollAmount(): Int {
        return ThreadLocalRandom.current().nextInt(amount.first, amount.last + 1)
    }

    open fun roll(player: Player): Drop? {
        if (condition?.invoke(player) == false) return null

        val mainDrop = Drop(itemId, rollAmount(), always)

        extraDropEntry?.roll(player)?.let {
            mainDrop.extraDrop = it
        }

        return mainDrop
    }
}

