package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.item.Item
import com.rs.java.game.item.meta.ItemMetadata
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

fun dropTable(
    rolls: Int = 1,
    name: String = "DropTable",
    category: TableCategory = TableCategory.REGULAR,
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
    herbTables: List<HerbTableConfig> = emptyList(),
    seedTable: SeedTableConfig? = null,
    rareTable: RareTableConfig? = null,
    godwarsRareTable: RareTableConfig? = null,
    gemTable: GemTableConfig? = null,
    godwarsGemTable: GemTableConfig? = null,
    block: DropTable.() -> Unit,
): DropTable =
    DropTable(rolls, name, category).apply {
        godwarsRareTable?.let { cfg ->
            rareTableConfig = cfg
            rareTableRoller = { player, drops ->
                val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)
                if (roll >= cfg.numerator) {
                    false
                } else {
                    godwarsRareDropTable.roll(player)?.let {
                        addAndProcessDrop(player, drops, it)
                        true
                    } ?: false
                }
            }
        }
        rareTable?.let { cfg ->
            rareTableConfig = cfg
            rareTableRoller = { player, drops ->
                val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)
                if (roll >= cfg.numerator) {
                    false
                } else {
                    rareDropTable.roll(player)?.let {
                        addAndProcessDrop(player, drops, it)
                        true
                    } ?: false
                }
            }
        }
        gemTable?.let { cfg ->
            gemTableConfig = cfg
            gemTableRoller = { player, drops ->
                val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)
                if (roll >= cfg.numerator) {
                    false
                } else {
                    gemDropTable.roll(player)?.let {
                        addAndProcessDrop(player, drops, it)
                        true
                    } ?: false
                }
            }
        }
        godwarsGemTable?.let { cfg ->
            gemTableConfig = cfg
            gemTableRoller = { player, drops ->
                val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)
                if (roll >= cfg.numerator) {
                    false
                } else {
                    godwarsGemDropTable.roll(player)?.let {
                        addAndProcessDrop(player, drops, it)
                        true
                    } ?: false
                }
            }
        }
        herbTableConfigs = herbTables

        if (herbTables.isNotEmpty()) {
            herbTables.forEach { cfg ->
                herbTable { player, drops ->

                    val roll = ThreadLocalRandom.current().nextInt(cfg.denominator)

                    if (roll < cfg.numerator) {
                        herbDropTable.roll(player)?.let { baseDrop ->
                            val rolledAmount = cfg.amount.random()
                            val newDrop = baseDrop.copy(amount = rolledAmount)
                            addAndProcessDrop(player, drops, newDrop)
                        }
                    }

                    false
                }
            }
        }
        seedTableConfig = seedTable
        if (seedTable != null) {
            seedTable { player, combatLevel, drops ->

                val roll = ThreadLocalRandom.current().nextInt(seedTable.denominator)
                if (roll >= seedTable.numerator) return@seedTable false

                val result =
                    DropTablesSetup.seedDropTable.roll(
                        seedTable.table,
                        player,
                        combatLevel,
                    )

                result?.let {
                    val finalAmount = seedTable.amount.random()
                    drops.add(it.copy(amount = finalAmount))
                    return@seedTable true
                }

                false
            }
        }

        block()
    }

