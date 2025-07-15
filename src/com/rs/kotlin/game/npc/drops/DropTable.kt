package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.kotlin.game.npc.drops.SummoningCharms.CharmType
import com.rs.java.game.player.Player
import java.util.concurrent.ThreadLocalRandom

fun dropTable(rolls: Int = 1, block: DropTable.() -> Unit): DropTable {
    val table = DropTable(rolls)
    table.block()
    return table
}

class DropTable(private val rolls: Int = 1) {
    private val alwaysDrops = mutableListOf<DropEntry>()
    private val preRollDrops = mutableListOf<DropEntry>()
    private val mainDrops = mutableListOf<WeightedDropEntry>()
    private val tertiaryDrops = mutableListOf<TertiaryDropEntry>()
    private val specialDrops = mutableListOf<WeightedDropEntry>()
    private val percentDrops = mutableListOf<PercentDropEntry>()

    private var rareTableRoller: ((Player?, MutableList<Drop>) -> Boolean)? = null


    fun alwaysDrops(block: MutableList<DropEntry>.() -> Unit) {
        alwaysDrops.block()
    }

    fun preRollDrops(block: MutableList<DropEntry>.() -> Unit) {
        preRollDrops.block()
    }

    fun mainDrops(block: MutableList<WeightedDropEntry>.() -> Unit) {
        mainDrops.block()
    }

    fun tertiaryDrops(block: MutableList<TertiaryDropEntry>.() -> Unit) {
        tertiaryDrops.block()
    }

    fun specialDrops(block: MutableList<WeightedDropEntry>.() -> Unit) {
        specialDrops.block()
    }

    fun percentDrops(block: MutableList<PercentDropEntry>.() -> Unit) {
        percentDrops.block()
    }

    fun rareTable(block: (Player?, MutableList<Drop>) -> Boolean) {
        rareTableRoller = block
    }

    fun DropTable.alwaysDrop(item: Any, min: Int = 1, max: Int = min) =
        addDrop(context = DropType.ALWAYS, item, min, max)

    fun DropTable.mainDrop(item: Any, amount: Int = 1, probability: Int = 1, chance: Int = 4) =
        addDrop(context = DropType.MAIN, item, amount, amount, probability, chance)

    fun DropTable.mainDrop(item: Any, min: Int = 1, max: Int = 1, probability: Int = 1, chance: Int = 4) =
        addDrop(context = DropType.MAIN, item, min, max, probability, chance)

    fun DropTable.mainDrop(item: Any, min: Int = 1, max: Int = min, probability: Int = 1, chance: Int = 4, condition: ((Player?) -> Boolean)? = null, customLogic: ((Player?, Drop) -> Unit)? = null) {
        addDrop(context = DropType.MAIN, item = item, min = min, max = max, numerator = probability, denominator = chance, condition = condition, customLogic = customLogic)
    }

    fun DropTable.preRollDrop(item: Any, condition: (Player?) -> Boolean) =
        addDrop(context = DropType.PREROLL, item, condition = condition)

    fun DropTable.tertiaryDrop(item: Any, probability: Int, chance: Int, condition: (Player?) -> Boolean) =
        addDrop(context = DropType.TERTIARY, item = item, numerator = probability, denominator = chance, condition = condition)

    fun DropTable.tertiaryDrop(item: Any, amount: Int = 1, probability: Int = 1, chance: Int = 64) =
        addDrop(context = DropType.TERTIARY, item, amount, amount, probability, chance)

    fun DropTable.percentDrop(item: Any, percent: Double) =
        addDrop(context = DropType.PERCENT,  item = item, percent = percent)

    fun DropTable.percentDrop(item: Any, amount: Int, percent: Double) {
        addDrop(context = DropType.PERCENT, item = item, min = amount, max = amount, percent = percent)
    }

