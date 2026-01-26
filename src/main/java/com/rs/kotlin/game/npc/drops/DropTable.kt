package com.rs.kotlin.game.npc.drops

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.DropTablesSetup.gemDropTable
import com.rs.kotlin.game.npc.drops.DropTablesSetup.herbDropTable
import java.io.File
import java.util.concurrent.ThreadLocalRandom

fun dropTable(
    rolls: Int = 1,
    herbTable: HerbTableConfig? = null,
    rareDropTable: Boolean = false,
    block: DropTable.() -> Unit
): DropTable = DropTable(rolls).apply {
    if (herbTable != null) herbTable { player, drops ->
        val roll = ThreadLocalRandom.current().nextInt(herbTable.denominator)
        if (roll < herbTable.numerator) {
            herbDropTable.roll(player)?.let { baseDrop ->
                val rolledAmount = herbTable.amount.random()
                drops.add(baseDrop.copy(amount = rolledAmount))
            }
            true
        } else false
    }

    if (rareDropTable) gemTable { player, drops ->
        gemDropTable.roll(player)?.also(drops::add) != null
    }

    block()
}


class DropTable(private val rolls: Int = 1, var name: String = "DropTable") {
    private val alwaysDrops = mutableListOf<DropEntry>()
    private val preRollDrops = mutableListOf<PreRollDropEntry>()
    private val tertiaryDrops = mutableListOf<TertiaryDropEntry>()
    private val mainDrops = WeightedTable()
    private val specialDrops = WeightedTable()
    private var charmTable: SummoningCharms? = null

    private var gemTableRoller: ((Player, MutableList<Drop>) -> Boolean)? = null
    private var herbTableRoller: ((Player, MutableList<Drop>) -> Boolean)? = null
    private var currentContext: DropType? = null

    override fun toString(): String = "DropTable(name='$name')"

    // ------------------ Scopes ------------------
    fun alwaysDrops(block: MutableList<DropEntry>.() -> Unit) {
        currentContext = DropType.ALWAYS
        alwaysDrops.block()
        currentContext = null
    }

    fun preRollDrops(block: MutableList<PreRollDropEntry>.() -> Unit) {
        currentContext = DropType.PREROLL
        preRollDrops.block()
        currentContext = null
    }


    fun mainDrops(size: Int, block: MutableList<WeightedDropEntry>.() -> Unit) {
        currentContext = DropType.MAIN
        mainDrops.setSize(size)
        mainDrops.mutableEntries().block()
        currentContext = null
    }

    fun specialDrops(size: Int, block: MutableList<WeightedDropEntry>.() -> Unit) {
        currentContext = DropType.SPECIAL
        specialDrops.setSize(size)
        specialDrops.mutableEntries().block()
        currentContext = null
    }

