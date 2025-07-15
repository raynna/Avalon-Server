package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

open class DropEntry @JvmOverloads constructor(
    @JvmField var itemId: Int,
    @JvmField protected var minAmount: Int,
    protected var maxAmount: Int,
    protected var always: Boolean = false
) {
    private var extraDropEntry: DropEntry? = null


    constructor(itemId: Int, amount: Int) : this(itemId, amount, amount, false)

    private fun rollAmount(): Int {
        if (minAmount == maxAmount) return minAmount
        return ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1)
    }

    open fun shouldDrop(player: Player?): Boolean {
        return true
    }

    open fun roll(player: Player?): Drop? {
        if (!shouldDrop(player)) return null

        val mainDrop = Drop(itemId, rollAmount(), always)

        if (extraDropEntry != null) {
            val extra = extraDropEntry!!.roll(player)
            if (extra != null) {
                mainDrop.extraDrop = extra
            }
        }

        return mainDrop
    }
}
