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

class GemTableEntry : DropEntry(-1, 1..1) {
    private val table = WeightedTable()

    companion object {
        const val NOTHING_MARKER = -1
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

        add("item.uncut_sapphire", 1..1, 32)
        add("item.uncut_emerald", 1..1, 16)
        add("item.uncut_ruby", 1..1, 8)
        // removed talisman, useless, replaced with uncut_dragonstone
        add("item.uncut_diamond", 1..1, 6)
        add("item.uncut_dragonstone", 1..1, 2)
        add("item.rune_javelin", 5..5, 1)
        add("item.loop_half_of_a_key", 1..1, 1)
        add("item.tooth_half_of_a_key", 1..1, 1)

        addMarker(MEGA_RARE_MARKER, 1)
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
