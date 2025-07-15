package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class SuperRareTableEntry : DropEntry(-1, 1, 1) {
    private class WeightedDropEntry(itemId: Int, min: Int, max: Int, val weight: Double) : DropEntry(itemId, min, max)

    private val entries: MutableList<WeightedDropEntry> = ArrayList()

    init {
        initTable()
        println("[DropSystem] Registered ${entries.size} Super rare table drops.");
    }

    private fun initTable() {
        add("nothing", numerator = 1941, denominator = 6400)
        add("onyx ring", numerator = 1, denominator = 6400)
        add("blurberry special", numerator = 9, denominator = 6400)
        add("dragon helm", numerator = 39, denominator = 640)
        add("shield left half", numerator = 20, denominator = 640)
        add("dragon spear", numerator = 35, denominator = 640)
        add("yew logs#noted", min = 675, max = 825, numerator = 20, denominator = 640)
        add("super restore (4)#noted", min = 45, max = 55, numerator = 30, denominator = 640)
        add("prayer potion (4)#noted", min = 45, max = 55, numerator = 30, denominator = 640)
        add("raw rocktail#noted", min = 180, max = 220, numerator = 25, denominator = 640)
        add("mahogany plank#noted", min = 270, max = 330, numerator = 15, denominator = 640)
        add("dragon longsword", numerator = 39, denominator = 1280)
        add("magic seed", min = 3, max = 5, numerator = 10, denominator = 640)
        add("water talisman#noted", min = 68, max = 82, numerator = 8, denominator = 640)
        add("battlestaff#noted", min = 180, max = 220, numerator = 8, denominator = 640)
        add("onyx bolt tips", min = 135, max = 165, numerator = 8, denominator = 640)
        add("uncut diamond#noted", min = 45, max = 55, numerator = 25, denominator = 640)
        add("uncut dragonstone#noted", min = 45, max = 55, numerator = 10, denominator = 640)
        add("soul rune", min = 450, max = 550, numerator = 8, denominator = 640)
        add("crystal key#noted", min = 9, max = 11, numerator = 60, denominator = 640)
        add("white berries#noted", min = 65, max = 85, numerator = 15, denominator = 640)
        add("vecna skull", numerator = 8, denominator = 640)
        add("coins", min = 7500, max = 12500, numerator = 8, denominator = 640)
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

        var weight = numerator.toDouble() / denominator
        if (weight < 0) weight = 0.0
        entries.add(WeightedDropEntry(itemId, min, max, weight))
    }


    override fun roll(player: Player?): Drop? {
        val filteredEntries = if (player?.hasRingOfWealth() == true) {
            entries.filter { it.itemId != -2 }
        } else {
            entries
        }

        if (filteredEntries.isEmpty()) {
            return null
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
                if (entry.itemId == -2) {
                    println("[SuperRare] Result: nothing (rolled into -2).")
                    return null
                } else {
                    val name = ItemDefinitions.getItemDefinitions(entry.itemId).name
                    println("[SuperRare] Result: $name (id=${entry.itemId})")
                    val mainDrop = entry.roll(player)
                    return mainDrop
                }
            }
        }

        return null
    }


    companion object {
        private const val WEIGHT_BASE = 32000
    }
}