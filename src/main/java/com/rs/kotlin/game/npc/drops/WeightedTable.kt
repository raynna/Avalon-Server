package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class WeightedTable {
    private val entries = mutableListOf<WeightedEntry>()

    var tableSize: Int = 0

    fun tableSize(): Int = if (tableSize > 0) tableSize else entries.sumOf { it.weight }

    fun setSize(size: Int) {
        tableSize = size
    }

    fun add(entry: WeightedEntry) {
        entries.add(entry)
    }

    fun size() = entries.size

    fun roll(context: DropContext): Drop? {
        val validEntries = entries.filter { it.weight > 0 }
        if (validEntries.isEmpty()) return null

        val explicitWeight = validEntries.sumOf { it.weight }
        val total = if (tableSize > 0) tableSize else explicitWeight

        val rand = ThreadLocalRandom.current().nextInt(total)

        if (rand >= explicitWeight) {
            return null // implicit nothing
        }

        var acc = 0
        for (entry in validEntries) {
            acc += entry.weight
            if (rand < acc) {
                return entry.roll(context)
            }
        }

        return null
    }

    fun mutableEntries(): MutableList<WeightedEntry> = entries
}
