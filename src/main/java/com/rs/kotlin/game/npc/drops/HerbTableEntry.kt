package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import java.util.concurrent.ThreadLocalRandom

class HerbTableEntry : DropEntry(-1, 1..1) {

    private val table = WeightedTable()

    init {
        add("item.grimy_guam", 32, 128)
        add("item.grimy_marrentill", 24, 128)
        add("item.grimy_tarromin", 18, 128)
        add("item.grimy_harralander", 14, 128)
        add("item.grimy_ranarr", 11, 128)
        add("item.grimy_irit", 8, 128)
        add("item.grimy_avantoe", 6, 128)
        add("item.grimy_kwuarm", 5, 128)
        add("item.grimy_cadantine", 4, 128)
        add("item.grimy_lantadyme", 3, 128)
        add("item.grimy_dwarf_weed", 3, 128)

        println("[DropSystem] Registered ${table.size()} Herb table drops.")
    }

    private fun add(item: String, numerator: Int, denominator: Int) {
        add(item, 1..1, numerator, denominator)
    }

    private fun add(item: String, amount: IntRange, numerator: Int, denominator: Int) {
        require(numerator in 1..denominator) { "Invalid drop rate: $numerator/$denominator" }
        val itemId = Rscm.lookup(item)
        table.add(WeightedDropEntry(itemId, amount, numerator, denominator))
    }

    override fun roll(player: Player): Drop? {

        val chance = ThreadLocalRandom.current().nextInt(LAND_DENOMINATOR)

        if (chance >= LAND_CHANCE) {
            return null
        }

        val entries = if (player.hasRingOfWealth()) {
            table.mutableEntries().filter { it.itemId != -1 }
        } else {
            table.mutableEntries()
        }


        val tempTable = WeightedTable()
        entries.forEach { tempTable.add(it) }

        val drop = tempTable.roll(player)

        if (drop == null) {
            return null
        }


        return when (drop.itemId) {
            -1 -> {
                null
            }
            else -> drop
        }
    }

    companion object {
        private const val LAND_CHANCE = 64  // numerator
        private const val LAND_DENOMINATOR = 128
    }
}
