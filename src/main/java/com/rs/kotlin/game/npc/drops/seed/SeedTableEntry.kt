package com.rs.kotlin.game.npc.drops.seed

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropEntry
import com.rs.kotlin.game.npc.drops.DropSource
import com.rs.kotlin.game.npc.drops.ItemWeightedEntry
import com.rs.kotlin.game.npc.drops.WeightedTable
import java.util.concurrent.ThreadLocalRandom

class SeedTableEntry : DropEntry(-1, 1..1) {
    fun getRareEntries() = rareTable.mutableEntries().filterIsInstance<ItemWeightedEntry>()

    fun getUncommonEntries() = uncommonTable.mutableEntries().filterIsInstance<ItemWeightedEntry>()

    fun getTreeHerbEntries() = treeHerbTable.mutableEntries().filterIsInstance<ItemWeightedEntry>()

    private data class SubTable(
        val min: Int,
        val max: Int,
        val table: WeightedTable,
    )

    private val generalSubTables = mutableListOf<SubTable>()

    private val rareTable = WeightedTable()
    private val uncommonTable = WeightedTable()
    private val treeHerbTable = WeightedTable()

    init {
        setupGeneral()
        setupRare()
        setupUncommon()
        setupTreeHerb()

        println("[DropSystem] Seed tables initialized.")
    }

    private fun setupGeneral() {
        // 0 - 484 (total weight = 1008)
        val low =
            weighted(1000) {
                add("item.potato_seed", 4..4, 368)
                add("item.onion_seed", 4..4, 276)
                add("item.cabbage_seed", 4..4, 184)
                add("item.tomato_seed", 3..3, 92)
                add("item.sweetcorn_seed", 3..3, 46)
                add("item.strawberry_seed", 2..2, 23)
                add("item.watermelon_seed", 2..2, 11)
            }
        generalSubTables += SubTable(0, 485, low)

        // 485 - 727 (total weight = 1000)
        val allotment =
            weighted(1000) {
                add("item.barley_seed", 4..4, 229)
                add("item.hammerstone_seed", 3..3, 228)
                add("item.asgarnian_seed", 3..3, 172)
                add("item.jute_seed", 2..2, 171)
                add("item.yanillian_seed", 2..2, 114)
                add("item.krandorian_seed", 2..2, 57)
                add("item.wildblood_seed", 1..1, 29)
            }
        generalSubTables += SubTable(485, 728, allotment)

        val flowers =
            weighted(1000) {
                add("item.marigold_seed", 1..1, 376)
                add("item.nasturtium_seed", 1..1, 249)
                add("item.rosemary_seed", 1..1, 161)
                add("item.woad_seed", 1..1, 119)
                add("item.limpwurt_seed", 1..1, 95)
            }
        generalSubTables += SubTable(728, 850, flowers)

        val berries =
            weighted(1000) {
                add("item.redberry_seed", 1..1, 400)
                add("item.cadavaberry_seed", 1..1, 280)
                add("item.dwellberry_seed", 1..1, 200)
                add("item.jangerberry_seed", 1..1, 80)
                add("item.whiteberry_seed", 1..1, 29)
                add("item.poison_ivy_seed", 1..1, 11)
            }
        generalSubTables += SubTable(850, 947, berries)

        val herbs =
            weighted(1000) {
                add("item.guam_seed", 1..1, 320)
                add("item.marrentill_seed", 1..1, 218)
                add("item.tarromin_seed", 1..1, 149)
                add("item.harralander_seed", 1..1, 101)
                add("item.ranarr_seed", 1..1, 69)
                add("item.toadflax_seed", 1..1, 47)
                add("item.irit_seed", 1..1, 32)
                add("item.avantoe_seed", 1..1, 22)
                add("item.kwuarm_seed", 1..1, 15)
                add("item.snapdragon_seed", 1..1, 10)
                add("item.cadantine_seed", 1..1, 7)
                add("item.lantadyme_seed", 1..1, 5)
                add("item.dwarf_weed_seed", 1..1, 3)
                add("item.torstol_seed", 1..1, 2)
            }
        generalSubTables += SubTable(947, 995, herbs)

        // 995+ (total weight = 1100)
        val special =
            weighted(1100) {
                add("item.bittercap_mushroom_spore", 1..1, 500)
                add("item.belladonna_seed", 1..1, 300)
                add("item.cactus_seed", 1..1, 200)
                add("item.morchella_mushroom_spore", 1..1, 100)
            }
        generalSubTables += SubTable(995, Int.MAX_VALUE, special)
    }

