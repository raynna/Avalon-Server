package com.rs.kotlin.game.npc.drops.rare

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropContext
import com.rs.kotlin.game.npc.drops.DropEntry
import com.rs.kotlin.game.npc.drops.DropSource
import com.rs.kotlin.game.npc.drops.DropTablesSetup
import com.rs.kotlin.game.npc.drops.ItemWeightedEntry
import com.rs.kotlin.game.npc.drops.WeightedTable

class GodwarsGemTableEntry : DropEntry(-1, 1..1) {
    private val table = WeightedTable()

    private companion object {
        const val NOTHING_MARKER = -1000
        const val MEGA_RARE_MARKER = -2000
        const val TALISMAN_MARKER = -3000
    }

    fun getEntries(): List<ItemWeightedEntry> =
        table
            .mutableEntries()
            .filterIsInstance<ItemWeightedEntry>()

    init {
        table.setSize(128)

        addMarker(NOTHING_MARKER, 63)

        add("item.coins", 19500..20000, 63)
        add("item.uncut_sapphire", 32)
        add("item.uncut_emerald", 16)
        add("item.uncut_ruby", 8)
        add("item.chaos_talisman", 3)
        add("item.uncut_diamond", 2)
        add("item.rune_javelin", amount = 5..5, 1)
        add("item.loop_half_of_a_key", 1)
        add("item.tooth_half_of_a_key", 1)
        addMarker(MEGA_RARE_MARKER, 1)
    }

    private fun add(
        item: String,
        weight: Int,
    ) {
        val itemId = Rscm.lookup(item)
        table.add(ItemWeightedEntry(itemId, 1..1, weight))
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

    override fun roll(context: DropContext): Drop? {
        val entries = table.mutableEntries()

        val filtered =
            if (context.player.hasRingOfWealth()) {
                entries.filterNot { it is ItemWeightedEntry && it.itemId == NOTHING_MARKER }
            } else {
                entries
            }

        if (filtered.isEmpty()) return null

        val temp = WeightedTable()
        temp.setSize(128)
        filtered.forEach { temp.add(it) }

        val result = temp.roll(context.copy(dropSource = DropSource.GEM)) ?: return null

        return when (result.itemId) {
            NOTHING_MARKER -> {
                null
            }

            MEGA_RARE_MARKER -> {
                DropTablesSetup.megaRareTable.roll(
                    context.copy(dropSource = DropSource.MEGARARE),
                )
            }

            else -> {
                result
            }
        }
    }
}
