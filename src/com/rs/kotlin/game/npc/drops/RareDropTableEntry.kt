package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class RareDropTableEntry : DropEntry(-1, 1, 1) {
    private class WeightedDropEntry(itemId: Int, min: Int, max: Int, val weight: Double) : DropEntry(itemId, min, max)

    private val entries: MutableList<WeightedDropEntry> = ArrayList()

    init {
        initTable()
        println("[DropSystem] Registered ${entries.size} rare table drops.");
    }

    private fun initTable() {
        add("nothing", numerator = 9, denominator = 64)
        add("uncut dragonstone", numerator = 8, denominator = 64)
        add("loop half of a key", numerator = 6, denominator = 64)
        add("tooth half of a key", numerator = 6, denominator = 64)
        add("rune platelegs", numerator = 5, denominator = 64)
        add("magic logs#noted", min = 65, max = 85, numerator = 4, denominator = 64)
        add("rune arrowheads", min = 110, max = 1140, numerator = 4, denominator = 64)
        add("soft clay#noted", min = 35, max = 45, numerator = 4, denominator = 64)
        add("teak plank#noted", min = 45, max = 55, numerator = 2, denominator = 64)
        add("dragon bones#noted", min = 35, max = 45, numerator = 2, denominator = 64)
        add("dragon helm", numerator = 2, denominator = 64)
        add("dragon longsword", numerator = 1, denominator = 128)
        add("molten glass#noted", min = 45, max = 55, numerator = 4, denominator = 64)
        add("rune ore#noted", min = 25, max = 35, numerator = 4, denominator = 64)
        add("raw lobster#noted", min = 135, max = 165, numerator = 1, denominator = 64)
        add("super-rare", numerator = 4, denominator = 64)
    }

    fun add(
        item: Any,
        min: Int = 1,
        max: Int = min,
        numerator: Int,
        denominator: Int
    ) {
        require(numerator in 1..denominator) { "Invalid drop rate: $numerator/$denominator" }

        val itemId = when (item) {
            is Int -> item
            is String -> when {
                item.equals("nothing", ignoreCase = true) -> -2
                item.equals("super-rare", ignoreCase = true) -> -3
                else -> {
                    ItemDefinitions.searchItems(item, 1).firstOrNull()?.id
                }
            }
            else -> null
        }

        if (itemId == null) {
            return
        }

        var weight = numerator.toDouble() / denominator
        if (weight < 0) weight = 0.0
        entries.add(WeightedDropEntry(itemId, min, max, weight))
    }


    override fun roll(player: Player?): Drop? {
        val filteredEntries = if (player?.hasRingOfWealth() == true) {
            entries.filter { it.itemId != -2 }//remove nothing from table if wearin wealth
        } else {
            entries
        }


        val totalWeight = filteredEntries.sumOf { it.weight }
        if (totalWeight == 0.0) {
            return null
        }
        for (entry in filteredEntries) {
            val name = if (entry.itemId == -2) "nothing" else ItemDefinitions.getItemDefinitions(entry.itemId).name
            println("  -> $name (id=${entry.itemId}, weight=${entry.weight})")
        }

        val roll = ThreadLocalRandom.current().nextDouble(totalWeight)

        var cumulative = 0.0
        for ((index, entry) in filteredEntries.withIndex()) {
            cumulative += entry.weight
            if (roll < cumulative) {
                return when (entry.itemId) {
                    -2 -> {
                        println("[RareDropTable] Rolled 'nothing'")
                        null
                    }
                    -3 -> {
                        println("[RareDropTable] Rolled 'super-table' entry, delegating to SuperRareTable.")
                        val superTable = DropTablesSetup.superRareTable.roll(player)
                        superTable
                    }
                    else -> {
                        val drop = entry.roll(player)
                        val name = ItemDefinitions.getItemDefinitions(entry.itemId).name
                        println("[RareDropTable] Result: $name (id=${entry.itemId})")
                        drop
                    }
                }
            }
        }
        return null
    }

    companion object {
        private const val WEIGHT_BASE = 32000
    }
}
