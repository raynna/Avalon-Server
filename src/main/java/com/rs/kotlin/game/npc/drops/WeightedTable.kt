package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class WeightedTable {
    private val entries = mutableListOf<WeightedDropEntry>()

    private var tableSize: Int = 0

    fun setSize(size: Int) {
        tableSize = size
    }

    fun add(entry: WeightedDropEntry) {
        entries.add(entry)
    }

    fun size() = entries.size

    fun roll(player: Player, source: DropSource): Drop? {

        val validEntries = entries.filter { it.weight > 0 }

        if (validEntries.isEmpty()) {
            return null
        }

        val totalWeight = validEntries.sumOf { it.weight }

        if (totalWeight <= 0) {
            return null
        }
        val rand = ThreadLocalRandom.current().nextInt(totalWeight)
        var acc = 0
        for (entry in validEntries) {
            acc += entry.weight
            if (rand < acc) {
                return entry.roll(player, source)
            }
        }

        return null
    }


    fun mutableEntries(): MutableList<WeightedDropEntry> = entries
}

