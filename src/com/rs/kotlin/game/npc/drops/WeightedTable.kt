package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class WeightedTable {
    private val entries = mutableListOf<WeightedDropEntry>()

    fun add(entry: WeightedDropEntry) {
        entries.add(entry)
    }

    fun size() = entries.size

    fun roll(player: Player): Drop? {
        val totalWeight = entries.sumOf { it.weight }
        if (totalWeight == 0) {
            return null
        }
        val rand = ThreadLocalRandom.current().nextInt(totalWeight)
        var acc = 0
        for (entry in entries) {
            acc += entry.weight
            if (rand < acc) {
                val drop = entry.roll(player)
                return drop
            }
        }
        return null
    }

    fun mutableEntries(): MutableList<WeightedDropEntry> = entries
}

