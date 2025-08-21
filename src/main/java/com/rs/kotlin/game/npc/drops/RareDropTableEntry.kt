package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import java.util.concurrent.ThreadLocalRandom

class RareDropTableEntry : DropEntry(-1, 1..1) {

    private data class RareDrop(val itemId: Int, val amount: IntRange, val numerator: Int, val denominator: Int)

    private val entries = mutableListOf<RareDrop>()

    init {
        add("item.uncut_dragonstone", 1..1, 8, 64)
        add("item.loop_half_of_a_key", 1..1, 6, 64)
        add("item.tooth_half_of_a_key", 1..1, 6, 64)
        add("item.magic_logs", 65..85, 4, 64)
        add("item.rune_arrowheads", 110..140, 4, 64)
        add("item.soft_clay", 35..45, 4, 64)
        add("item.teak_plank", 45..55, 2, 64)
        add("item.dragon_bones", 35..45, 2, 64)
        add("item.dragon_helm", 1..1, 2, 64)
        add("item.dragon_longsword", 1..1, 1, 128)
        add("item.molten_glass", 45..55, 4, 64)
        add("item.raw_lobster", 135..165, 4, 64)
        add(-2, 1..1, 4, 64) // Super rare table
    }

    // Helper to add drops by item name
    private fun add(item: String, amount: IntRange, numerator: Int, denominator: Int) {
        val itemId = Rscm.lookup(item)
        entries.add(RareDrop(itemId, amount, numerator, denominator))
    }

    // Helper to add drops by explicit itemId (like -2 for super rare table)
    private fun add(itemId: Int, amount: IntRange, numerator: Int, denominator: Int) {
        entries.add(RareDrop(itemId, amount, numerator, denominator))
    }

    override fun roll(player: Player): Drop? {
        val drops = mutableListOf<Drop>()

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

                val drop = when (entry.itemId) {
                    -2 -> DropTablesSetup.superRareTable.roll(player)
                    else -> Drop(entry.itemId, amount)
                }
                drop?.let { drops.add(it) }
            }
        }

        return if (drops.isEmpty()) null else drops[ThreadLocalRandom.current().nextInt(drops.size)]
    }
}
