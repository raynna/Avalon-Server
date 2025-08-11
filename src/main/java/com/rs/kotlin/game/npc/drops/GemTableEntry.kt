package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import java.util.concurrent.ThreadLocalRandom

class GemTableEntry : DropEntry(-1, 1..1) {

    private val table = WeightedTable()

    init {
        add("nothing", 4, 128)
        add("item.coins", 250..499, 63, 128)
        add("item.uncut_sapphire", 31, 128)
        add("item.uncut_emerald", 16, 128)
        add("item.uncut_ruby", 8, 128)
        add("item.uncut_diamond", 2, 128)
        add("item.rune_javelin", 5, 4, 128)
        add("item.chaos_talisman", 3, 128)
        add("item.uncut_dragonstone", 1, 128)
        add("item.tooth_half_of_a_key", 1, 128)
        add("item.loop_half_of_a_key", 1, 128)
        add("rare-table", 1, 128)

        println("[DropSystem] Registered ${table.size()} Gem table drops.")
    }

    private fun add(item: String, numerator: Int, denominator: Int) {
        add(item, 1..1, numerator, denominator)
    }

    private fun add(item: String, amount: Int, numerator: Int, denominator: Int) {
        add(item, amount..amount, numerator, denominator)
    }

    private fun add(item: String, amount: IntRange, numerator: Int, denominator: Int) {
        require(numerator in 1..denominator) { "Invalid drop rate: $numerator/$denominator" }

        val itemId = when (item.lowercase()) {
            "nothing" -> -1
            "rare-table" -> -2
            else -> Rscm.lookup(item)
        }

        table.add(
            WeightedDropEntry(
            itemId = itemId,
            amount = amount,
            numerator = numerator,
            denominator = denominator
        )
        )
    }

    override fun roll(player: Player): Drop? {
        val chance = ThreadLocalRandom.current().nextInt(LAND_DENOMINATOR)
        if (chance >= LAND_CHANCE) return null

        val entries = if (player.hasRingOfWealth()) {
            table.mutableEntries().filter { it.itemId != -1 }
        } else {
            table.mutableEntries()
        }

        val tempTable = WeightedTable()
        entries.forEach { tempTable.add(it) }

        val drop = tempTable.roll(player) ?: return null

        return when (drop.itemId) {
            -1 -> null
            -2 -> DropTablesSetup.rareDropTable.roll(player)
            else -> drop
        }
    }

    companion object {
        private const val LAND_CHANCE = 3
        private const val LAND_DENOMINATOR = 128
    }
}
