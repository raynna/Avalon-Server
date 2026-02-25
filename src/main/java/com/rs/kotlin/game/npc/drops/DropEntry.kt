package com.rs.kotlin.game.npc.drops

import java.util.concurrent.ThreadLocalRandom

open class DropEntry
    @JvmOverloads
    constructor(
        @JvmField var itemId: Int,
        @JvmField var amount: IntRange,
        protected var always: Boolean = false,
        private val condition: ((DropContext) -> Boolean)? = null,
        val metadata: DropMetadata = DropMetadata(),
    ) {
        private var extraDropEntry: DropEntry? = null

        open fun rollAmount(): Int = ThreadLocalRandom.current().nextInt(amount.first, amount.last + 1)

        open fun roll(context: DropContext): Drop? {
            if (condition?.invoke(context) == false) return null

            val mainDrop =
                Drop(
                    itemId = itemId,
                    amount = rollAmount(),
                    context = context,
                    isAlways = always,
                    metadata = metadata,
                )

            extraDropEntry?.roll(context)?.let {
                mainDrop.extraDrop = it
            }

            return mainDrop
        }

        fun setExtraDrop(entry: DropEntry) {
            this.extraDropEntry = entry
        }
    }
