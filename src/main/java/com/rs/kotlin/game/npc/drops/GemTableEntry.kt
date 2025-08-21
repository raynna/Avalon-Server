package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import java.util.concurrent.ThreadLocalRandom

class GemTableEntry : DropEntry(-1, 1..1) {

    private data class GemDrop(val itemId: Int, val amount: IntRange, val numerator: Int, val denominator: Int)

    private val entries = mutableListOf<GemDrop>()

    init {
        add("item.coins", 250..499, 63, 128)
        add("item.coins", 50..50, 71, 128)
        add("item.uncut_sapphire", 1..1, 31, 128)
        add("item.uncut_emerald", 1..1, 16, 128)
        add("item.uncut_ruby", 1..1, 8, 128)
        add("item.uncut_diamond", 1..1, 2, 128)
        add("item.rune_javelin", 5..5, 4, 128)
        add("item.uncut_dragonstone", 1..1, 1, 128)
        add("item.tooth_half_of_a_key", 1..1, 1, 128)
        add("item.loop_half_of_a_key", 1..1, 1, 128)
        add(-2, 1..1, 1, 128) // Rare drop table
    }

    private fun add(item: String, amount: IntRange, numerator: Int, denominator: Int) {
        val itemId = Rscm.lookup(item)
        entries.add(GemDrop(itemId, amount, numerator, denominator))
    }

    private fun add(itemId: Int, amount: IntRange, numerator: Int, denominator: Int) {
        entries.add(GemDrop(itemId, amount, numerator, denominator))
    }

    override fun roll(player: Player): Drop? {
        val landRoll = ThreadLocalRandom.current().nextInt(64)
        if (landRoll != 0) return null
        val successfulDrops = mutableListOf<Drop>()

        for (entry in entries) {
            val roll = ThreadLocalRandom.current().nextInt(entry.denominator)
            if (roll < entry.numerator) {
                val amount = if (entry.amount.first == entry.amount.last)
                    entry.amount.first
                else
                    ThreadLocalRandom.current().nextInt(entry.amount.first, entry.amount.last + 1)

                val drop = when (entry.itemId) {
                    -2 -> DropTablesSetup.rareDropTable.roll(player)
                    else -> Drop(entry.itemId, amount)
                }
                drop?.let { successfulDrops.add(it) }
            }
        }

        return if (successfulDrops.isEmpty()) null else successfulDrops[ThreadLocalRandom.current().nextInt(successfulDrops.size)]
    }
}
