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

    fun roll(player: Player): Drop? {
        val rand = ThreadLocalRandom.current().nextInt(tableSize)
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

