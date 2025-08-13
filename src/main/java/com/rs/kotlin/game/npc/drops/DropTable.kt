package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.DropTablesSetup.gemDropTable
import com.rs.kotlin.game.npc.drops.DropTablesSetup.herbDropTable

fun dropTable(
    rolls: Int = 1,
    herbTable: Boolean = false,
    rareDropTable: Boolean = false,
    block: DropTable.() -> Unit
): DropTable = DropTable(rolls).apply {
    if (herbTable) herbTable { player, drops ->
        herbDropTable.roll(player)?.also(drops::add) != null
    }

    if (rareDropTable) gemTable { player, drops ->
        gemDropTable.roll(player)?.also(drops::add) != null
    }
    block()
}


class DropTable(private val rolls: Int = 1, var name: String = "DropTable") {
    private val alwaysDrops = mutableListOf<DropEntry>()
    private val preRollDrops = mutableListOf<DropEntry>()
    private val tertiaryDrops = mutableListOf<TertiaryDropEntry>()
    private val mainDrops = WeightedTable()
    private val specialDrops = WeightedTable()
    private var charmTable: SummoningCharms? = null

    private var gemTableRoller: ((Player, MutableList<Drop>) -> Boolean)? = null
    private var herbTableRoller: ((Player, MutableList<Drop>) -> Boolean)? = null
    private var currentContext: DropType? = null

    override fun toString(): String {
        return "DropTable(name='$name')"
    }

    fun alwaysDrops(block: MutableList<DropEntry>.() -> Unit) {
        currentContext = DropType.ALWAYS
        alwaysDrops.block()
        currentContext = null
    }

    fun preRollDrops(block: MutableList<DropEntry>.() -> Unit) {
        currentContext = DropType.PREROLL
        preRollDrops.block()
        currentContext = null;
    }

    fun mainDrops(block: MutableList<WeightedDropEntry>.() -> Unit) {
        currentContext = DropType.MAIN
        mainDrops.mutableEntries().block()
        currentContext = null
    }
    fun tertiaryDrops(block: MutableList<TertiaryDropEntry>.() -> Unit) {
        currentContext = DropType.TERTIARY
        tertiaryDrops.block()
        currentContext = null;
    }

    fun specialDrops(block: MutableList<WeightedDropEntry>.() -> Unit) {
        currentContext = DropType.SPECIAL
        specialDrops.mutableEntries().block()
        currentContext = null;
    }

    fun charmDrops(block: SummoningCharms.() -> Unit) {
        currentContext = DropType.CHARM
        val scope = SummoningCharms(this)
        scope.block()
        charmTable = scope
        currentContext = null
    }

    fun gemTable(block: (Player, MutableList<Drop>) -> Boolean) {
        gemTableRoller = block
    }

    fun herbTable(block: (Player, MutableList<Drop>) -> Boolean) {
        herbTableRoller = block
    }

    fun DropTable.drop(
        item: String,
        amount: IntRange = 1..1,
        numerator: Int = 1,
        denominator: Int = 4,
        condition: ((Player) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null
    ) {
        val ctx = currentContext ?: error("Cannot call drop() outside a drop type scope")
        addDrop(ctx, Rscm.lookup(item), amount, numerator, denominator, condition, customLogic)
    }

    fun DropTable.drop(
        item: String,
        amount: Int,
        numerator: Int = 1,
        denominator: Int = 4,
        condition: ((Player) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null
    ) = drop(item, amount..amount, numerator, denominator, condition, customLogic)

    private fun addDrop(
        context: DropType,
        item: Int,
        amount: IntRange,
        numerator: Int = 1,
        denominator: Int = 4,
        condition: ((Player) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null
    ) {
        when (context) {
            DropType.ALWAYS, DropType.PREROLL -> {
                val entry = DropEntry(item, amount, always = context == DropType.ALWAYS, condition = condition)
                    if (context == DropType.ALWAYS) alwaysDrops.add(entry)
                    else preRollDrops.add(entry)
            }

            DropType.MAIN, DropType.SPECIAL -> {
                val entry = WeightedDropEntry(item, amount, numerator, denominator, condition, customLogic)
                if (context == DropType.MAIN) mainDrops.add(entry)
                else specialDrops.add(entry)
            }

            DropType.TERTIARY -> {
                if (numerator <= 0 || denominator <= 0) {
                    System.err.println("Invalid tertiary drop rate for item $item")
                    return
                }
                val entry = TertiaryDropEntry(item, amount, numerator, denominator, condition)
                tertiaryDrops.add(entry)
            }

            DropType.CHARM -> {
                throw UnsupportedOperationException("Please use charmDrops { } block for charms instead of drop()")
            }
        }
    }

    fun rollDrops(player: Player): List<Drop> {
        val drops = mutableListOf<Drop>()

        alwaysDrops.forEach { it.roll(player)?.let { drop -> drops.add(drop) } }

        preRollDrops.forEach { it.roll(player)?.let { drop -> drops.add(drop) } }

        gemTableRoller?.let {
            if (it(player, drops)) {
                return drops
            }
        }
        herbTableRoller?.let {
            if (it(player, drops)) {
                return drops;
            }
        }

       // mainDrops.mutableEntries().forEach { entry ->
         //   println("  itemId=${entry.itemId}, amount=${entry.rollAmount()}, weight=${entry.weight}, numerator=${entry.numerator}, denominator=${entry.denominator}")
        //}
        repeat(rolls) {
            if (gemTableRoller?.invoke(player, drops) == true) return@repeat
            val drop = mainDrops.roll(player)
            drop?.let(drops::add)
        }

        tertiaryDrops.forEach { it.roll(player)?.let { drop -> drops.add(drop) } }

        charmTable?.roll()?.let { drops.add(it) }

        return drops
    }

    fun totalDropCount(): Int {
        return alwaysDrops.size +
                preRollDrops.size +
                mainDrops.size() +
                tertiaryDrops.size +
                specialDrops.size()
    }
}
