package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class GemTableEntry : DropEntry(-1, 1, 1) {
    private class WeightedDropEntry(itemId: Int, min: Int, max: Int, val weight: Double) : DropEntry(itemId, min, max)

    private val entries: MutableList<WeightedDropEntry> = ArrayList()

    init {
        initTable()
        println("[DropSystem] Registered ${entries.size} Gem table drops.");
    }

    private fun initTable() {
        add("nothing", numerator = 4, denominator = 128)
        add("coins", min = 250, max = 499, numerator = 59, denominator = 128)
        add("uncut sapphire", numerator = 31, denominator = 128)
        add("uncut emerald", numerator = 16, denominator = 128)
        add("uncut ruby", numerator = 8, denominator = 128)
        add("uncut diamond", numerator = 2, denominator = 128)
        add("rune javelin", min = 5, numerator = 4, denominator = 128)
        add("chaos talisman", numerator = 3, denominator = 128)//TODO ADD NATURE IF NOT UNDERGROUND
        add("uncut dragonstone", numerator = 1, denominator = 128)
        add("tooth half of key", numerator = 1, denominator = 128)
        add("loop half of key", numerator = 1, denominator = 128)
        add("rare-table", numerator = 1, denominator = 128)//rare table
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
                item.equals("rare-table", ignoreCase = true) -> -3
                else -> {
                    val def = ItemDefinitions.searchItems(item, 1).firstOrNull()
                    if (def == null) {
                        println("[DropSystem] Warning: Item '$item' not found in definitions.")
                    }
                    def?.id
                }
            }
            else -> {
                println("[DropSystem] Warning: Unsupported item type: ${item::class.simpleName}")
                null
            }
        }

        if (itemId == null) {
            return
        }
        var weight = numerator.toDouble() / denominator
        if (weight < 0) weight = 0.0
        entries.add(WeightedDropEntry(itemId, min, max, weight))
    }


    override fun roll(player: Player?): Drop? {

        val randomCheck = ThreadLocalRandom.current().nextInt(LAND_DENOMINATOR)

        if (randomCheck >= LAND_CHANCE) {
            return null
        }

        val filteredEntries = if (player?.hasRingOfWealth() == true) {
            entries.filter { it.itemId != -2 }
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
                        println("[GemTable] Rolled 'nothing'")
                        null
                    }
                    -3 -> {
                        println("[GemTable] Rolled 'rare-table' entry, delegating to RareTable.")
                        val rareTable = DropTablesSetup.rareDropTable.roll(player)
                        rareTable
                    }
                    else -> {
                        val drop = entry.roll(player)
                        val name = ItemDefinitions.getItemDefinitions(entry.itemId).name
                        println("[GemTable] Result: $name (id=${entry.itemId})")
                        drop
                    }
                }
            }
        }
        return null
    }


    companion object {
        private const val LAND_CHANCE = 3
        private const val LAND_DENOMINATOR = 128
        private const val WEIGHT_BASE = 128
    }
}