    fun addDrop(
        context: DropType,
        item: Any, // item ID or name
        min: Int = 1,
        max: Int = min,
        numerator: Int = -1,
        denominator: Int = -1,
        percent: Double = 0.0,
        condition: ((Player?) -> Boolean)? = null,
        customLogic: ((Player?, Drop) -> Unit)? = null
    ) {
        // Resolve item ID
        val id = when (item) {
            is Int -> item
            is String -> ItemDefinitions.searchItems(item, 1).firstOrNull()?.id
            else -> null
        }

        if (id == null) {
            System.err.println("Unknown item: $item")
            return
        }

        when (context) {
            DropType.ALWAYS, DropType.PREROLL -> {
                val entry = if (condition != null) {
                    object : DropEntry(id, min, max) {
                        override fun shouldDrop(player: Player?) = condition(player)
                    }
                } else {
                    DropEntry(id, min, max)
                }

                if (context == DropType.ALWAYS) alwaysDrops.add(entry)
                else preRollDrops.add(entry)
            }

            DropType.MAIN, DropType.SPECIAL -> {
                val weight = if (numerator > 0 && denominator > 0) (1024 * numerator) / denominator else 0

                val entry = if (customLogic != null) {
                    object : WeightedDropEntry(id, min, max, weight) {
                        override fun roll(player: Player?): Drop {
                            return super.roll(player)?.apply { customLogic(player, this) } ?: Drop(0, 0)
                        }
                    }
                } else {
                    WeightedDropEntry(id, min, max, weight)
                }

                if (context == DropType.MAIN) mainDrops.add(entry)
                else specialDrops.add(entry)
            }

            DropType.TERTIARY -> {
                if (numerator <= 0 || denominator <= 0) {
                    System.err.println("Invalid tertiary drop rate for item $item")
                    return
                }

                val entry = if (condition != null) {
                    object : TertiaryDropEntry(id!!, min, numerator, denominator) {
                        override fun shouldDrop(player: Player?) = condition(player)
                    }
                } else {
                    TertiaryDropEntry(id!!, min, numerator, denominator)
                }

                tertiaryDrops.add(entry)
            }
            DropType.PERCENT -> {
                if (id == null) {
                    System.err.println("Percent drop requires a valid item ID or name")
                    return
                }
                if (percent <= 0.0 || percent > 100.0) {
                    System.err.println("Invalid percent drop chance: $percent for item $item")
                    return
                }

                percentDrops.add(
                    PercentDropEntry(id, min, max, percent, condition)
                )
            }
        }
    }



    fun rollDrops(player: Player?): List<Drop> {
        val drops = mutableListOf<Drop>()

        alwaysDrops.forEach { it.roll(player)?.let { drop -> drops.add(drop) } }
        preRollDrops.forEach { it.roll(player)?.let { drop -> drops.add(drop) } }

        if (rareTableRoller?.invoke(player, drops) == true) return drops

        repeat(rolls) {
            if (rareTableRoller?.invoke(player, drops) == true) return@repeat
            val entry = rollWeighted(mainDrops)
            entry?.roll(player)?.let { drops.add(it) }
        }

        tertiaryDrops.forEach { it.roll(player)?.let { drop -> drops.add(drop) } }

        if (percentDrops.isNotEmpty()) {
            val eligible = percentDrops.filter { it.condition?.invoke(player) ?: true }
            val totalChance = eligible.sumOf { it.percent }

            if (totalChance > 0.0) {
                val roll = ThreadLocalRandom.current().nextDouble(0.0, 100.0)
                var cumulative = 0.0

                for (entry in eligible) {
                    cumulative += entry.percent
                    if (roll < cumulative) {
                        val amount = ThreadLocalRandom.current().nextInt(entry.min, entry.max + 1)
                        drops.add(Drop(entry.itemId, amount))
                        break
                    }
                }
            }
        }

        return drops
    }

    private fun rollWeighted(entries: List<WeightedDropEntry>): WeightedDropEntry? {
        val totalWeight = entries.sumOf { it.weight }
        if (totalWeight == 0) return null
        val rand = ThreadLocalRandom.current().nextInt(totalWeight)
        var acc = 0
        for (entry in entries) {
            acc += entry.weight
            if (rand < acc) return entry
        }
        return null
    }

    companion object {
        val CRIMSON = CharmType.CRIMSON
        val GOLD = CharmType.GOLD
        val GREEN = CharmType.GREEN
        val BLUE = CharmType.BLUE
        val ABYSSAL = CharmType.ABYSSAL
        val NONE = CharmType.NONE
    }

    fun totalDropCount(): Int {
        return alwaysDrops.size +
                preRollDrops.size +
                mainDrops.size +
                tertiaryDrops.size +
                percentDrops.size +
                specialDrops.size
    }
}
