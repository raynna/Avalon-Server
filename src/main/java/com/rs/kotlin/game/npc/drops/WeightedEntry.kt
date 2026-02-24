package com.rs.kotlin.game.npc.drops

interface WeightedEntry {
    val weight: Int

    fun roll(context: DropContext): Drop?
}
