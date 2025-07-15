package com.rs.kotlin.game.npc.drops

open class WeightedDropEntry(itemId: Int, minAmount: Int, maxAmount: Int, var weight: Double) :
    DropEntry(itemId, minAmount, maxAmount)