class DropTable(
    private val rolls: Int = 1,
    var name: String = "DropTable",
    val category: TableCategory = TableCategory.REGULAR,
    var collectionGroup: String? = null,
) {
    private val alwaysDrops = mutableListOf<DropEntry>()
    private val preRollDrops = mutableListOf<PreRollDropEntry>()
    private val tertiaryDrops = mutableListOf<TertiaryDropEntry>()
    val mainDrops = WeightedTable()
    private val minorDrops = WeightedTable()
    private val specialDrops = WeightedTable()
    private var charmTable: SummoningCharms? = null

    private val herbTableRollers = mutableListOf<(Player, MutableList<Drop>) -> Boolean>()
    var rareTableRoller: ((Player, MutableList<Drop>) -> Boolean)? = null
    var gemTableRoller: ((Player, MutableList<Drop>) -> Boolean)? = null
    var herbTableConfigs: List<HerbTableConfig>? = null
    var rareTableConfig: RareTableConfig? = null
    var godwarsRareTableConfig: RareTableConfig? = null
    var gemTableConfig: GemTableConfig? = null
    var godwarsGemTableConfig: GemTableConfig? = null
    private var seedTableRoller: ((Player, Int, MutableList<Drop>) -> Boolean)? = null
    var seedTableConfig: SeedTableConfig? = null
    private var currentContext: DropType? = null
    private var currentWeightedTable: WeightedTable? = null

    override fun toString(): String = "DropTable(name='$name')"

    fun getAllDropsForDisplay(multiplier: Double = 1.0): List<DropDisplay> {
        val list = mutableListOf<DropDisplay>()

        fun addWeightedTableDisplays(
            table: WeightedTable,
            dropType: DropType,
            parentChance: Double = 1.0,
        ) {
            val entries = table.mutableEntries()
            val total = entries.sumOf { it.weight }.toDouble()
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
                        addWeightedTableDisplays(entry.table, dropType, chanceToEnter)
                    }

                    else -> {
                        // If you add more entry types later, ignore safely
                    }
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
        preRollDrops.forEach { entry ->
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

        addWeightedTableDisplays(mainDrops, DropType.MAIN)
        addWeightedTableDisplays(minorDrops, DropType.MINOR)
        addWeightedTableDisplays(specialDrops, DropType.SPECIAL)

        herbTableConfigs?.forEach { cfg ->

            val tableEntries = herbDropTable.getEntries()
            val totalWeight = tableEntries.sumOf { it.weight }.toDouble()

            tableEntries.forEach { herb ->

                // chance to land herb table
                val tableChance =
                    (cfg.numerator.toDouble() / cfg.denominator.toDouble())

                // chance of this herb inside table
                val herbChance =
                    herb.weight.toDouble() / totalWeight

                // combined chance
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

        seedTableConfig?.let { cfg ->

            val tableChance =
                cfg.numerator.toDouble() / cfg.denominator.toDouble()

            when (cfg.table) {
                SeedTableType.GENERAL -> {
                    // Only display summary line
                    list.add(
                        DropDisplay(
                            -1,
                            cfg.amount,
                            "General seed table",
                            DropType.PREROLL,
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
                                DropType.PREROLL,
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
                    type = DropType.GEM_TABLE, // or DropType.GEM_TABLE
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
        // If this DropTable is the Rare Drop Table itself
        /**if (name.equals("Rare Table", ignoreCase = true)) {
         val entries = rareDropTable.getEntries()
         val total = entries.sumOf { it.weight }.toDouble()

         for (entry in entries) {
         val chance = entry.weight / total
         val denom = (1.0 / chance).toInt()

         when (entry.itemId) {
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
         ),
         )
         }

         else -> {
         list.add(
         DropDisplay(
         entry.itemId,
         entry.amount,
         "1/$denom",
         DropType.MAIN,
         denom,
         ),
         )
         }
         }
         }
         }*/

        return list
    }

    fun getAllItemIdsForCollectionLog(): Set<Int> {
        val set = mutableSetOf<Int>()

        alwaysDrops.forEach {
            if (it.metadata.collectionLog) set.add(it.itemId)
        }

        preRollDrops.forEach { entry ->
            if (!entry.metadata.collectionLog) return@forEach

            val display = entry.displayItems
            if (!display.isNullOrEmpty()) {
                set.addAll(display)
                return@forEach
            }

            set.add(entry.itemId)
        }

        tertiaryDrops.forEach {
            if (it.metadata.collectionLog) set.add(it.itemId)
        }

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

        scanTable(mainDrops)
        scanTable(minorDrops)
        scanTable(specialDrops)

        return set
    }

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

    fun mainDrops(
        size: Int,
        block: MutableList<WeightedEntry>.() -> Unit,
    ) {
        currentContext = DropType.MAIN
        mainDrops.setSize(size)

        val prev = currentWeightedTable
        currentWeightedTable = null // main uses default
        mainDrops.mutableEntries().block()
        currentWeightedTable = prev

        currentContext = null
    }

    fun minorDrops(
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

    fun specialDrops(
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
        herbTableRollers += block
    }

    fun seedTable(block: (Player, Int, MutableList<Drop>) -> Boolean) {
        seedTableRoller = block
    }

    // ------------------ Drop DSL ------------------
    fun DropTable.drop(
        numerator: Int = 1,
        denominator: Int = 4,
        amount: IntRange = 1..1,
        condition: ((Player) -> Boolean)? = null,
        dynamicItem: (Player) -> Int?,
        displayItems: List<String>? = null,
        meta: (DropMetadata.() -> Unit)? = null,
    ) {
        val ctx = currentContext ?: error("Cannot call drop() outside a drop type scope")

        if (ctx != DropType.PREROLL) {
            error("dynamicItem drops are only supported in preRollDrops")
        }
        val metadata = DropMetadata().apply { meta?.invoke(this) }
        preRollDrops.add(
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

    fun MutableList<WeightedEntry>.nestedTable(
        size: Int,
        block: MutableList<WeightedEntry>.() -> Unit,
    ) {
        val nested = WeightedTable().apply { setSize(size) }

        val prev = currentWeightedTable
        currentWeightedTable = nested
        nested.mutableEntries().block()
        currentWeightedTable = prev

        nested.mutableEntries().forEach { entry ->
            this.add(entry)
        }
    }

    fun DropTable.drop(
        item: String,
        amount: IntRange = 1..1,
        weight: Int = 1,
        numerator: Int = 1,
        denominator: Int = 4,
        condition: ((Player) -> Boolean)? = null,
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
                preRollDrops.add(
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
        condition: ((Player) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null,
        meta: (DropMetadata.() -> Unit)? = null,
    ) = drop(item, amount..amount, weight, numerator, denominator, condition, customLogic, meta)

    private fun addDrop(
        context: DropType,
        item: Int,
        amount: IntRange,
        condition: ((Player) -> Boolean)? = null,
        customLogic: ((Player, Drop?) -> Unit)? = null,
        metadata: DropMetadata = DropMetadata(),
    ) {
        val entry = DropEntry(item, amount, always = context == DropType.ALWAYS, condition = condition, metadata = metadata)
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
            Msg.newsRare("${player.displayName} received ${ItemDefinitions.getItemDefinitions(drop.itemId).name}!")
        }
    }

    fun rollDrops(
        player: Player,
        combatLevel: Int,
    ): List<Drop> = rollDrops(player, combatLevel, 1.0)

    fun rollDrops(
        player: Player,
        combatLevel: Int,
        multiplier: Double = 1.0,
    ): List<Drop> {
        val drops = mutableListOf<Drop>()

        // ALWAYS
        alwaysDrops.forEach {
            it.roll(player)?.let { drop ->
                addAndProcessDrop(player, drops, drop)
            }
        }

        // PREROLL
        var preRollHit = false
        for (entry in preRollDrops) {
            val drop = entry.roll(player, multiplier)
            if (drop != null) {
                addAndProcessDrop(player, drops, drop)
                preRollHit = true
                break
            }
        }

        // RARE TABLE
        rareTableRoller?.let {
            if (it(player, drops)) return drops
        }

        // GEM TABLE
        gemTableRoller?.let {
            if (it(player, drops)) return drops
        }

        // HERB TABLE
        herbTableRollers.forEach { it(player, drops) }

        // SEED TABLE
        seedTableRoller?.let {
            if (it(player, combatLevel, drops)) return drops
        }

        // MAIN ROLLS
        if (!preRollHit) {
            repeat(rolls) {
                if (gemTableRoller?.invoke(player, drops) == true) return@repeat

                mainDrops.roll(player, source = DropSource.MAIN)?.let {
                    addAndProcessDrop(player, drops, it)
                }

                minorDrops.roll(player, source = DropSource.MINOR)?.let {
                    addAndProcessDrop(player, drops, it)
                }
            }
        }

        // SPECIAL
        if (specialDrops.size() > 0) {
            specialDrops.roll(player, source = DropSource.SPECIAL)?.let {
                addAndProcessDrop(player, drops, it)
            }
        }

        // TERTIARY
        tertiaryDrops.forEach {
            it.roll(player, multiplier)?.let { drop ->
                addAndProcessDrop(player, drops, drop)
            }
        }

        // CHARMS
        charmTable?.roll()?.let {
            addAndProcessDrop(player, drops, it)
        }

        return drops
    }

    fun allDrops(): List<DropEntry> {
        val list = mutableListOf<DropEntry>()
        list.addAll(alwaysDrops)
        list.addAll(preRollDrops)
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
        preRollDrops.firstOrNull()?.denominator = value
    }

    fun totalDropCount(): Int = alwaysDrops.size + preRollDrops.size + mainDrops.size() + tertiaryDrops.size + specialDrops.size()

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
                    "}",
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
                    "}",
            )

            if (i < preRollDrops.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("],\n")

        // ---------------- MAIN / MINOR / SPECIAL ----------------
        fun appendWeightedTable(
            table: WeightedTable,
            parentChance: Double = 1.0,
        ) {
            val entries = table.mutableEntries()
            val total = entries.sumOf { it.weight }.toDouble()
            if (total <= 0.0) return

            for (entry in entries) {
                when (entry) {
                    is PackageWeightedEntry -> {
                        val chance = parentChance * (entry.weight / total)
                        val denom = (1.0 / chance).coerceAtLeast(1.0)

                        entry.displayDrops.forEach { dd ->
                            sb.append(
                                "{" +
                                    "\"itemId\":${dd.itemId}," +
                                    "\"name\":\"${ItemDefinitions.getItemDefinitions(dd.itemId).name}\"," +
                                    "\"amount\":\"${dd.amount}\"," +
                                    "\"rate\":\"1/${"%.2f".format(denom)}\"" +
                                    "},\n",
                            )
                        }
                    }

                    is ItemWeightedEntry -> {
                        val chance = parentChance * (entry.weight / total)
                        val denom = (1.0 / chance).coerceAtLeast(1.0)

                        sb.append(
                            "{" +
                                "\"itemId\":${entry.itemId}," +
                                "\"name\":\"${ItemDefinitions.getItemDefinitions(entry.itemId).name}\"," +
                                "\"amount\":\"${entry.amount}\"," +
                                "\"rate\":\"1/${"%.2f".format(denom)}\"" +
                                "},\n",
                        )
                    }

                    is NestedTableEntry -> {
                        val nestedChance = parentChance * (entry.weight / total)
                        appendWeightedTable(entry.table, nestedChance)
                    }
                }
            }
        }

        sb.append("\"mainDrops\":[\n")
        appendWeightedTable(mainDrops)
        if (sb.endsWith(",\n")) sb.setLength(sb.length - 2)
        sb.append("\n],\n")

        sb.append("\"minorDrops\":[\n")
        appendWeightedTable(minorDrops)
        if (sb.endsWith(",\n")) sb.setLength(sb.length - 2)
        sb.append("\n],\n")

        sb.append("\"specialDrops\":[\n")
        appendWeightedTable(specialDrops)
        if (sb.endsWith(",\n")) sb.setLength(sb.length - 2)
        sb.append("\n],\n")

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
                    "}",
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
