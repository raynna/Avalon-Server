package com.rs.kotlin.game.npc.drops.rare

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropEntry
import com.rs.kotlin.game.npc.drops.DropSource
import com.rs.kotlin.game.npc.drops.ItemWeightedEntry
import com.rs.kotlin.game.npc.drops.WeightedTable

class MegaRareTableEntry : DropEntry(-1, 1..1) {
    private val table = WeightedTable()

    companion object {
        const val NOTHING_MARKER = -1
    }

    fun getEntries(): List<ItemWeightedEntry> =
        table
            .mutableEntries()
            .filterIsInstance<ItemWeightedEntry>()

    init {
        table.setSize(128)

        addMarker(NOTHING_MARKER, 113)

        add("item.rune_spear", 1..1, 8)
        add("item.shield_left_half", 1..1, 4)
        add("item.dragon_spear", 1..1, 3)

        println("[DropSystem] Registered ${table.size()} SuperRare entries.")
    }

    private fun add(
        item: String,
        amount: IntRange,
        weight: Int,
    ) {
        val itemId = Rscm.lookup(item)
        table.add(ItemWeightedEntry(itemId, amount, weight))
    }

    private fun addMarker(
        marker: Int,
        weight: Int,
    ) {
        table.add(ItemWeightedEntry(marker, 1..1, weight))
    }

    override fun roll(player: Player): Drop? {
        val entries = table.mutableEntries()

        val filtered =
            if (player.hasRingOfWealth()) {
                entries.filterNot {
                    it is ItemWeightedEntry && it.itemId == NOTHING_MARKER
                }
            } else {
                entries
            }

        if (filtered.isEmpty()) return null

        val temp = WeightedTable()
        temp.setSize(128)
        filtered.forEach { temp.add(it) }

        val result =
            temp.roll(player, source = DropSource.MEGARARE)
                ?: return null

        return if (result.itemId == NOTHING_MARKER) null else result
    }
}
