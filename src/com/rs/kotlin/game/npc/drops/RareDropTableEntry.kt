package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.item.ItemId
import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class RareDropTableEntry : DropEntry(-1, 1, 1) {
    private class WeightedDropEntry(itemId: Int, min: Int, max: Int, val weight: Int) : DropEntry(itemId, min, max)

    private val entries: MutableList<WeightedDropEntry> = ArrayList()

    init {
        initTable()
        println("[DropSystem] Registered ${entries.size} rare table drops.");
    }

    private fun initTable() {
        add("nothing", numerator = 1, denominator = 69)
        add("uncut sapphire", numerator = 1, denominator = 155)
        add("uncut emerald", numerator = 1, denominator = 309)
        add("loop half of key", numerator = 1, denominator = 378)
        add("tooth half of key", numerator = 1, denominator = 378)
        add(ItemId.COINS, min = 3000, max = 3000, numerator = 1, denominator = 390)
        add("uncut ruby", numerator = 1, denominator = 618)
        add("runite bar", numerator = 1, denominator = 1638)
        add("chaos talisman", numerator = 1, denominator = 1649)
        add("nature talisman", numerator = 1, denominator = 1649)
        add("uncut diamond", numerator = 1, denominator = 2473)
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
            is String -> {
                if (item.equals("nothing", ignoreCase = true)) -2
                else ItemDefinitions.searchItems(item, 1).firstOrNull()?.id
            }
            else -> null
        }

        if (itemId == null) {
            return
        }

        var weight = Math.round(numerator.toDouble() / denominator * WEIGHT_BASE).toInt()
        if (weight <= 0) weight = 1
        entries.add(WeightedDropEntry(itemId, min, max, weight))
    }


    override fun roll(player: Player?): Drop? {

        val randomCheck = ThreadLocalRandom.current().nextInt(LAND_DENOMINATOR)
        if (randomCheck >= LAND_CHANCE) {
            return null
        }

        val filteredEntries = if (player?.hasRingOfWealth() == true) {
            entries.filter { it.itemId != -2 } // Exclude "nothing"
        } else {
            entries
        }


        val totalWeight = filteredEntries.sumOf { it.weight }
        if (totalWeight == 0) {
            return null
        }

        val roll = ThreadLocalRandom.current().nextInt(totalWeight) + 1

        var cumulative = 0
        for ((index, entry) in filteredEntries.withIndex()) {
            cumulative += entry.weight
            if (roll <= cumulative) {
                if (entry.itemId == -2) {
                    return null
                } else {
                    val gemTable = DropTablesSetup.gemDropTable.roll(player)
                    if (gemTable != null) {
                        return gemTable;
                    }
                    return entry.roll(player)
                }
            }
        }
        return null
    }

    companion object {
        private const val LAND_CHANCE = 2
        private const val LAND_DENOMINATOR = 128
        private const val WEIGHT_BASE = 32000
    }
}
