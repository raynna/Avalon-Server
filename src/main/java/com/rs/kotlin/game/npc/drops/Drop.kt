package com.rs.kotlin.game.npc.drops

class Drop(
    @JvmField val itemId: Int,
    @JvmField val amount: Int,
    val context: DropContext,
    val isAlways: Boolean = false,
    var metadata: DropMetadata? = null,
) {
    @JvmField
    var extraDrop: Drop? = null

    fun copy(
        itemId: Int = this.itemId,
        amount: Int = this.amount,
        context: DropContext = this.context,
        isAlways: Boolean = this.isAlways,
        metadata: DropMetadata? = this.metadata,
        extraDrop: Drop? = this.extraDrop,
    ): Drop {
        val newDrop =
            Drop(
                itemId = itemId,
                amount = amount,
                context = context,
                isAlways = isAlways,
                metadata = metadata,
            )
        newDrop.extraDrop = extraDrop
        return newDrop
    }

    override fun toString(): String = "Drop{itemId=$itemId, amount=$amount}"
}
