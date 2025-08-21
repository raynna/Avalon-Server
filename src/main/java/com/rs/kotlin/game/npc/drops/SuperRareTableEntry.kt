package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import java.util.concurrent.ThreadLocalRandom

class SuperRareTableEntry : DropEntry(-1, 1..1) {

    private data class SuperRareDrop(val itemId: Int, val amount: IntRange, val numerator: Int, val denominator: Int)

    private val entries = mutableListOf<SuperRareDrop>()

    init {
        // Use helper to add drops
        add(-1, 1..1, 1941, 6400) // Nothing
        add("item.blurberry_special", 1..1, 9, 6400)
        add("item.dragon_helm", 1..1, 39, 640)
        add("item.shield_left_half", 1..1, 20, 640)
        add("item.dragon_spear", 1..1, 35, 640)
        add("item.yew_logs_noted", 675..825, 20, 640)
        add("item.super_restore_4_noted", 45..55, 30, 640)
        add("item.prayer_potion_4_noted", 45..55, 30, 640)
        add("item.raw_rocktail_noted", 180..220, 25, 640)
        add("item.mahogany_plank_noted", 270..330, 15, 640)
        add("item.dragon_longsword", 1..1, 39, 1280)
        add("item.magic_seed", 3..5, 10, 640)
        add("item.water_talisman_noted", 68..82, 8, 640)
        add("item.battlestaff_noted", 180..220, 8, 640)
        add("item.onyx_bolt_tips", 135..165, 8, 640)
        add("item.uncut_diamond_noted", 45..55, 25, 640)
        add("item.uncut_dragonstone_noted", 45..55, 10, 640)
        add("item.soul_rune", 450..550, 8, 640)
        add("item.crystal_key_noted", 9..11, 60, 640)
        add("item.white_berries_noted", 65..85, 15, 640)
        add("item.vecna_skull", 1..1, 8, 640)
        add("item.coins", 7500..12500, 8, 640)
    }

    private fun add(item: String, amount: IntRange, numerator: Int, denominator: Int) {
        val itemId = Rscm.lookup(item)
        entries.add(SuperRareDrop(itemId, amount, numerator, denominator))
    }

    // Overload for explicit itemId (like -1 for nothing)
    private fun add(itemId: Int, amount: IntRange, numerator: Int, denominator: Int) {
        entries.add(SuperRareDrop(itemId, amount, numerator, denominator))
    }

    override fun roll(player: Player): Drop? {
        val successfulDrops = mutableListOf<Drop>()

        val entriesToRoll = if (player.hasRingOfWealth()) {
            entries.filter { it.itemId != -1 }
        } else {
            entries
        }

        for (entry in entriesToRoll) {
            val roll = ThreadLocalRandom.current().nextInt(entry.denominator)
            if (roll < entry.numerator) {
                val amount = if (entry.amount.first == entry.amount.last)
                    entry.amount.first
                else
                    ThreadLocalRandom.current().nextInt(entry.amount.first, entry.amount.last + 1)

                successfulDrops.add(Drop(entry.itemId, amount))
            }
        }

        return if (successfulDrops.isEmpty()) null else successfulDrops[ThreadLocalRandom.current().nextInt(successfulDrops.size)]
    }

}
