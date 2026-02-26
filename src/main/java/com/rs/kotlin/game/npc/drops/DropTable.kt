package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.DropTablesSetup.gemDropTable
import com.rs.kotlin.game.npc.drops.DropTablesSetup.godwarsGemDropTable
import com.rs.kotlin.game.npc.drops.DropTablesSetup.godwarsRareDropTable
import com.rs.kotlin.game.npc.drops.DropTablesSetup.herbDropTable
import com.rs.kotlin.game.npc.drops.DropTablesSetup.rareDropTable
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.rare.GemTableEntry
import com.rs.kotlin.game.npc.drops.rare.RareDropTableEntry
import com.rs.kotlin.game.npc.drops.seed.SeedTableType
import com.rs.kotlin.game.player.interfaces.DropDisplay
import com.rs.kotlin.game.world.util.Msg
import java.io.File
import java.util.concurrent.ThreadLocalRandom

fun either(vararg keys: String): String {
    require(keys.isNotEmpty())
    return keys[ThreadLocalRandom.current().nextInt(keys.size)]
}

fun sharedWeightedTable(
    meta: (DropMetadata.() -> Unit)? = null,
    block: WeightedTableBuilder.() -> Unit,
): WeightedTable {
    val table = WeightedTable()
    WeightedTableBuilder(table, meta).apply(block)
    return table
}

fun dropTable(
    rolls: Int = 1,
    name: String = "DropTable",
    category: TableCategory = TableCategory.REGULAR,
    sourceAction: String = "killing",
    herbTables: HerbTableConfig,
    seedTable: SeedTableConfig? = null,
    rareTable: RareTableConfig? = null,
    godwarsRareTable: RareTableConfig? = null,
    gemTable: GemTableConfig? = null,
    godwarsGemTable: GemTableConfig? = null,
    block: DropTable.() -> Unit,
): DropTable =
    dropTable(
        rolls,
        name,
        category,
        sourceAction,
        herbTables = listOf(herbTables),
        seedTable,
        rareTable,
        godwarsRareTable,
        gemTable,
        godwarsGemTable,
        block,
    )

fun dropTable(
    rolls: Int = 1,
    name: String = "DropTable",
    category: TableCategory = TableCategory.REGULAR,
    sourceAction: String = "killing",
    herbTables: List<HerbTableConfig> = emptyList(),
    seedTable: SeedTableConfig? = null,
    rareTable: RareTableConfig? = null,
    godwarsRareTable: RareTableConfig? = null,
    gemTable: GemTableConfig? = null,
    godwarsGemTable: GemTableConfig? = null,
    block: DropTable.() -> Unit,
): DropTable =
    DropTable(rolls, name, category, sourceAction).apply {
        godwarsRareTable?.let { cfg ->
            godwarsRareTableConfig = cfg
            rareTableRoller = { context, drops ->

                val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)
                if (roll >= cfg.numerator) {
                    false
                } else {
                    val rareContext = context.copy(dropSource = DropSource.RARE)

                    godwarsRareDropTable
                        .roll(rareContext)
                        ?.let {
                            addAndProcessDrop(context.player, drops, it)
                            true
                        } ?: false
                }
            }
        }
        // ---------- RARE TABLE ----------
        rareTable?.let { cfg ->
            rareTableConfig = cfg
            rareTableRoller = { context, drops ->

                val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)
                if (roll >= cfg.numerator) {
                    false
                } else {
                    val rareContext = context.copy(dropSource = DropSource.RARE)

                    rareDropTable
                        .roll(rareContext)
                        ?.let {
                            addAndProcessDrop(context.player, drops, it)
                            true
                        } ?: false
                }
            }
        }
        gemTable?.let { cfg ->
            gemTableConfig = cfg
            gemTableRoller = { context, drops ->
                val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)
                if (roll >= cfg.numerator) {
                    false
                } else {
                    val gemContext = context.copy(dropSource = DropSource.RARE)
                    gemDropTable.roll(gemContext)?.let {
                        addAndProcessDrop(context.player, drops, it)
                        true
                    } ?: false
                }
            }
        }
        godwarsGemTable?.let { cfg ->
            godwarsGemTableConfig = cfg
            gemTableRoller = { context, drops ->
                val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)
                if (roll >= cfg.numerator) {
                    false
                } else {
                    val gemContext = context.copy(dropSource = DropSource.RARE)
                    godwarsGemDropTable.roll(gemContext)?.let {
                        addAndProcessDrop(context.player, drops, it)
                        true
                    } ?: false
                }
            }
        }
        herbTableConfigs = herbTables

        if (herbTables.isNotEmpty()) {
            herbTables.forEach { cfg ->
                herb { context, drops ->

                    val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)

                    if (roll < cfg.numerator) {
                        val herbContext = context.copy(dropSource = DropSource.HERB)
                        herbDropTable.roll(herbContext)?.let { baseDrop ->
                            val rolledAmount = cfg.amount.random()
                            val newDrop = baseDrop.copy(amount = rolledAmount)
                            addAndProcessDrop(context.player, drops, newDrop)
                        }
                    }

                    false
                }
            }
        }
        seedTableConfig = seedTable
        if (seedTable != null) {
            seed { context, combatLevel, drops ->

                val roll = ThreadLocalRandom.current().nextInt(seedTable.denominator)
                if (roll >= seedTable.numerator) {
                    false
                } else {
                    val seedContext = context.copy(dropSource = DropSource.SEED)

                    val result =
                        DropTablesSetup.seedDropTable.roll(
                            seedTable.table,
                            seedContext,
                            combatLevel,
                        )

                    result?.let {
                        val finalAmount = seedTable.amount.random()
                        drops.add(it.copy(amount = finalAmount))
                        true
                    } ?: false
                }
            }
        }

        block()
    }

