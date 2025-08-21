package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm

class HerbTableEntry : DropEntry(-1, 1..1) {

    private val table = WeightedTable()

    init {
        table.setSize(128)
        add("item.grimy_guam", weight = 32)
        add("item.grimy_marrentill", weight = 24)
        add("item.grimy_tarromin", weight = 18)
        add("item.grimy_harralander", weight = 14)
        add("item.grimy_ranarr", weight = 11)
        add("item.grimy_irit", weight = 8)
        add("item.grimy_avantoe", weight = 6)
        add("item.grimy_kwuarm", weight = 5)
        add("item.grimy_cadantine", weight = 4)
        add("item.grimy_lantadyme", weight = 3)
        add("item.grimy_dwarf_weed", weight = 3)

        println("[DropSystem] Registered ${table.size()} Herb table drops.")
    }

    private fun add(item: String, amount: IntRange = 1..1, weight: Int = 1) {
        val itemId = Rscm.lookup(item)
        table.add(WeightedDropEntry(itemId, amount, weight))
    }

    override fun roll(player: Player): Drop? {
        val entries = if (player.hasRingOfWealth()) {
            table.mutableEntries().filter { it.itemId != -1 }
        } else {
            table.mutableEntries()
        }

        val tempTable = WeightedTable()
        tempTable.setSize(tableSizeOrDefault())
        entries.forEach { tempTable.add(it) }

        return tempTable.roll(player)?.takeIf { it.itemId != -1 }
    }

    private fun tableSizeOrDefault() = 128
}