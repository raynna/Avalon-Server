package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

class GemTableEntry : DropEntry(-1, 1, 1) {
    private class WeightedDropEntry(itemId: Int, min: Int, max: Int, val weight: Int) : DropEntry(itemId, min, max)

    private val entries: MutableList<WeightedDropEntry> = ArrayList()

    init {
        initTable()
        println("[DropSystem] Registered ${entries.size} Gem table drops.");
    }

    private fun initTable() {
        add("nothing", numerator = 1, denominator = 2)
        add("uncut sapphire", numerator = 1, denominator = 4)
        add("uncut emerald", numerator = 1, denominator = 8)
        add("chaos talisman", numerator = 1, denominator = 42)
        add("nature talisman", numerator = 1, denominator = 42)
        add("uncut diamond", numerator = 1, denominator = 64)
        add("rune javelin", min = 5, numerator = 1, denominator = 64)
        add("loop half of key", min = 1, numerator = 1, denominator = 128)
        add("tooth half of key", min = 1, numerator = 1, denominator = 128)
        add("mega-rare", min = 1, numerator = 1, denominator = 128)//megarare
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
                item.equals("mega-rare", ignoreCase = true) -> -3
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
            entries.filter { it.itemId != -2 }
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
                return when (entry.itemId) {
                    -2 -> {
                        println("[GemTable] Rolled 'nothing'")
                        null
                    }
                    -3 -> {
                        println("[GemTable] Rolled 'mega-rare' entry, delegating to MegaRareTable.")
                        val megaRare = DropTablesSetup.megaRareTable.roll(player)
                        println("[GemTable] MegaRare result: ${megaRare?.itemId ?: "null"}")
                        megaRare
                    }
                    else -> {
                        val drop = entry.roll(player)
                        println("[GemTable] Rolled normal item: ${drop?.itemId ?: "null"}")
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
        private const val WEIGHT_BASE = 32000
    }
}