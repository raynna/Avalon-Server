package com.rs.kotlin.game.npc.drops

class Drop @JvmOverloads constructor(@JvmField val itemId: Int, @JvmField val amount: Int, val isAlways: Boolean = false) {
    @JvmField
    var extraDrop: Drop? = null
    fun copy(
        itemId: Int = this.itemId,
        amount: Int = this.amount,
        isAlways: Boolean = this.isAlways,
        extraDrop: Drop? = this.extraDrop
    ): Drop {
        val newDrop = Drop(itemId, amount, isAlways)
        newDrop.extraDrop = extraDrop
        return newDrop
    }
    override fun toString(): String {
        return "Drop{itemId=$itemId, amount=$amount}"
    }
}
