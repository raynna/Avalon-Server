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
        val itemId = Rscm.lookup(item)
        val weight = WeightedDropEntry.calculateWeight(numerator, denominator)
        table.add(
            WeightedDropEntry(itemId, 1..1, numerator, denominator)
        )
    }

    override fun roll(player: Player): Drop? {
        val chanceRoll = ThreadLocalRandom.current().nextInt(LAND_DENOMINATOR)
        if (chanceRoll >= LAND_NUMERATOR) return null

        return table.roll(player)
    }

    companion object {
        private const val LAND_NUMERATOR = 2
        private const val LAND_DENOMINATOR = 128
    }
}
