package com.rs.kotlin.game.npc.drops.weighted

import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropContext

interface WeightedEntry {
    val weight: Int

    fun roll(context: DropContext): Drop?
}