    fun tertiaryDrops(block: MutableList<TertiaryDropEntry>.() -> Unit) {
        currentContext = DropType.TERTIARY
        tertiaryDrops.block()
        currentContext = null
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

    // ------------------ Drop DSL ------------------
    fun DropTable.drop(
        item: String,
        amount: IntRange = 1..1,
        weight: Int = 1,                 // new weight for main/special
        numerator: Int = 1,              // still used for tertiary/pre-roll
        denominator: Int = 4,
        condition: ((Player) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null
    ) {
        val ctx = currentContext ?: error("Cannot call drop() outside a drop type scope")
        when (ctx) {
            DropType.ALWAYS -> addDrop(ctx, Rscm.lookup(item), amount, condition, customLogic)
            DropType.PREROLL -> { preRollDrops.add(PreRollDropEntry(Rscm.lookup(item), amount, numerator, denominator, condition)) }
            DropType.MAIN -> mainDrops.add(WeightedDropEntry(Rscm.lookup(item), amount, weight, condition, customLogic))
            DropType.SPECIAL -> specialDrops.add(WeightedDropEntry(Rscm.lookup(item), amount, weight, condition, customLogic))
            DropType.TERTIARY -> {
                if (numerator <= 0 || denominator <= 0) {
                    System.err.println("Invalid tertiary drop rate for item $item")
                    return
                }
                tertiaryDrops.add(TertiaryDropEntry(Rscm.lookup(item), amount, numerator, denominator, condition))
            }
            DropType.CHARM -> throw UnsupportedOperationException("Use charmDrops { } block for charms")
        }
    }

    fun DropTable.drop(item: String, amount: Int, weight: Int = 1, numerator: Int = 1, denominator: Int = 4,
                       condition: ((Player) -> Boolean)? = null, customLogic: ((Player, Drop?) -> Unit)? = null) =
        drop(item, amount..amount, weight, numerator, denominator, condition, customLogic)

    private fun addDrop(context: DropType, item: Int, amount: IntRange, condition: ((Player) -> Boolean)? = null, customLogic: ((Player, Drop?) -> Unit)? = null) {
        val entry = DropEntry(item, amount, always = context == DropType.ALWAYS, condition = condition)
        when (context) {
            DropType.ALWAYS -> alwaysDrops.add(entry)
            else -> error("Cannot addDrop to context $context")
        }
    }

    fun rollDrops(player: Player): List<Drop> {
        return rollDrops(player, 1.0)
    }

    fun rollDrops(player: Player, multiplier: Double = 1.0): List<Drop> {
        val drops = mutableListOf<Drop>()

        alwaysDrops.forEach { it.roll(player)?.let(drops::add) }

        var preRollHit = false
        for (entry in preRollDrops) {
            val drop = entry.roll(player, multiplier)
            if (drop != null) {
                drops.add(drop)
                preRollHit = true
                break
            }
        }
        gemTableRoller?.let { if (it(player, drops)) return drops }
        herbTableRoller?.let { if (it(player, drops)) return drops }
        if (!preRollHit) {
            repeat(rolls) {
                if (gemTableRoller?.invoke(player, drops) == true) return@repeat
                mainDrops.roll(player)?.let(drops::add)
            }
        }

        if (specialDrops.size() > 0) {
            specialDrops.roll(player)?.let(drops::add)
        }

        tertiaryDrops.forEach {
            it.roll(player, multiplier)?.let(drops::add)
        }
        charmTable?.roll()?.let(drops::add)

        return drops
    }

    fun totalDropCount(): Int =
        alwaysDrops.size + preRollDrops.size + mainDrops.size() + tertiaryDrops.size + specialDrops.size()

    fun exportRatesToJson(multiplier: Double): String {
        val sb = StringBuilder()

        sb.append("{\n")
        sb.append("\"name\":\"$name\",\n")
        sb.append("\"multiplier\":$multiplier,\n")

        // ---------------- ALWAYS ----------------
        sb.append("\"alwaysDrops\":[\n")
        alwaysDrops.forEachIndexed { i, e ->
            sb.append(
                "{" +
                        "\"itemId\":${e.itemId}," +
                        "\"name\":\"${ItemDefinitions.getItemDefinitions(e.itemId).name}\"," +
                        "\"amount\":\"${e.amount}\"" +
                        "}"
            )
            if (i < alwaysDrops.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("],\n")

        // ---------------- PREROLL ----------------
        sb.append("\"preRollDrops\":[\n")
        preRollDrops.forEachIndexed { i, e ->

            val boostedDenom =
                (e.denominator / multiplier)
                    .toInt()
                    .coerceAtLeast(1)

            sb.append(
                "{" +
                        "\"itemId\":${e.itemId}," +
                        "\"name\":\"${ItemDefinitions.getItemDefinitions(e.itemId).name}\"," +
                        "\"amount\":\"${e.amount}\"," +
                        "\"originalRate\":\"${e.numerator}/${e.denominator}\"," +
                        "\"boostedRate\":\"1/$boostedDenom\"" +
                        "}"
            )

            if (i < preRollDrops.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("],\n")

        // ---------------- MAIN ----------------
        sb.append("\"mainDrops\":[\n")

        val totalWeight = mainDrops.mutableEntries().sumOf { it.weight }.toDouble()

        mainDrops.mutableEntries().forEachIndexed { i, e ->

            val originalDenom = totalWeight / e.weight
            sb.append(
                "{" +
                        "\"itemId\":${e.itemId}," +
                        "\"name\":\"${ItemDefinitions.getItemDefinitions(e.itemId).name}\"," +
                        "\"amount\":\"${e.amount}\"," +
                        "\"weight\":${e.weight}," +
                        "\"originalRate\":\"1/${"%.2f".format(originalDenom)}\"" +
                        "}"
            )


            if (i < mainDrops.mutableEntries().size - 1) sb.append(",")
            sb.append("\n")
        }

        sb.append("],\n")

        // ---------------- TERTIARY ----------------
        sb.append("\"tertiaryDrops\":[\n")

        tertiaryDrops.forEachIndexed { i, e ->

            val boosted =
                (e.denominator / multiplier)
                    .toInt()
                    .coerceAtLeast(1)

            sb.append(
                "{" +
                        "\"itemId\":${e.itemId}," +
                        "\"name\":\"${ItemDefinitions.getItemDefinitions(e.itemId).name}\"," +
                        "\"amount\":\"${e.amount}\"," +
                        "\"originalRate\":\"1/${e.denominator}\"," +
                        "\"boostedRate\":\"1/$boosted\"" +
                        "}"
            )

            if (i < tertiaryDrops.size - 1) sb.append(",")
            sb.append("\n")
        }

        sb.append("]\n")
        sb.append("}")

        return sb.toString()
    }

    fun writeRatesToFile(multiplier: Double) {
        val json = exportRatesToJson(multiplier)

        val file = File("data/droptables/${name.replace(" ", "_")}.json")
        file.parentFile.mkdirs()
        file.writeText(json)

        println("[DROP EXPORT] Wrote ${file.path}")
    }

}
