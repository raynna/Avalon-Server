package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm

class SuperRareTableEntry : DropEntry(-1, 1..1) {

    private val table = WeightedTable()

    init {
        initTable()
        println("[DropSystem] Registered ${table.size()} Super rare table drops.")
    }

    private fun initTable() {
        add("nothing", 1941, 6400)
        add("item.onyx_ring", 1, 6400)
        add("item.blurberry_special", 9, 6400)
        add("item.dragon_helm", 39, 640)
        add("item.shield_left_half", 20, 640)
        add("item.dragon_spear", 35, 640)
        add("item.yew_logs_noted", 675..825, 20, 640)
        add("item.super_restore_4_noted", 45..55, 30, 640)
        add("item.prayer_potion_4_noted", 45..55, 30, 640)
        add("item.raw_rocktail_noted", 180..220, 25, 640)
        add("item.mahogany_plank_noted", 270..330, 15, 640)
        add("item.dragon_longsword", 39, 1280)
        add("item.magic_seed", 3..5, 10, 640)
        add("item.water_talisman_noted", 68..82, 8, 640)
        add("item.battlestaff_noted", 180..220, 8, 640)
        add("item.onyx_bolt_tips", 135..165, 8, 640)
        add("item.uncut_diamond_noted", 45..55, 25, 640)
        add("item.uncut_dragonstone_noted", 45..55, 10, 640)
        add("item.soul_rune", 450..550, 8, 640)
        add("item.crystal_key_noted", 9..11, 60, 640)
        add("item.white_berries_noted", 65..85, 15, 640)
        add("item.vecna_skull", 8, 640)
        add("item.coins", 7500..12500, 8, 640)
    }

    private fun add(item: String, numerator: Int, denominator: Int) {
        add(item, 1..1, numerator, denominator)
    }

    private fun add(item: String, amount: Int, numerator: Int, denominator: Int) {
        add(item, amount..amount, numerator, denominator)
    }

    private fun add(item: String, amount: IntRange, numerator: Int, denominator: Int) {
        require(numerator in 1..denominator) { "Invalid drop rate: $numerator/$denominator for $item" }

        val itemId = when {
            item.equals("nothing", ignoreCase = true) -> -1
            else -> Rscm.lookup(item)
        } ?: return

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
        val filteredEntries = if (player.hasRingOfWealth()) {
            table.mutableEntries().filter { it.itemId != -1 }
        } else {
            table.mutableEntries()
        }

        if (filteredEntries.isEmpty()) return null

        val filteredTable = WeightedTable().apply {
            filteredEntries.forEach { add(it) }
        }

        val drop = filteredTable.roll(player) ?: return null
        return if (drop.itemId == -1) null else drop
    }
}
