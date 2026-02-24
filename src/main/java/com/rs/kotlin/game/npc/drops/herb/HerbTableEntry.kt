package com.rs.kotlin.game.npc.drops.herb

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropEntry
import com.rs.kotlin.game.npc.drops.DropSource
import com.rs.kotlin.game.npc.drops.ItemWeightedEntry
import com.rs.kotlin.game.npc.drops.WeightedTable

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

    private fun add(
        item: String,
        amount: IntRange = 1..1,
        weight: Int = 1,
    ) {
        val itemId = Rscm.lookup(item)
        table.add(ItemWeightedEntry(itemId, amount, weight))
    }

    override fun roll(player: Player): Drop? {
        val entries = table.mutableEntries()

        val filtered =
            if (player.hasRingOfWealth()) {
                entries.filterIsInstance<ItemWeightedEntry>()
            } else {
                entries
            }

        if (filtered.isEmpty()) return null

        val tempTable = WeightedTable()
        tempTable.setSize(128)

        filtered.forEach { tempTable.add(it) }

        return tempTable.roll(player, source = DropSource.HERB)
    }

    private fun tableSizeOrDefault() = 128

    fun getEntries(): List<ItemWeightedEntry> =
        table
            .mutableEntries()
            .filterIsInstance<ItemWeightedEntry>()
}
