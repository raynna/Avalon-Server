package raynna.game.npc.drops.rare

import raynna.game.player.Player
import raynna.game.npc.drops.Drop
import raynna.game.npc.drops.DropContext
import raynna.game.npc.drops.DropEntry
import raynna.game.npc.drops.DropSource
import raynna.game.npc.drops.DropTablesSetup
import raynna.game.npc.drops.weighted.ItemWeightedEntry
import raynna.game.npc.drops.weighted.WeightedTable
import raynna.data.rscm.Rscm

class RareDropTableEntry : DropEntry(-1, 1..1) {
    val table = WeightedTable()

    companion object {
        const val GEM_TABLE_MARKER = -1000
        const val MEGA_RARE_MARKER = -2000
    }

    fun getEntries(): List<ItemWeightedEntry> =
        table
            .mutableEntries()
            .filterIsInstance<ItemWeightedEntry>()

    init {
        table.setSize(128)

        add("item.nature_rune", 67..67, 3)
        add("item.adamant_javelin", 20..20, 2)
        add("item.death_rune", 45..45, 2)
        add("item.law_rune", 45..45, 2)
        add("item.rune_arrow", 42..42, 2)
        add("item.steel_arrow", 150..150, 2)

        add("item.rune_2h_sword", 1..1, 3)
        add("item.rune_battleaxe", 1..1, 3)
        add("item.rune_sq_shield", 1..1, 2)
        add("item.dragon_helm", 1..1, 1)
        add("item.rune_kiteshield", 1..1, 1)

        add("item.coins", 3000..3000, 21)
        add("item.loop_half_of_a_key", 1..1, 20)
        add("item.tooth_half_of_a_key", 1..1, 20)
        add("item.rune_bar", 1..1, 5)
        add("item.dragonstone", 1..1, 2)
        add("item.silver_ore_noted", 100..100, 2)

        addMarker(GEM_TABLE_MARKER, 20)
        addMarker(MEGA_RARE_MARKER, 15)
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
        val result = table.roll(context.copy(dropSource = DropSource.RARE)) ?: return null

        return when (result.itemId) {
            GEM_TABLE_MARKER -> DropTablesSetup.gemDropTable.roll(context)
            MEGA_RARE_MARKER -> DropTablesSetup.megaRareTable.roll(context)
            else -> result
        }
    }
}
