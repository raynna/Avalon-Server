package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import java.util.concurrent.ThreadLocalRandom

class RareDropTableEntry : DropEntry(-1, 1..1) {

    private val table = WeightedTable()

    init {
        initTable()
        println("[DropSystem] Registered ${table.size()} rare table drops.")
    }

    private fun initTable() {
        add("nothing", 9, 64)
        add("item.uncut_dragonstone", 8, 64)
        add("item.loop_half_of_a_key", 6, 64)
        add("item.tooth_half_of_a_key", 6, 64)
        add("item.rune_platelegs", 5, 64)
        add("item.magic_logs_noted", 65..85, 4, 64)
        add("item.rune_arrowheads", 110..140, 4, 64)
        add("item.soft_clay_noted", 35..45, 4, 64)
        add("item.teak_plank_noted", 45..55, 2, 64)
        add("item.dragon_bones_noted", 35..45, 2, 64)
        add("item.dragon_helm", 2, 64)
        add("item.dragon_longsword", 1, 128)
        add("item.molten_glass_noted", 45..55, 4, 64)
        add("item.runite_ore_noted", 25..35, 4, 64)
        add("item.raw_lobster_noted", 135..165, 1, 64)
        add("super-rare", 4, 64)
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
            "super-rare" -> -2
            else -> Rscm.lookup(item)
        }

        if (itemId == null) return

        table.add(WeightedDropEntry(
            itemId = itemId,
            amount = amount,
            numerator = numerator,
            denominator = denominator
        ))
    }

    override fun roll(player: Player): Drop? {
        val filteredEntries = if (player.hasRingOfWealth()) {
            table.mutableEntries().filter { it.itemId != -1 }
        } else {
            table.mutableEntries()
        }

        val filteredTable = WeightedTable().apply {
            filteredEntries.forEach { add(it) }
        }

        val drop = filteredTable.roll(player) ?: return null

        return when (drop.itemId) {
            -1 -> null
            -2 -> DropTablesSetup.superRareTable.roll(player)
            else -> drop
        }
    }
}