class DropTable(
    private val rolls: Int = 1,
    var name: String = "DropTable",
    val category: TableCategory = TableCategory.REGULAR,
    var sourceAction: String = "killing",
    var collectionGroup: String? = null,
) {
    private val alwaysDrops = mutableListOf<DropEntry>()
    private val preRollDenom = mutableListOf<PreRollDropEntry>()
    private val preRollTables = mutableListOf<PreRollTableEntry>()
    private val tertiaryDrops = mutableListOf<TertiaryDropEntry>()
    val mainDrops = WeightedTable()
    val preRollWeight = WeightedTable()
    private val minorDrops = WeightedTable()
    private val specialDrops = WeightedTable()
    private var charmTable: SummoningCharms? = null

    private val herbTableRollers = mutableListOf<(DropContext, MutableList<Drop>) -> Boolean>()
    var rareTableRoller: ((DropContext, MutableList<Drop>) -> Boolean)? = null
    var gemTableRoller: ((DropContext, MutableList<Drop>) -> Boolean)? = null
    private var seedTableRoller: ((DropContext, Int, MutableList<Drop>) -> Boolean)? = null

    var herbTableConfigs: List<HerbTableConfig>? = null
    var rareTableConfig: RareTableConfig? = null
    var godwarsRareTableConfig: RareTableConfig? = null
    var gemTableConfig: GemTableConfig? = null
    var godwarsGemTableConfig: GemTableConfig? = null
    var seedTableConfig: SeedTableConfig? = null

    private var currentContext: DropType? = null
    private var currentWeightedTable: WeightedTable? = null

    override fun toString(): String = "DropTable(name='$name')"

    fun getWeightedTableDropsForDisplay(
        table: WeightedTable,
        dropType: DropType,
        parentChance: Double = 1.0,
        expandNested: Boolean = true,
    ): List<DropDisplay> {
        val list = mutableListOf<DropDisplay>()

        fun addWeightedTableDisplays(
            t: WeightedTable,
            dt: DropType,
            parent: Double,
        ) {
            val entries = t.mutableEntries()
            val total = t.tableSize.toDouble()
            if (total <= 0.0) return

            val nothingWeight =
                entries
                    .filterIsInstance<ItemWeightedEntry>()
                    .firstOrNull { it.itemId == GemTableEntry.NOTHING_MARKER }
                    ?.weight ?: 0

            for (entry in entries) {
                if (entry.weight <= 0) continue

                when (entry) {
                    is PackageWeightedEntry -> {
                        val chance = parent * (entry.weight / total)
                        val denom = (1.0 / chance).coerceAtLeast(1.0).toInt()

                        if (entry.displayDrops.isEmpty()) {
                            list.add(
                                DropDisplay(
                                    itemId = -1,
                                    amount = 1..1,
                                    rarityText = "1/$denom",
                                    type = DropType.NOTHING,
                                    baseDenominator = denom,
                                    weight = entry.weight,
                                    totalWeight = total.toInt(),
                                    nothingWeight = nothingWeight,
                                ),
                            )
                        } else {
                            entry.displayDrops.forEach { dd ->
                                list.add(
                                    DropDisplay(
                                        itemId = dd.itemId,
                                        amount = dd.amount,
                                        rarityText = "1/$denom",
                                        type = dt,
                                        baseDenominator = denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                        nothingWeight = nothingWeight,
                                    ),
                                )
                            }
                        }
                    }

                    is ItemWeightedEntry -> {
                        val chance = parent * (entry.weight / total)
                        val denom = (1.0 / chance).coerceAtLeast(1.0).toInt()

                        val adjustedTotal =
                            if (nothingWeight > 0 && entry.itemId != GemTableEntry.NOTHING_MARKER) {
                                total - nothingWeight
                            } else {
                                null
                            }

                        when (entry.itemId) {
                            GemTableEntry.NOTHING_MARKER -> {
                                list.add(
                                    DropDisplay(
                                        itemId = -1,
                                        amount = entry.amount,
                                        rarityText = "1/$denom",
                                        type = DropType.NOTHING,
                                        baseDenominator = denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                        nothingWeight = nothingWeight,
                                    ),
                                )
                            }

                            RareDropTableEntry.GEM_TABLE_MARKER -> {
                                list.add(
                                    DropDisplay(
                                        itemId = Rscm.lookup("item.uncut_dragonstone"),
                                        amount = 1..1,
                                        rarityText = "1/$denom",
                                        type = DropType.GEM_TABLE,
                                        baseDenominator = denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                        nothingWeight = if (adjustedTotal != null) nothingWeight else null,
                                    ),
                                )
                            }

                            RareDropTableEntry.MEGA_RARE_MARKER -> {
                                list.add(
                                    DropDisplay(
                                        itemId = Rscm.lookup("item.shield_left_half"),
                                        amount = 1..1,
                                        rarityText = "1/$denom",
                                        type = DropType.MEGA_TABLE,
                                        baseDenominator = denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                        nothingWeight = if (adjustedTotal != null) nothingWeight else null,
                                    ),
                                )
                            }

                            else -> {
                                list.add(
                                    DropDisplay(
                                        itemId = entry.itemId,
                                        amount = entry.amount,
                                        rarityText = "1/$denom",
                                        type = dt,
                                        baseDenominator = denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                        nothingWeight = if (adjustedTotal != null) nothingWeight else null,
                                    ),
                                )
                            }
                        }
                    }

                    is NestedTableEntry -> {
                        val chanceToEnter = parent * (entry.weight / total)

                        if (expandNested) {
                            // ✅ "Show all items": recurse into the nested table
                            addWeightedTableDisplays(entry.table, dt, chanceToEnter)
                        } else {
                            // ✅ "Show categories": show the nested table row (clickable) instead of expanding
                            val denom =
                                if (chanceToEnter > 0) (1.0 / chanceToEnter).toInt() else 0

                            list.add(
                                DropDisplay(
                                    itemId = entry.displayItemId ?: -1,
                                    amount = 1..1,
                                    rarityText = if (denom > 0) "1/$denom" else "",
                                    type = DropType.SUB_TABLE,
                                    baseDenominator = denom,
                                    tableName = entry.displayName,
                                    tableReference = entry.table,
                                    parentChance = chanceToEnter,
                                    expandNested = false,
                                ),
                            )
                        }
                    }
                }
            }
        }

        addWeightedTableDisplays(table, dropType, parentChance)
        return list
    }

    fun getAllDropsForDisplay(multiplier: Double = 1.0): List<DropDisplay> {
        var list = mutableListOf<DropDisplay>()

        fun addWeightedTableDisplays(
            table: WeightedTable,
            dropType: DropType,
            parentChance: Double = 1.0,
            depth: Int = 0,
            expandNested: Boolean = true,
        ) {
            val entries = table.mutableEntries()
            val configuredSize = table.tableSize
            val computedSum = entries.sumOf { it.weight }
            val total = if (configuredSize > 0) configuredSize.toDouble() else computedSum.toDouble()
            val nothingWeight =
                entries
                    .filterIsInstance<ItemWeightedEntry>()
                    .firstOrNull {
                        it.itemId == GemTableEntry.NOTHING_MARKER
                    }?.weight ?: 0
            if (total <= 0.0) return

            for (entry in entries) {
                when (entry) {
                    is PackageWeightedEntry -> {
                        val chance = parentChance * (entry.weight / total)
                        val denom = (1.0 / chance).coerceAtLeast(1.0).toInt()

                        if (entry.displayDrops.isEmpty()) {
                            list.add(
                                DropDisplay(
                                    -1,
                                    1..1,
                                    "1/$denom",
                                    DropType.NOTHING,
                                    denom,
                                    weight = entry.weight,
                                    totalWeight = total.toInt(),
                                ),
                            )
                        } else {
                            entry.displayDrops.forEach { dd ->
                                list.add(
                                    DropDisplay(
                                        dd.itemId,
                                        dd.amount,
                                        "1/$denom",
                                        dropType,
                                        denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                    ),
                                )
                            }
                        }
                    }

                    is ItemWeightedEntry -> {
                        val chance = parentChance * (entry.weight / total)
                        val denom = (1.0 / chance).coerceAtLeast(1.0).toInt()

                        val adjustedTotal =
                            if (nothingWeight > 0 && entry.itemId != GemTableEntry.NOTHING_MARKER) {
                                total - nothingWeight
                            } else {
                                null
                            }

                        when (entry.itemId) {
                            GemTableEntry.NOTHING_MARKER -> {
                                list.add(
                                    DropDisplay(
                                        -1,
                                        entry.amount,
                                        "1/$denom",
                                        DropType.NOTHING,
                                        denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                        nothingWeight = nothingWeight,
                                    ),
                                )
                            }

                            RareDropTableEntry.GEM_TABLE_MARKER -> {
                                list.add(
                                    DropDisplay(
                                        itemId = Rscm.lookup("item.uncut_dragonstone"),
                                        amount = 1..1,
                                        rarityText = "1/$denom",
                                        type = DropType.GEM_TABLE,
                                        baseDenominator = denom,
                                    ),
                                )
                            }

                            RareDropTableEntry.MEGA_RARE_MARKER -> {
                                list.add(
                                    DropDisplay(
                                        itemId = Rscm.lookup("item.shield_left_half"),
                                        amount = 1..1,
                                        rarityText = "1/$denom",
                                        type = DropType.MEGA_TABLE,
                                        baseDenominator = denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                        nothingWeight = if (adjustedTotal != null) nothingWeight else null,
                                    ),
                                )
                            }

                            else -> {
                                list.add(
                                    DropDisplay(
                                        entry.itemId,
                                        entry.amount,
                                        "1/$denom",
                                        dropType,
                                        denom,
                                        weight = entry.weight,
                                        totalWeight = total.toInt(),
                                        nothingWeight = if (adjustedTotal != null) nothingWeight else null,
                                    ),
                                )
                            }
                        }
                    }

                    is NestedTableEntry -> {
                        val chanceToEnter = parentChance * (entry.weight / total)

                        val denom =
                            if (chanceToEnter > 0) {
                                (1.0 / chanceToEnter).toInt()
                            } else {
                                0
                            }

                        // If this entry should be shown as a category
                        if (entry.displayAsTable) {
                            // 1️⃣ Show THIS table
                            list.add(
                                DropDisplay(
                                    itemId = entry.displayItemId ?: -1,
                                    amount = 1..1,
                                    rarityText = if (denom > 0) "1/$denom" else "",
                                    type = DropType.SUB_TABLE,
                                    baseDenominator = denom,
                                    tableName = entry.displayName,
                                    tableReference = entry.table,
                                    parentChance = chanceToEnter,
                                ),
                            )

                            // 2️⃣ Recursively search for deeper displayAsTable tables
                            fun collectSubTables(
                                table: WeightedTable,
                                parentChance: Double,
                            ) {
                                val entries = table.mutableEntries()
                                val configuredSize = table.tableSize
                                val computedSum = entries.sumOf { it.weight }
                                val totalInner =
                                    if (configuredSize > 0) {
                                        configuredSize.toDouble()
                                    } else {
                                        computedSum.toDouble()
                                    }

                                for (child in entries) {
                                    if (child is NestedTableEntry) {
                                        val childChance =
                                            parentChance * (child.weight / totalInner)

                                        val childDenom =
                                            if (childChance > 0) {
                                                (1.0 / childChance).toInt()
                                            } else {
                                                0
                                            }

                                        if (child.displayAsTable) {
                                            list.add(
                                                DropDisplay(
                                                    itemId = child.displayItemId ?: -1,
                                                    amount = 1..1,
                                                    rarityText = if (childDenom > 0) "1/$childDenom" else "",
                                                    type = DropType.SUB_TABLE,
                                                    baseDenominator = childDenom,
                                                    tableName = child.displayName,
                                                    tableReference = child.table,
                                                    parentChance = childChance,
                                                    expandNested = false,
                                                ),
                                            )
                                        }

                                        // Keep digging
                                        collectSubTables(child.table, childChance)
                                    }
                                }
                            }

                            collectSubTables(entry.table, chanceToEnter)
                        } else {
                            // Not a category → flatten normally
                            addWeightedTableDisplays(
                                entry.table,
                                dropType,
                                chanceToEnter,
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
        // ALWAYS
        alwaysDrops.forEach {
            list.add(
                DropDisplay(
                    it.itemId,
                    it.amount,
                    "Always",
                    DropType.ALWAYS,
                    1,
                ),
            )
        }

        // PREROLL
        preRollDenom.forEach { entry ->
            val base = entry.denominator
            val boosted =
                (entry.denominator / multiplier)
                    .toInt()
                    .coerceAtLeast(1)

            if (entry.displayItems != null) {
                entry.displayItems.forEach { itemId ->
                    list.add(
                        DropDisplay(
                            itemId,
                            entry.amount,
                            "1/$boosted",
                            DropType.PREROLL,
                            base,
                        ),
                    )
                }
                return@forEach
            }

            list.add(
                DropDisplay(
                    entry.itemId ?: -1,
                    entry.amount,
                    "1/$boosted",
                    DropType.PREROLL,
                    base,
                ),
            )
        }
        // PREROLL TABLES
        preRollTables.forEach { entry ->

            val baseChance = entry.numerator.toDouble() / entry.denominator.toDouble()
            val chanceToEnter = baseChance

            val denom =
                if (chanceToEnter > 0) {
                    (1.0 / chanceToEnter).toInt()
                } else {
                    0
                }

            if (entry.displayAsTable) {
                // 1️⃣ Show THIS table
                list.add(
                    DropDisplay(
                        itemId = entry.displayItemId ?: -1,
                        amount = 1..1,
                        rarityText = if (denom > 0) "1/$denom" else "",
                        type = DropType.SUB_TABLE,
                        baseDenominator = denom,
                        tableName = entry.displayName,
                        tableReference = entry.table,
                        parentChance = chanceToEnter,
                        expandNested = false,
                    ),
                )

                fun collectSubTables(
                    table: WeightedTable,
                    parentChance: Double,
                ) {
                    val entries = table.mutableEntries()
                    val configuredSize = table.tableSize
                    val computedSum = entries.sumOf { it.weight }
                    val totalInner =
                        if (configuredSize > 0) {
                            configuredSize.toDouble()
                        } else {
                            computedSum.toDouble()
                        }

                    for (child in entries) {
                        if (child is NestedTableEntry) {
                            val childChance =
                                parentChance * (child.weight / totalInner)

                            val childDenom =
                                if (childChance > 0) {
                                    (1.0 / childChance).toInt()
                                } else {
                                    0
                                }

                            if (child.displayAsTable) {
                                list.add(
                                    DropDisplay(
                                        itemId = child.displayItemId ?: -1,
                                        amount = 1..1,
                                        rarityText = if (childDenom > 0) "1/$childDenom" else "",
                                        type = DropType.SUB_TABLE,
                                        baseDenominator = childDenom,
                                        tableName = child.displayName,
                                        tableReference = child.table,
                                        parentChance = childChance,
                                        expandNested = false,
                                    ),
                                )
                            }

                            collectSubTables(child.table, childChance)
                        }
                    }
                }

                collectSubTables(entry.table, chanceToEnter)
            } else {
                addWeightedTableDisplays(
                    entry.table,
                    DropType.PREROLL,
                    chanceToEnter,
                )
            }
        }
        addWeightedTableDisplays(preRollWeight, DropType.PREROLL)
        val preRollTotal = preRollWeight.tableSize.toDouble()
        val preRollSum = preRollWeight.mutableEntries().sumOf { it.weight }.toDouble()

        val preRollHitChance =
            if (preRollTotal > 0) preRollSum / preRollTotal else 0.0

        val mainGateMultiplier = 1.0 - preRollHitChance
        addWeightedTableDisplays(mainDrops, DropType.MAIN, parentChance = mainGateMultiplier)
        addWeightedTableDisplays(minorDrops, DropType.MINOR)
        addWeightedTableDisplays(specialDrops, DropType.SPECIAL)

        herbTableConfigs?.forEach { cfg ->

            val tableEntries = herbDropTable.getEntries()
            val totalWeight = tableEntries.sumOf { it.weight }.toDouble()

            tableEntries.forEach { herb ->

                val tableChance =
                    (cfg.numerator.toDouble() / cfg.denominator.toDouble())

                val herbChance =
                    herb.weight.toDouble() / totalWeight

                val combinedChance =
                    tableChance * herbChance

                val denom =
                    (1.0 / combinedChance)
                        .coerceAtLeast(1.0)
                        .toInt()

                list.add(
                    DropDisplay(
                        herb.itemId,
                        cfg.amount,
                        "1/$denom",
                        DropType.PREROLL,
                        denom,
                    ),
                )
            }
        }

        // SEED
        seedTableConfig?.let { cfg ->

            val tableChance =
                cfg.numerator.toDouble() / cfg.denominator.toDouble()

            when (cfg.table) {
                SeedTableType.GENERAL -> {
                    list.add(
                        DropDisplay(
                            -1,
                            cfg.amount,
                            "General seed table",
                            DropType.MAIN,
                            cfg.denominator,
                        ),
                    )
                }

                SeedTableType.RARE,
                SeedTableType.UNCOMMON,
                SeedTableType.TREE_HERB,
                -> {
                    val entries =
                        when (cfg.table) {
                            SeedTableType.RARE -> {
                                DropTablesSetup.seedDropTable.getRareEntries()
                            }

                            SeedTableType.UNCOMMON -> {
                                DropTablesSetup.seedDropTable.getUncommonEntries()
                            }

                            SeedTableType.TREE_HERB -> {
                                DropTablesSetup.seedDropTable.getTreeHerbEntries()
                            }

                            else -> {
                                emptyList()
                            }
                        }

                    val totalWeight =
                        entries.sumOf { it.weight }.toDouble()

                    entries.forEach { entry ->

                        val seedChance =
                            entry.weight.toDouble() / totalWeight

                        val combinedChance =
                            tableChance * seedChance

                        val denom =
                            (1.0 / combinedChance)
                                .coerceAtLeast(1.0)
                                .toInt()

                        list.add(
                            DropDisplay(
                                entry.itemId,
                                cfg.amount,
                                "1/$denom",
                                DropType.MAIN,
                                denom,
                            ),
                        )
                    }
                }
            }
        }

        // TERTIARY
        tertiaryDrops.forEach {
            val base = it.denominator
            val boosted =
                (it.denominator / multiplier)
                    .toInt()
                    .coerceAtLeast(1)

            list.add(
                DropDisplay(
                    it.itemId,
                    it.amount,
                    "1/$boosted",
                    DropType.TERTIARY,
                    base,
                ),
            )
        }

        gemTableConfig?.let { cfg ->
            val chance = cfg.numerator.toDouble() / cfg.denominator
            val denom = (1.0 / chance).toInt()

            list.add(
                DropDisplay(
                    itemId = Rscm.lookup("item.uncut_dragonstone"),
                    amount = 1..1,
                    rarityText = "1/$denom",
                    type = DropType.GEM_TABLE,
                    baseDenominator = denom,
                ),
            )
        }

        rareTableConfig?.let { cfg ->
            val chance = cfg.numerator.toDouble() / cfg.denominator
            val denom = (1.0 / chance).toInt()

            list.add(
                DropDisplay(
                    itemId = Rscm.lookup("item.dragon_helm"),
                    amount = 1..1,
                    rarityText = "1/$denom",
                    type = DropType.RARE_TABLE,
                    baseDenominator = denom,
                ),
            )
        }

        val charms = charmTable
        if (charms != null) {
            for (charm in charms.getEntries()) {
                if (charm.percent <= 0.0) continue

                val percentText =
                    if (charm.percent % 1.0 == 0.0) {
                        "${charm.percent.toInt()}%"
                    } else {
                        "%.2f%%".format(charm.percent)
                    }
                list.add(
                    DropDisplay(
                        charm.itemId,
                        charm.amount..charm.amount,
                        percentText,
                        DropType.CHARM,
                        0,
                        percentage = charm.percent,
                    ),
                )
            }
        }
        return list
    }

    fun DropTable.registerCollectionFrom(table: WeightedTable) {
        fun scan(t: WeightedTable) {
            t.mutableEntries().forEach { entry ->
                when (entry) {
                    is ItemWeightedEntry -> {
                        val itemName = Rscm.reverseItemLookup(entry.itemId) ?: return@forEach
                        if (itemName.startsWith("item.")) {
                            always {
                                drop(itemName) { collectionLog = true }
                            }
                        }
                    }

                    is NestedTableEntry -> {
                        scan(entry.table)
                    }

                    is PackageWeightedEntry -> {
                        entry.displayDrops.forEach { dd ->
                            val itemName = Rscm.reverseItemLookup(dd.itemId) ?: return@forEach
                            if (itemName.startsWith("item.")) {
                                always {
                                    drop(itemName) { collectionLog = true }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Begin scanning the table
        scan(table)
    }

    fun getAllItemIdsForCollectionLog(): Set<Int> {
        val set = mutableSetOf<Int>()

        fun scanTable(table: WeightedTable) {
            table.mutableEntries().forEach { entry ->
                when (entry) {
                    is PackageWeightedEntry -> {
                        entry.displayDrops.forEach { dd ->
                            if (dd.metadata.collectionLog) set.add(dd.itemId)
                        }
                    }

                    is ItemWeightedEntry -> {
                        if (entry.metadata.collectionLog) {
                            set.add(entry.itemId)
                        }
                    }

                    is NestedTableEntry -> {
                        scanTable(entry.table)
                    }
                }
            }
        }
        alwaysDrops.forEach {
            if (it.metadata.collectionLog) set.add(it.itemId)
        }

        preRollDenom.forEach { entry ->
            if (!entry.metadata.collectionLog) return@forEach

            val display = entry.displayItems
            if (!display.isNullOrEmpty()) {
                set.addAll(display)
                return@forEach
            }

            set.add(entry.itemId)
        }

        preRollTables.forEach { entry ->
            scanTable(entry.table)
        }
        scanTable(mainDrops)
        scanTable(minorDrops)
        scanTable(specialDrops)
        tertiaryDrops.forEach {
            if (it.metadata.collectionLog) {
                set.add(it.itemId)
            }
        }
        return set
    }

    fun always(block: MutableList<DropEntry>.() -> Unit) {
        currentContext = DropType.ALWAYS
        alwaysDrops.block()
        currentContext = null
    }

    fun prerollDenom(
        meta: (DropMetadata.() -> Unit)? = null,
        block: MutableList<PreRollDropEntry>.() -> Unit,
    ) {
        currentContext = DropType.PREROLL
        if (meta != null) {
            val metadata = DropMetadata().apply { meta() }
            preRollDenom.forEach { entry ->
                entry.metadata = metadata // Apply the metadata to each PreRollTableEntry
            }
        }
        preRollDenom.block()
        currentContext = null
    }

    fun preroll(
        meta: (DropMetadata.() -> Unit)? = null,
        block: MutableList<PreRollTableEntry>.() -> Unit,
    ) {
        currentContext = DropType.PREROLL
        if (meta != null) {
            val metadata = DropMetadata().apply { meta() }
            preRollTables.forEach { entry ->
                entry.metadata = metadata // Apply the metadata to each PreRollTableEntry
            }
        }
        preRollTables.block()
        currentContext = null
    }

    fun main(
        size: Int?,
        block: MutableList<WeightedEntry>.() -> Unit,
    ) {
        currentContext = DropType.MAIN
        if (size != null) {
            mainDrops.setSize(size)
        }

        val prev = currentWeightedTable
        currentWeightedTable = null // main uses default
        mainDrops.mutableEntries().block()
        currentWeightedTable = prev

        currentContext = null
    }

    fun minor(
        size: Int,
        block: MutableList<WeightedEntry>.() -> Unit,
    ) {
        currentContext = DropType.MINOR
        minorDrops.setSize(size)

        val prev = currentWeightedTable
        currentWeightedTable = null
        minorDrops.mutableEntries().block()
        currentWeightedTable = prev

        currentContext = null
    }

    fun special(
        size: Int,
        block: MutableList<WeightedEntry>.() -> Unit,
    ) {
        currentContext = DropType.SPECIAL
        specialDrops.setSize(size)

        val prev = currentWeightedTable
        currentWeightedTable = null
        specialDrops.mutableEntries().block()
        currentWeightedTable = prev

        currentContext = null
    }

    fun tertiary(block: MutableList<TertiaryDropEntry>.() -> Unit) {
        currentContext = DropType.TERTIARY
        tertiaryDrops.block()
        currentContext = null
    }

    fun charm(block: SummoningCharms.() -> Unit) {
        currentContext = DropType.CHARM
        val scope = SummoningCharms(this)
        scope.block()
        charmTable = scope
        currentContext = null
    }

    fun gem(block: (DropContext, MutableList<Drop>) -> Boolean) {
        gemTableRoller = block
    }

    fun herb(block: (DropContext, MutableList<Drop>) -> Boolean) {
        herbTableRollers += block
    }

    fun seed(block: (DropContext, Int, MutableList<Drop>) -> Boolean) {
        seedTableRoller = block
    }

    // ------------------ Drop DSL ------------------
    fun DropTable.drop(
        numerator: Int = 1,
        denominator: Int = 4,
        amount: IntRange = 1..1,
        condition: ((DropContext) -> Boolean)? = null,
        dynamicItem: (DropContext) -> Int?,
        displayItems: List<String>? = null,
        meta: (DropMetadata.() -> Unit)? = null,
    ) {
        val ctx = currentContext ?: error("Cannot call drop() outside a drop type scope")

        if (ctx != DropType.PREROLL) {
            error("dynamicItem drops are only supported in preRollDrops")
        }
        val metadata = DropMetadata().apply { meta?.invoke(this) }
        preRollDenom.add(
            PreRollDropEntry(
                itemId = null,
                amount = amount,
                numerator = numerator,
                denominator = denominator,
                condition = condition,
                dynamicItem = dynamicItem,
                displayItems = displayItems?.map { Rscm.lookup(it) },
                metadata = metadata,
            ),
        )
    }

    @JvmName("tablePreRollByWeight")
    fun MutableList<PreRollTableEntry>.table(
        table: WeightedTable,
        weight: Int,
        asSubTable: Boolean = false,
        name: String? = null,
        icon: String? = null,
    ) {
        add(
            PreRollTableEntry(
                table = table,
                numerator = 1,
                denominator = weight,
                displayAsTable = asSubTable,
                displayName = name,
                displayItemId = icon?.let { Rscm.lookup(it) },
            ),
        )
    }

    @JvmName("tablePreRollByFraction")
    fun MutableList<PreRollTableEntry>.table(
        table: WeightedTable,
        numerator: Int,
        denominator: Int,
        asSubTable: Boolean = false,
        name: String? = null,
        icon: String? = null,
    ) {
        add(
            PreRollTableEntry(
                table = table,
                numerator = numerator,
                denominator = denominator,
                displayAsTable = asSubTable,
                displayName = name,
                displayItemId = icon?.let { Rscm.lookup(it) },
            ),
        )
    }

    fun WeightedTableBuilder.packageDrop(
        weight: Int,
        block: PackageBuilder.() -> Unit,
    ) {
        val builder = PackageBuilder().apply(block)
        table.add(
            PackageWeightedEntry(
                weight = weight,
                displayDrops = builder.displayDrops,
                build = { ctx -> builder.build(ctx) },
            ),
        )
    }

    fun weightedTable(
        total: Int? = null,
        meta: (DropMetadata.() -> Unit)? = null,
        block: WeightedTableBuilder.() -> Unit,
    ): WeightedTable {
        val table = WeightedTable()
        WeightedTableBuilder(table, meta).apply(block)
        if (total != null) {
            table.setSize(total)
        }
        return table
    }

    fun MutableList<WeightedEntry>.table(
        table: WeightedTable,
        weight: Int,
        asSubTable: Boolean = false,
        name: String? = null,
        icon: String? = null,
    ) {
        add(
            NestedTableEntry(
                table,
                weight,
                displayAsTable = asSubTable,
                displayName = name,
                displayItemId = icon?.let { Rscm.lookup(it) },
            ),
        )
    }

    fun MutableList<WeightedEntry>.nestedTable(
        weight: Int,
        size: Int,
        block: MutableList<WeightedEntry>.() -> Unit,
    ) {
        val nested = WeightedTable().apply { setSize(size) }

        val prev = currentWeightedTable
        currentWeightedTable = nested
        nested.mutableEntries().block()
        currentWeightedTable = prev

        this.add(NestedTableEntry(nested, weight))
    }

    fun alwaysTable(block: WeightedTableBuilder.() -> Unit): WeightedTable {
        val table = WeightedTable()
        WeightedTableBuilder(table).apply(block)
        return table
    }

    fun DropTable.drop(
        item: String,
        amount: IntRange = 1..1,
        weight: Int = 1,
        numerator: Int = 1,
        denominator: Int = 4,
        condition: ((DropContext) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null,
        meta: (DropMetadata.() -> Unit)? = null,
    ) {
        val metadata =
            DropMetadata().apply {
                meta?.invoke(this)
            }
        val ctx = currentContext ?: error("Cannot call drop() outside a drop type scope")
        when (ctx) {
            DropType.ALWAYS -> {
                addDrop(ctx, Rscm.lookup(item), amount, condition, customLogic, metadata)
            }

            DropType.PREROLL -> {
                preRollDenom.add(
                    PreRollDropEntry(
                        itemId = Rscm.lookup(item),
                        amount = amount,
                        numerator = numerator,
                        denominator = denominator,
                        condition = condition,
                        metadata = metadata,
                    ),
                )
            }

            DropType.MAIN -> {
                val table = currentWeightedTable ?: mainDrops
                table.add(ItemWeightedEntry(Rscm.lookup(item), amount, weight, condition, customLogic, metadata))
            }

            DropType.MINOR -> {
                val table = currentWeightedTable ?: minorDrops
                table.add(ItemWeightedEntry(Rscm.lookup(item), amount, weight, condition, customLogic, metadata))
            }

            DropType.SPECIAL -> {
                val table = currentWeightedTable ?: specialDrops
                table.add(ItemWeightedEntry(Rscm.lookup(item), amount, weight, condition, customLogic, metadata))
            }

            DropType.TERTIARY -> {
                if (numerator <= 0 || denominator <= 0) {
                    System.err.println("Invalid tertiary drop rate for item $item")
                    return
                }
                tertiaryDrops.add(TertiaryDropEntry(Rscm.lookup(item), amount, numerator, denominator, condition, metadata))
            }

            DropType.CHARM -> {
                throw UnsupportedOperationException("Use charmDrops { } block for charms")
            }

            else -> {}
        }
    }

    fun DropTable.drop(
        item: String,
        amount: Int,
        weight: Int = 1,
        numerator: Int = 1,
        denominator: Int = 4,
        condition: ((DropContext) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null,
        meta: (DropMetadata.() -> Unit)? = null,
    ) = drop(item, amount..amount, weight, numerator, denominator, condition, customLogic, meta)

    private fun addDrop(
        context: DropType,
        item: Int,
        amount: IntRange,
        condition: ((DropContext) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null,
        metadata: DropMetadata = DropMetadata(),
    ) {
        val entry =
            DropEntry(
                itemId = item,
                amount = amount,
                always = context == DropType.ALWAYS,
                condition = condition,
                metadata = metadata,
            )

        when (context) {
            DropType.ALWAYS -> alwaysDrops.add(entry)
            else -> error("Cannot addDrop to context $context")
        }
    }

    fun addAndProcessDrop(
        player: Player,
        drops: MutableList<Drop>,
        drop: Drop,
    ) {
        var cur: Drop? = drop
        while (cur != null) {
            drops.add(cur)
            processDrop(player, cur)
            cur = cur.extraDrop
        }
    }

    private fun processDrop(
        player: Player,
        drop: Drop,
    ) {
        val meta = drop.metadata ?: return

        if (meta.collectionLog) {
            val item = Item(drop.itemId, drop.amount)
            player.collectionLog.addItem(item)
        }

        if (meta.announce) {
            val itemName = ItemDefinitions.getItemDefinitions(drop.itemId).name
            val sourceName = drop.context.sourceName
            val action = drop.context.sourceAction

            Msg.newsRare(
                "${player.displayName} received $itemName from $action $sourceName!",
            )
        }
    }

    fun rollDrops(
        player: Player,
        combatLevel: Int,
    ): List<Drop> = rollDrops(player, combatLevel, multiplier = 1.0, rollsOverride = null)

    fun rollDrops(
        player: Player,
        combatLevel: Int,
        multiplier: Double = 1.0,
        rollsOverride: Int? = null,
    ): List<Drop> {
        val drops = mutableListOf<Drop>()

        val baseContext =
            DropContext(
                player = player,
                sourceName = name,
                sourceAction = sourceAction,
                tableCategory = category,
                dropSource = DropSource.MAIN,
            )

        val attempts = (rollsOverride ?: rolls).coerceAtLeast(1)

        // ALWAYS
        alwaysDrops.forEach { entry ->
            val drop = entry.roll(baseContext)
            drop?.let { addAndProcessDrop(player, drops, it) }
        }

        repeat(attempts) { attemptIndex ->
            var preRollHit = false

            // PREROLL TABLES
            for (entry in preRollTables) {
                val drop = entry.roll(baseContext, multiplier)

                if (drop != null) {
                    addAndProcessDrop(player, drops, drop)
                    preRollHit = true
                    break
                }
            }

            // PREROLL DENOM
            if (!preRollHit) {
                for (entry in preRollDenom) {
                    val drop = entry.roll(baseContext, multiplier)

                    if (drop != null) {
                        addAndProcessDrop(player, drops, drop)
                        preRollHit = true
                        break
                    }
                }
            }

            // RARE TABLE
            rareTableRoller?.let { it(baseContext, drops) }

            // GEM TABLE
            gemTableRoller?.let { it(baseContext, drops) }

            // HERB
            herbTableRollers.forEach { roller -> roller(baseContext, drops) }

            // SEED
            seedTableRoller?.let { it(baseContext, combatLevel, drops) }

            // MAIN
            if (!preRollHit) {
                val mainDrop =
                    mainDrops.roll(baseContext.copy(dropSource = DropSource.MAIN))

                mainDrop?.let { addAndProcessDrop(player, drops, it) }

                val minorDrop =
                    minorDrops.roll(baseContext.copy(dropSource = DropSource.MINOR))

                minorDrop?.let { addAndProcessDrop(player, drops, it) }
            }
        }

        // SPECIAL
        val specialDrop = specialDrops.roll(baseContext.copy(dropSource = DropSource.SPECIAL))

        specialDrop?.let { addAndProcessDrop(player, drops, it) }

        // TERTIARY
        tertiaryDrops.forEach { entry ->
            val drop = entry.roll(baseContext.copy(dropSource = DropSource.TERTIARY))
            drop?.let { addAndProcessDrop(player, drops, it) }
        }

        charmTable?.let { it ->
            val drop = it.roll(baseContext)
            drop?.let { addAndProcessDrop(player, drops, it) }
        }
        return drops
    }

    fun allDrops(): List<DropEntry> {
        val list = mutableListOf<DropEntry>()
        list.addAll(alwaysDrops)
        list.addAll(preRollDenom)
        list.addAll(tertiaryDrops)

        fun flatten(table: WeightedTable) {
            for (e in table.mutableEntries()) {
                when (e) {
                    is PackageWeightedEntry -> {
                        e.displayDrops.forEach { dd ->
                            list.add(DropEntry(dd.itemId, dd.amount))
                        }
                    }

                    is ItemWeightedEntry -> {
                        list.add(DropEntry(e.itemId, e.amount))
                    }

                    is NestedTableEntry -> {
                        flatten(e.table)
                    }
                }
            }
        }

        flatten(mainDrops)
        flatten(minorDrops)
        flatten(specialDrops)

        return list
    }

    fun setPreRollDenominator(value: Int) {
        preRollDenom.firstOrNull()?.denominator = value
    }

    fun totalDropCount(): Int = alwaysDrops.size + preRollDenom.size + mainDrops.size() + tertiaryDrops.size + specialDrops.size()

    private fun appendWeightedTableDebug(
        sb: StringBuilder,
        table: WeightedTable,
        tableLabel: String,
        path: String,
        depth: Int,
        parentChance: Double = 1.0,
    ) {
        val entries = table.mutableEntries()

        val configuredSize = table.tableSize
        val computedSum = entries.sumOf { it.weight }
        val effectiveTotal = if (configuredSize > 0) configuredSize else computedSum

        if (effectiveTotal <= 0) return

        val implicitNothingWeight = (effectiveTotal - computedSum).coerceAtLeast(0)
        val implicitNothingChance =
            if (implicitNothingWeight > 0) {
                implicitNothingWeight.toDouble() / effectiveTotal.toDouble()
            } else {
                0.0
            }

        sb.append(
            """
            {
                "kind":"table",
                "label":"${jsonEscape(tableLabel)}",
                "path":"${jsonEscape(path)}",
                "depth":$depth,
                "configuredSize":$configuredSize,
                "computedWeightSum":$computedSum,
                "effectiveTotalUsed":$effectiveTotal,
                "implicitNothingWeight":$implicitNothingWeight,
                "implicitNothingChance":${"%.12f".format(implicitNothingChance)},
                "parentChance":${"%.12f".format(parentChance)}
            },
            """.trimIndent(),
        )

        for ((index, entry) in entries.withIndex()) {
            val entryWeight = entry.weight
            if (entryWeight <= 0) continue

            val entryChanceInTable = entryWeight.toDouble() / effectiveTotal.toDouble()
            val combinedChance = parentChance * entryChanceInTable
            val combinedDenom =
                if (combinedChance > 0) {
                    1.0 / combinedChance
                } else {
                    Double.POSITIVE_INFINITY
                }

            when (entry) {
                is ItemWeightedEntry -> {
                    val itemName =
                        try {
                            ItemDefinitions.getItemDefinitions(entry.itemId).name
                        } catch (e: Exception) {
                            "Unknown"
                        }

                    sb.append(
                        """
                        {
                            "kind":"entry",
                            "entryType":"ITEM",
                            "index":$index,
                            "path":"${jsonEscape(path)}",
                            "depth":$depth,
                            "itemId":${entry.itemId},
                            "itemName":"${jsonEscape(itemName)}",
                            "amount":"${jsonEscape(entry.amount.toString())}",
                            "weight":$entryWeight,
                            "entryChanceInTable":${"%.12f".format(entryChanceInTable)},
                            "combinedChance":${"%.12f".format(combinedChance)},
                            "combinedDenom":${"%.6f".format(combinedDenom)}
                        },
                        """.trimIndent(),
                    )
                }

                is PackageWeightedEntry -> {
                    sb.append(
                        """
                        {
                            "kind":"entry",
                            "entryType":"PACKAGE",
                            "index":$index,
                            "path":"${jsonEscape(path)}",
                            "depth":$depth,
                            "weight":$entryWeight,
                            "entryChanceInTable":${"%.12f".format(entryChanceInTable)},
                            "combinedChance":${"%.12f".format(combinedChance)},
                            "combinedDenom":${"%.6f".format(combinedDenom)}
                        },
                        """.trimIndent(),
                    )

                    entry.displayDrops.forEach { dd ->

                        val itemName =
                            try {
                                ItemDefinitions.getItemDefinitions(dd.itemId).name
                            } catch (e: Exception) {
                                "Unknown"
                            }

                        sb.append(
                            """
                            {
                                "kind":"entry",
                                "entryType":"PACKAGE_ITEM",
                                "index":$index,
                                "path":"${jsonEscape(path)}",
                                "depth":$depth,
                                "itemId":${dd.itemId},
                                "itemName":"${jsonEscape(itemName)}",
                                "amount":"${jsonEscape(dd.amount.toString())}",
                                "combinedChance":${"%.12f".format(combinedChance)},
                                "combinedDenom":${"%.6f".format(combinedDenom)}
                            },
                            """.trimIndent(),
                        )
                    }
                }

                is NestedTableEntry -> {
                    sb.append(
                        """
                        {
                            "kind":"entry",
                            "entryType":"NESTED",
                            "index":$index,
                            "path":"${jsonEscape(path)}",
                            "depth":$depth,
                            "weight":$entryWeight,
                            "entryChanceInTable":${"%.12f".format(entryChanceInTable)},
                            "combinedChance":${"%.12f".format(combinedChance)},
                            "combinedDenom":${"%.6f".format(combinedDenom)}
                        },
                        """.trimIndent(),
                    )

                    appendWeightedTableDebug(
                        sb = sb,
                        table = entry.table,
                        tableLabel = "NestedTable",
                        path = "$path > nested#$index",
                        depth = depth + 1,
                        parentChance = combinedChance,
                    )
                }
            }
        }

        if (implicitNothingWeight > 0) {
            val combinedChance = parentChance * implicitNothingChance
            val combinedDenom =
                if (combinedChance > 0) {
                    1.0 / combinedChance
                } else {
                    Double.POSITIVE_INFINITY
                }

            sb.append(
                """
                {
                    "kind":"entry",
                    "entryType":"IMPLICIT_NOTHING",
                    "path":"${jsonEscape(path)}",
                    "depth":$depth,
                    "weight":$implicitNothingWeight,
                    "combinedChance":${"%.12f".format(combinedChance)},
                    "combinedDenom":${"%.6f".format(combinedDenom)}
                },
                """.trimIndent(),
            )
        }
    }

    private fun jsonEscape(s: String): String =
        buildString(s.length + 8) {
            for (c in s) {
                when (c) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(c)
                }
            }
        }

    fun exportRatesToJson(multiplier: Double): String {
        val sb = StringBuilder()

        sb.append("{\n")
        sb.append("\"name\":\"${jsonEscape(name)}\",\n")
        sb.append("\"multiplier\":$multiplier,\n")

        // ---------- ALWAYS ----------
        sb.append("\"alwaysDrops\":[\n")
        alwaysDrops.forEachIndexed { i, e ->
            sb.append(
                """
                {
                    "itemId":${e.itemId},
                    "name":"${jsonEscape(ItemDefinitions.getItemDefinitions(e.itemId).name)}",
                    "amount":"${jsonEscape(e.amount.toString())}"
                }
                """.trimIndent(),
            )
            if (i < alwaysDrops.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("],\n")

        // ---------- MAIN ----------
        sb.append("\"mainDrops\":[\n")
        appendWeightedTableDebug(
            sb = sb,
            table = mainDrops,
            tableLabel = "MainDrops",
            path = "main",
            depth = 0,
            parentChance = 1.0,
        )
        if (sb.endsWith(",\n")) sb.setLength(sb.length - 2)
        sb.append("\n],\n")

        // ---------- MINOR ----------
        sb.append("\"minorDrops\":[\n")
        appendWeightedTableDebug(
            sb = sb,
            table = minorDrops,
            tableLabel = "MinorDrops",
            path = "minor",
            depth = 0,
            parentChance = 1.0,
        )
        if (sb.endsWith(",\n")) sb.setLength(sb.length - 2)
        sb.append("\n],\n")

        // ---------- SPECIAL ----------
        sb.append("\"specialDrops\":[\n")
        appendWeightedTableDebug(
            sb = sb,
            table = specialDrops,
            tableLabel = "SpecialDrops",
            path = "special",
            depth = 0,
            parentChance = 1.0,
        )
        if (sb.endsWith(",\n")) sb.setLength(sb.length - 2)
        sb.append("\n]\n")

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
