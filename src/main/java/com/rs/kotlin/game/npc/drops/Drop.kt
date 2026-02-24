package com.rs.kotlin.game.npc.drops

class Drop
    @JvmOverloads
    constructor(
        @JvmField val itemId: Int,
        @JvmField val amount: Int,
        val isAlways: Boolean = false,
        val source: DropSource = DropSource.MAIN,
        var metadata: DropMetadata? = null,
    ) {
        @JvmField
        var extraDrop: Drop? = null

        fun copy(
            itemId: Int = this.itemId,
            amount: Int = this.amount,
            isAlways: Boolean = this.isAlways,
            source: DropSource = this.source,
            metadata: DropMetadata? = this.metadata,
            extraDrop: Drop? = this.extraDrop,
        ): Drop {
            val newDrop = Drop(itemId, amount, isAlways, source, metadata)
            newDrop.extraDrop = extraDrop
            return newDrop
        }

        override fun toString(): String = "Drop{itemId=$itemId, amount=$amount}"
    }
