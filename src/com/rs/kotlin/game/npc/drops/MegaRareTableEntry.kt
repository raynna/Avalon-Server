package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class MegaRareTableEntry : DropEntry(-1, 1, 1) {
    private class WeightedDropEntry(itemId: Int, min: Int, max: Int, val weight: Int) : DropEntry(itemId, min, max)

    private val entries: MutableList<WeightedDropEntry> = ArrayList()

    init {
        initTable()
        println("[DropSystem] Registered ${entries.size} Mega rare table drops.");
    }

    private fun initTable() {
        add("nothing", numerator = 1, denominator = 2)
        add("rune spear", numerator = 1, denominator = 16)
        add("shield left half", numerator = 1, denominator = 32)
        add("dragon spear", numerator = 1, denominator = 42)
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
        println("[MegaRare] Landed on mega-rare table!")

        val filteredEntries = if (player?.hasRingOfWealth() == true) {
            println("[MegaRare] Ring of Wealth equipped — filtering out 'nothing'.")
            entries.filter { it.itemId != -2 }
        } else {
            println("[MegaRare] No Ring of Wealth — including 'nothing'.")
            entries
        }

        if (filteredEntries.isEmpty()) {
            println("[MegaRare] No valid entries after filtering. Aborting.")
            return null
        }

        val totalWeight = filteredEntries.sumOf { it.weight }
        if (totalWeight == 0) {
            println("[MegaRare] Total weight after filtering is 0. Aborting.")
            return null
        }

        println("[MegaRare] Entries (post-filter):")
        for (entry in filteredEntries) {
            val name = if (entry.itemId == -2) "nothing" else ItemDefinitions.getItemDefinitions(entry.itemId).name
            println("  -> $name (id=${entry.itemId}, weight=${entry.weight})")
        }

        val roll = ThreadLocalRandom.current().nextInt(totalWeight) + 1
        println("[MegaRare] Rolled $roll / $totalWeight")

        var cumulative = 0
        for ((index, entry) in filteredEntries.withIndex()) {
            cumulative += entry.weight
            if (roll <= cumulative) {
                if (entry.itemId == -2) {
                    println("[MegaRare] Result: nothing (rolled into -2).")
                    return null
                } else {
                    val name = ItemDefinitions.getItemDefinitions(entry.itemId).name
                    println("[MegaRare] Result: $name (id=${entry.itemId})")
                    val mainDrop = entry.roll(player)
                    return mainDrop
                }
            }
        }

        println("[MegaRare] Fallback: no item matched the roll.")
        return null
    }


    companion object {
        private const val LAND_CHANCE = 3
        private const val LAND_DENOMINATOR = 128
        private const val WEIGHT_BASE = 32000
    }
}