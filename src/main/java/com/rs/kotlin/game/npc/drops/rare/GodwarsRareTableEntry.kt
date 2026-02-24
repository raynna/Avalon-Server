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

class GodwarsRareTableEntry : DropEntry(-1, 1..1) {
    private val table = WeightedTable()

    private companion object {
        const val GEM_TABLE_MARKER = -1000
        const val MEGA_RARE_MARKER = -2000
    }

    fun getEntries(): List<ItemWeightedEntry> =
        table
            .mutableEntries()
            .filterIsInstance<ItemWeightedEntry>()

    init {
        table.setSize(128)

        add("item.loop_half_of_a_key", 21)
        add("item.tooth_half_of_a_key", 20)
        add("item.coins", 19500..20000, 20)
        addMarker(GEM_TABLE_MARKER, 20)
        addMarker(MEGA_RARE_MARKER, 15)
        add("item.rune_sword", 5)
        add("item.nature_rune", amount = 62..67, 3)
        add("item.rune_2h_sword", 3)
        add("item.rune_battleaxe", 3)
        add("item.law_rune", amount = 40..45, 2)
        add("item.death_rune", amount = 40..45, 2)
        add("item.steel_arrow", amount = 145..150, 2)
        add("item.rune_arrow", amount = 38..43, 2)
        add("item.adamant_javelin", amount = 15..20, 2)
        add("item.rune_sq_shield", 2)
        add("item.dragonstone", 2)
        add("item.silver_ore_noted", amount = 100..100, 2)
        add("item.rune_kiteshield", 1)
        add("item.dragon_helm", 1)
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
        val rareContext = context.copy(dropSource = DropSource.RARE)

        val result = table.roll(rareContext) ?: return null

        return when (result.itemId) {
            GEM_TABLE_MARKER -> {
                DropTablesSetup.gemDropTable
                    .roll(rareContext.copy(dropSource = DropSource.GEM))
            }

            MEGA_RARE_MARKER -> {
                DropTablesSetup.megaRareTable
                    .roll(rareContext.copy(dropSource = DropSource.MEGARARE))
            }

            else -> {
                result
            }
        }
    }
}