    private fun setupRare() {
        rareTable.setSize(1000)

        rareTable.add("item.toadflax_seed", 1..1, 216)
        rareTable.add("item.irit_seed", 1..1, 148)
        rareTable.add("item.belladonna_seed", 1..1, 143)
        rareTable.add("item.avantoe_seed", 1..1, 103)
        rareTable.add("item.poison_ivy_seed", 1..1, 101)
        rareTable.add("item.cactus_seed", 1..1, 96)
        rareTable.add("item.kwuarm_seed", 1..1, 69)
        rareTable.add("item.snapdragon_seed", 1..1, 46)
        rareTable.add("item.cadantine_seed", 1..1, 32)
        rareTable.add("item.lantadyme_seed", 1..1, 23)
        rareTable.add("item.dwarf_weed_seed", 1..1, 14)
        rareTable.add("item.torstol_seed", 1..1, 9)
    }

    private fun setupUncommon() {
        uncommonTable.setSize(1008)

        uncommonTable.add("item.limpwurt_seed", 1..1, 137)
        uncommonTable.add("item.strawberry_seed", 1..1, 131)
        uncommonTable.add("item.marrentill_seed", 1..1, 125)
        uncommonTable.add("item.jangerberry_seed", 1..1, 92)
        uncommonTable.add("item.tarromin_seed", 1..1, 85)
        uncommonTable.add("item.wildblood_seed", 1..1, 83)
        uncommonTable.add("item.watermelon_seed", 1..1, 63)
        uncommonTable.add("item.harralander_seed", 1..1, 56)
        uncommonTable.add("item.ranarr_seed", 1..1, 39)
        uncommonTable.add("item.whiteberry_seed", 1..1, 34)
        uncommonTable.add("item.bittercap_mushroom_spore", 1..1, 29)
        uncommonTable.add("item.toadflax_seed", 1..1, 27)
        uncommonTable.add("item.belladonna_seed", 1..1, 18)
        uncommonTable.add("item.irit_seed", 1..1, 18)
        uncommonTable.add("item.poison_ivy_seed", 1..1, 13)
        uncommonTable.add("item.avantoe_seed", 1..1, 12)
        uncommonTable.add("item.cactus_seed", 1..1, 12)
        uncommonTable.add("item.kwuarm_seed", 1..1, 9)
        uncommonTable.add("item.morchella_mushroom_spore", 1..1, 8)
        uncommonTable.add("item.snapdragon_seed", 1..1, 5)
        uncommonTable.add("item.cadantine_seed", 1..1, 4)
        uncommonTable.add("item.lantadyme_seed", 1..1, 3)
        uncommonTable.add("item.dwarf_weed_seed", 1..1, 2)
        uncommonTable.add("item.torstol_seed", 1..1, 1)
    }

    private fun setupTreeHerb() {
        treeHerbTable.setSize(200)

        treeHerbTable.add("item.ranarr_seed", 1..1, 30)
        treeHerbTable.add("item.snapdragon_seed", 1..1, 28)
        treeHerbTable.add("item.torstol_seed", 1..1, 22)
        treeHerbTable.add("item.watermelon_seed", 15..15, 21)
        treeHerbTable.add("item.willow_seed", 1..1, 20)
        treeHerbTable.add("item.maple_seed", 1..1, 18)
        treeHerbTable.add("item.yew_seed", 1..1, 18)
        treeHerbTable.add("item.papaya_tree_seed", 1..1, 14)
        treeHerbTable.add("item.magic_seed", 1..1, 11)
        treeHerbTable.add("item.palm_tree_seed", 1..1, 10)
        treeHerbTable.add("item.spirit_seed", 1..1, 8)
    }

    fun roll(
        type: SeedTableType,
        player: Player,
        combatLevel: Int,
    ): Drop? =
        when (type) {
            SeedTableType.GENERAL -> {
                rollGeneral(player, combatLevel)
            }

            SeedTableType.RARE -> {
                rareTable.roll(player, source = DropSource.SEED)
            }

            SeedTableType.UNCOMMON -> {
                uncommonTable.roll(player, source = DropSource.SEED)
            }

            SeedTableType.TREE_HERB -> {
                treeHerbTable.roll(player, source = DropSource.SEED)
            }
        }

    private fun rollGeneral(
        player: Player,
        combatLevel: Int,
    ): Drop? {
        val combatCalc = combatLevel * 10
        if (combatCalc <= 0) return null

        val roll = ThreadLocalRandom.current().nextInt(combatCalc)

        val sub =
            generalSubTables.firstOrNull {
                roll >= it.min && roll < it.max
            } ?: return null

        return sub.table.roll(player, source = DropSource.SEED)
    }

    private fun weighted(
        size: Int,
        block: WeightedTable.() -> Unit,
    ): WeightedTable {
        val table = WeightedTable()
        table.setSize(size)
        table.block()
        return table
    }

    private fun WeightedTable.add(
        item: String,
        amount: IntRange,
        weight: Int,
    ) {
        val id = Rscm.lookup(item)
        add(ItemWeightedEntry(id, amount, weight))
    }
}
