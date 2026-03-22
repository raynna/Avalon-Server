package raynna.game.npc.drops.herb

import raynna.game.player.Player
import raynna.game.npc.drops.Drop
import raynna.game.npc.drops.DropContext
import raynna.game.npc.drops.DropEntry
import raynna.game.npc.drops.DropSource
import raynna.game.npc.drops.weighted.ItemWeightedEntry
import raynna.game.npc.drops.weighted.WeightedTable
import raynna.data.rscm.Rscm

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

    override fun roll(context: DropContext): Drop? {
        val entries = table.mutableEntries()

        val filtered =
            if (context.player.hasRingOfWealth()) {
                entries.filterIsInstance<ItemWeightedEntry>()
            } else {
                entries
            }

        if (filtered.isEmpty()) return null

        val tempTable = WeightedTable()
        tempTable.setSize(128)

        filtered.forEach { tempTable.add(it) }

        return tempTable.roll(
            context.copy(
                dropSource = DropSource.HERB,
                receivedDrop = true,
            ),
        )
    }

    private fun tableSizeOrDefault() = 128

    fun getEntries(): List<ItemWeightedEntry> =
        table
            .mutableEntries()
            .filterIsInstance<ItemWeightedEntry>()
}
