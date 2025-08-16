package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import kotlin.random.Random

class SummoningCharms(private val dropTable: DropTable) {

    private data class CharmEntry(
        val itemId: Int,
        val amount: Int,
        val percent: Double
    )

    private val entries = mutableListOf<CharmEntry>()

    fun gold(amount: Int = 1, percent: Double) = add("item.gold_charm", amount, percent)
    
    fun green(amount: Int = 1, percent: Double) = add("item.green_charm", amount, percent)
    fun crimson(amount: Int = 1, percent: Double) = add("item.crimson_charm", amount, percent)
    fun blue(amount: Int = 1, percent: Double) = add("item.blue_charm", amount, percent)

    private fun add(itemName: String, amount: Int, percent: Double) {
        val itemId = Rscm.lookup(itemName)
        entries.add(CharmEntry(itemId, amount, percent))
    }

    fun roll(): Drop? {
        val totalChance = entries.sumOf { it.percent }
        if (totalChance == 0.0) return null

        val roll = Random.nextDouble(100.0)
        var acc = 0.0

        for (entry in entries) {
            acc += entry.percent
            if (roll < acc) {
                return Drop(entry.itemId, entry.amount)
            }
        }

        return null
    }
}
