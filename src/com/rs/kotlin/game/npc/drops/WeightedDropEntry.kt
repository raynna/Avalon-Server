package com.rs.kotlin.game.npc.drops

open class WeightedDropEntry(itemId: Int, minAmount: Int, maxAmount: Int, var weight: Int) :
    DropEntry(itemId, minAmount, maxAmount)
