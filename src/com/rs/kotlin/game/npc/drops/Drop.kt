package com.rs.kotlin.game.npc.drops

class Drop @JvmOverloads constructor(@JvmField val itemId: Int, @JvmField val amount: Int, val isAlways: Boolean = false) {
    @JvmField
    var extraDrop: Drop? = null

    override fun toString(): String {
        return "Drop{itemId=$itemId, amount=$amount}"
    }
}
