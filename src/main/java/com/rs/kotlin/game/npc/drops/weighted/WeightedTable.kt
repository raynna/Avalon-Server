package com.rs.kotlin.game.npc.drops.weighted

import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropContext
import com.rs.kotlin.game.npc.drops.weighted.WeightedEntry
import java.util.concurrent.ThreadLocalRandom

class WeightedTable {
    private val entries = mutableListOf<WeightedEntry>()

    private var explicitSize: Int? = null

    private var nothingWeight: Int = 0

    val totalWeight: Int
        get() = entries.asSequence().filter { it.weight > 0 }.sumOf { it.weight }

    val tableSize: Int
        get() = explicitSize ?: (totalWeight + nothingWeight)

    fun setSize(size: Int) {
        explicitSize = size
    }

    fun setNothingWeight(weight: Int) {
        nothingWeight = weight.coerceAtLeast(0)
    }

    fun add(entry: WeightedEntry) {
        entries.add(entry)
    }

    fun size() = entries.size

    fun roll(context: DropContext): Drop? {
        val validEntries = entries.filter { it.weight > 0 }
        if (validEntries.isEmpty()) return null

        val explicitWeight = validEntries.sumOf { it.weight }
        val total = tableSize

        val rand = ThreadLocalRandom.current().nextInt(total)

        if (rand >= explicitWeight) return null

        var acc = 0
        for (entry in validEntries) {
            acc += entry.weight
            if (rand < acc) return entry.roll(context)
        }
        return null
    }

    fun mutableEntries(): MutableList<WeightedEntry> = entries
}
