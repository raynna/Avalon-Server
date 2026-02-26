package com.rs.kotlin.game.player.interfaces

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.core.cache.defintions.NPCDefinitions
import com.rs.java.game.player.Player
import com.rs.kotlin.game.npc.drops.DropTable
import com.rs.kotlin.game.npc.drops.DropTableRegistry
import com.rs.kotlin.game.npc.drops.DropTableSource
import com.rs.kotlin.game.npc.drops.DropType

/**
 * Drop Viewer Interface
 *
 * @Author -Andreas
 * @Project Avalon 727
 * Handles:
 * - Displaying NPC drop tables
 * - Searching NPCs by name
 * - Searching NPCs by item
 * - Pagination
 * - Dynamic title updates
 */
object DropInterface {
    private const val INTERFACE_ID = 3005

    private const val NOTHING_SPRITE = 11372

    private const val ROW_START = 73
    private const val ROW_END = 905
    private const val ROW_STRIDE = 8

    private const val ICON_OFFSET = 2
    private const val NAME_OFFSET = 3
    private const val AMOUNT_LABEL = 4
    private const val AMOUNT_VALUE = 5
    private const val CHANCE_LABEL = 6
    private const val CHANCE_VALUE = 7

    private const val NPC_LIST_START = 37
    private const val NPC_LIST_END = 49

    private const val PREVIOUS_BUTTON = 50
    private const val NEXT_BUTTON = 51
    private const val TITLE_COMPONENT = 15
    private const val SEARCHITEM_BUTTON = 23
    private const val SEARCHNPC_BUTTON = 27

    private const val ATTR_FOUND = "drop_viewer_found_npcs"
    private const val ATTR_PAGE = "drop_viewer_npc_page"
    private const val ATTR_ITEM_FILTER = "drop_viewer_item_filter"
    private const val ATTR_CURRENT = "current_drop_source"
    private const val ATTR_CURRENT_NAME = "current_drop_name"
    private const val ATTR_IN_SEARCH = "drop_viewer_in_search"
    private const val ATTR_SOURCE_FILTER = "drop_viewer_source_filter"
    private const val ATTR_TABLE_STACK = "drop_viewer_table_stack"
    private const val ATTR_CURRENT_DROPS = "drop_viewer_current_drops"

    private data class TableFrame(
        val title: String,
        val drops: List<DropDisplay>,
    )

    private fun resetState(player: Player) {
        player.temporaryAttributtes[ATTR_PAGE] = 0
        player.temporaryAttributtes.remove(ATTR_FOUND)
        player.temporaryAttributtes.remove(ATTR_ITEM_FILTER)
        player.temporaryAttributtes.remove(ATTR_CURRENT)
        player.temporaryAttributtes.remove(ATTR_CURRENT_NAME)
        player.temporaryAttributtes.remove(ATTR_IN_SEARCH)
        player.temporaryAttributtes.remove(ATTR_SOURCE_FILTER)
        player.temporaryAttributtes.remove("drop_find")
        player.temporaryAttributtes.remove("npc_find")
    }

    private fun dropRateMult(): Double = if (Settings.DROP_MULTIPLIER > 1.0) Settings.DROP_MULTIPLIER else 1.0

    private fun tableFor(source: DropTableSource): DropTable? = DropTableRegistry.getTableForSource(source)

    private fun sourceName(source: DropTableSource): String =
        when (source) {
            is DropTableSource.Npc -> {
                NPCDefinitions.getNPCDefinitions(source.id).name
            }

            is DropTableSource.Named -> {
                source.key.replaceFirstChar { it.uppercase() }
            }

            is DropTableSource.Item -> {
                ItemDefinitions.getItemDefinitions(source.id).name
            }

            is DropTableSource.Object -> {
                DropTableRegistry.getObjectAlias(source.id)
                    ?: "Object ${source.id}"
            }
        }

    fun open(
        player: Player,
        buildList: Boolean = true,
    ) {
        player.temporaryAttributtes[ATTR_PAGE] = 0
        player.interfaceManager.sendInterface(INTERFACE_ID)

        var row = ROW_START
        while (row <= ROW_END) {
            hideRow(player, row)
            row += ROW_STRIDE
        }

        player.packets.sendHideIComponent(INTERFACE_ID, PREVIOUS_BUTTON, true)
        player.packets.sendHideIComponent(INTERFACE_ID, NEXT_BUTTON, true)
        refreshScrollbar(player, 0)

        if (buildList) {
            buildInitialList(player)
            sendSourceList(player)
        }
        player.setCloseInterfacesEvent {
            resetState(player)
        }
    }

    private fun buildInitialList(player: Player) {
        val results = mutableListOf<DropTableSource>()
        val seen = HashSet<String>()

        for (source in DropTableRegistry.getAllSources()) {
            val table = tableFor(source) ?: continue

            if (table.getAllDropsForDisplay(dropRateMult()).isEmpty()) {
                continue
            }

            val name = sourceName(source).lowercase()

            if (seen.add(name)) {
                results += source
            }
        }

        results.sortBy { sourceName(it) }

        player.temporaryAttributtes[ATTR_FOUND] = results
        player.temporaryAttributtes[ATTR_PAGE] = 0
    }

    fun openForSource(
        player: Player,
        source: DropTableSource,
    ) {
        player.temporaryAttributtes.remove(ATTR_CURRENT)
        player.temporaryAttributtes.remove(ATTR_CURRENT_NAME)
        player.temporaryAttributtes[ATTR_SOURCE_FILTER] =
            sourceName(source)
        open(player, buildList = false)

        val list = mutableListOf(source)

        player.temporaryAttributtes[ATTR_FOUND] = list
        player.temporaryAttributtes[ATTR_PAGE] = 0

        sendSourceList(player)
        selectSource(player, source)
    }

    fun openForItem(
        player: Player,
        itemId: Int,
    ) {
        player.temporaryAttributtes.remove(ATTR_CURRENT)
        player.temporaryAttributtes.remove(ATTR_CURRENT_NAME)

        open(player, false)

        val results = mutableListOf<DropTableSource>()
        val seen = HashSet<String>()

        val itemSource = DropTableSource.Item(itemId)
        if (DropTableRegistry.getTableForSource(itemSource) != null) {
            results += itemSource
            seen += sourceName(itemSource).lowercase()
        }

        for (source in DropTableRegistry.getAllSources()) {
            val table = tableFor(source) ?: continue

            for (drop in table.getAllDropsForDisplay(dropRateMult())) {
                if (drop.itemId == itemId) {
                    val key = sourceName(source).lowercase()
                    if (seen.add(key)) {
                        results += source
                    }

                    break
                }
            }
        }

        player.temporaryAttributtes[ATTR_FOUND] = results
        player.temporaryAttributtes[ATTR_PAGE] = 0

        val itemName = ItemDefinitions.getItemDefinitions(itemId).name
        updateTitle(player, "Viewing Sources", "Filtered by item: $itemName")
        player.temporaryAttributtes[ATTR_IN_SEARCH] = true
        player.temporaryAttributtes[ATTR_ITEM_FILTER] = itemName
        sendSourceList(player)

        if (results.isNotEmpty()) {
            selectSource(player, results[0])
        }
    }

    fun openForObject(
        player: Player,
        objectId: Int,
    ) {
        val bound = DropTableRegistry.getSourceForObject(objectId)

        if (bound != null) {
            openForSource(player, bound)
            return
        }

        open(player, false)
        selectItem(player, objectId, true)
    }

    private fun openMegaTableWithNpcList(player: Player) {
        val rareSource = DropTableSource.Named("Mega Table")
        val rareTable = tableFor(rareSource) ?: return

        val sources = DropTableRegistry.getSourcesWithMegaTable()

        player.temporaryAttributtes[ATTR_FOUND] = sources
        player.temporaryAttributtes[ATTR_PAGE] = 0
        player.temporaryAttributtes[ATTR_CURRENT] = rareSource
        player.temporaryAttributtes[ATTR_CURRENT_NAME] = "Mega Table"

        sendSourceList(player)
        renderTable(player, rareTable)

        updateTitle(player, "Viewing: Mega Table")
    }

    private fun openRareTableWithNpcList(player: Player) {
        val rareSource = DropTableSource.Named("Rare Table")
        val rareTable = tableFor(rareSource) ?: return

        val sources = DropTableRegistry.getSourcesWithRareTable()

        player.temporaryAttributtes[ATTR_FOUND] = sources
        player.temporaryAttributtes[ATTR_PAGE] = 0
        player.temporaryAttributtes[ATTR_CURRENT] = rareSource
        player.temporaryAttributtes[ATTR_CURRENT_NAME] = "Rare Table"

        sendSourceList(player)
        renderTable(player, rareTable)

        updateTitle(player, "Viewing: Rare Table")
    }

    private fun openGemTableWithNpcList(player: Player) {
        val gemSource = DropTableSource.Named("Gem Table")
        val gemTable = tableFor(gemSource) ?: return

        val sources = DropTableRegistry.getSourcesWithGemTable()

        player.temporaryAttributtes[ATTR_FOUND] = sources
        player.temporaryAttributtes[ATTR_PAGE] = 0
        player.temporaryAttributtes[ATTR_CURRENT] = gemSource
        player.temporaryAttributtes[ATTR_CURRENT_NAME] = "Gem Table"

        sendSourceList(player)
        renderTable(player, gemTable)

        updateTitle(player, "Viewing: Gem Table")
    }

    private fun clearViewer(player: Player) {
        refreshScrollbar(player, 0)
        resetState(player)

        var row = ROW_START
        while (row <= ROW_END) {
            hideRow(player, row)
            row += ROW_STRIDE
        }

        buildInitialList(player)

        player.temporaryAttributtes[ATTR_PAGE] = 0

        sendSourceList(player)
        updateTitle(player, "Drop Viewer")
        refreshScrollbar(player, 0)
    }

    fun handleButtons(
        player: Player,
        componentId: Int,
    ) {
        when (componentId) {
            SEARCHITEM_BUTTON -> {
                player.packets.sendInputNameScript("Enter item name")
                player.temporaryAttributtes["drop_find"] = true
                return
            }

            SEARCHNPC_BUTTON -> {
                player.packets.sendInputNameScript("Enter name")
                player.temporaryAttributtes["npc_find"] = true
                player.temporaryAttributtes.remove(ATTR_ITEM_FILTER)
                return
            }

            NEXT_BUTTON -> {
                nextPage(player)
                return
            }

            PREVIOUS_BUTTON -> {
                val stack =
                    player.temporaryAttributtes[ATTR_TABLE_STACK] as? MutableList<TableFrame>

                if (!stack.isNullOrEmpty()) {
                    val prev = stack.removeLast()
                    player.temporaryAttributtes[ATTR_CURRENT_DROPS] = prev.drops
                    updateTitle(player, "Viewing: ${prev.title}")
                    renderDrops(player, prev.drops)
                    updatePageButtons(player)
                    return
                }

                val page = player.temporaryAttributtes[ATTR_PAGE] as? Int ?: 0

                val hasFilter =
                    player.temporaryAttributtes.containsKey(ATTR_ITEM_FILTER) ||
                        player.temporaryAttributtes.containsKey(ATTR_SOURCE_FILTER)

                val inSearch =
                    player.temporaryAttributtes.containsKey(ATTR_IN_SEARCH)

                val hasSelected =
                    player.temporaryAttributtes.containsKey(ATTR_CURRENT)

                if (page <= 0 && (hasFilter || inSearch || hasSelected)) {
                    clearViewer(player)
                } else {
                    previousPage(player)
                }
                return
            }
        }

        if (componentId in NPC_LIST_START..NPC_LIST_END) {
            val list =
                player.temporaryAttributtes[ATTR_FOUND] as? List<DropTableSource>
                    ?: return

            val page = player.temporaryAttributtes[ATTR_PAGE] as? Int ?: 0
            val index = componentId - NPC_LIST_START + page * 13
            val source = list.getOrNull(index) ?: return

            selectSource(player, source)
            updatePageButtons(player)
            return
        }

        if (componentId in ROW_START..ROW_END &&
            (componentId - ROW_START) % ROW_STRIDE == 0
        ) {
            val slot = (componentId - ROW_START) / ROW_STRIDE

            val source =
                player.temporaryAttributtes[ATTR_CURRENT] as? DropTableSource
                    ?: return

            val root = tableFor(source) ?: return

            val currentDrops =
                player.temporaryAttributtes[ATTR_CURRENT_DROPS] as? List<DropDisplay>
                    ?: root.getAllDropsForDisplay(dropRateMult()).also {
                        player.temporaryAttributtes[ATTR_CURRENT_DROPS] = it
                    }

            val drop = currentDrops.getOrNull(slot) ?: return

            when (drop.type) {
                DropType.SUB_TABLE -> {
                    val subTable = drop.tableReference ?: return

                    val stack =
                        (player.temporaryAttributtes[ATTR_TABLE_STACK] as? MutableList<TableFrame>)
                            ?: mutableListOf<TableFrame>().also { player.temporaryAttributtes[ATTR_TABLE_STACK] = it }

                    val currentTitle =
                        player.temporaryAttributtes[ATTR_CURRENT_NAME] as? String ?: "Drop Viewer"

                    stack.add(
                        TableFrame(
                            title = currentTitle,
                            drops = currentDrops,
                        ),
                    )

                    val subDrops =
                        root.getWeightedTableDropsForDisplay(
                            table = subTable,
                            dropType = DropType.MAIN,
                            parentChance = drop.parentChance ?: 1.0,
                            expandNested = true,
                        )

                    player.temporaryAttributtes[ATTR_CURRENT_DROPS] = subDrops
                    updateTitle(player, "Viewing: ${drop.tableName ?: "Category"}")
                    renderDrops(player, subDrops)
                    updatePageButtons(player)
                    return
                }

                DropType.MEGA_TABLE -> {
                    val sources = DropTableRegistry.getSourcesWithGemTable()
                    player.temporaryAttributtes[ATTR_FOUND] = sources
                    player.temporaryAttributtes[ATTR_PAGE] = 0
                    openMegaTableWithNpcList(player)
                    updatePageButtons(player)
                    return
                }

                DropType.RARE_TABLE -> {
                    val sources = DropTableRegistry.getSourcesWithRareTable()
                    player.temporaryAttributtes[ATTR_FOUND] = sources
                    player.temporaryAttributtes[ATTR_PAGE] = 0
                    openRareTableWithNpcList(player)
                    updatePageButtons(player)
                    return
                }

                DropType.GEM_TABLE -> {
                    val sources = DropTableRegistry.getSourcesWithRareTable()
                    player.temporaryAttributtes[ATTR_FOUND] = sources
                    player.temporaryAttributtes[ATTR_PAGE] = 0
                    openGemTableWithNpcList(player)
                    updatePageButtons(player)
                    return
                }

                else -> {
                    selectItem(player, drop.itemId, false)
                    return
                }
            }
        }
    }

    fun sendSourceList(player: Player) {
        for (i in NPC_LIST_START..NPC_LIST_END) {
            player.packets.sendTextOnComponent(INTERFACE_ID, i, "")
        }

        val list =
            player.temporaryAttributtes[ATTR_FOUND] as? List<DropTableSource>

        if (list.isNullOrEmpty()) {
            player.packets.sendTextOnComponent(INTERFACE_ID, NPC_LIST_START, "No results found.")
            updateTitle(player, "No results found.")
            updatePageButtons(player)
            return
        }

        val page = player.temporaryAttributtes[ATTR_PAGE] as? Int ?: 0

        for (i in 0 until 13) {
            val index = i + page * 13
            if (index >= list.size) break

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                NPC_LIST_START + i,
                sourceName(list[index]),
            )
        }

        updatePageButtons(player)
    }

    fun selectSource(
        player: Player,
        source: DropTableSource,
    ) {
        val table = tableFor(source) ?: return

        player.temporaryAttributtes[ATTR_CURRENT] = source
        player.temporaryAttributtes[ATTR_CURRENT_NAME] = sourceName(source)

        val itemFilter =
            player.temporaryAttributtes[ATTR_ITEM_FILTER] as? String

        val sourceFilter =
            player.temporaryAttributtes[ATTR_SOURCE_FILTER] as? String

        when {
            itemFilter != null -> {
                updateTitle(
                    player,
                    "Viewing: ${sourceName(source)}",
                    "Filtered by item: $itemFilter",
                )
            }

            sourceFilter != null -> {
                updateTitle(
                    player,
                    "Viewing: ${sourceName(source)}",
                    "Filtered by: $sourceFilter",
                )
            }

            else -> {
                updateTitle(
                    player,
                    "Viewing: ${sourceName(source)}",
                )
            }
        }
        val drops = table.getAllDropsForDisplay(dropRateMult())

        player.temporaryAttributtes[ATTR_TABLE_STACK] = mutableListOf<TableFrame>()
        player.temporaryAttributtes[ATTR_CURRENT_DROPS] = drops

        renderDrops(player, drops)
    }

    private fun renderTable(
        player: Player,
        table: DropTable,
    ) {
        var row = ROW_START
        while (row <= ROW_END) {
            hideRow(player, row)
            row += ROW_STRIDE
        }
        player.temporaryAttributtes[ATTR_TABLE_STACK] = mutableListOf<TableFrame>()
        val drops =
            table.getAllDropsForDisplay(dropRateMult())
        player.temporaryAttributtes[ATTR_CURRENT_DROPS] = drops
        val maxRows = ((ROW_END - ROW_START) / ROW_STRIDE) + 1
        refreshScrollbar(player, drops.size)
        row = ROW_START

        for (drop in drops.take(maxRows)) {
            val name =
                when (drop.type) {
                    DropType.SUB_TABLE -> drop.tableName ?: "Category"
                    DropType.MEGA_TABLE -> "Mega table"
                    DropType.RARE_TABLE -> "Rare table"
                    DropType.GEM_TABLE -> "Gem table"
                    DropType.NOTHING -> "Nothing"
                    else -> ItemDefinitions.getItemDefinitions(drop.itemId).name
                }

            showRow(player, row)
            if (drop.type == DropType.SUB_TABLE) {
                if (drop.itemId > 0) {
                    player.packets.sendItemOnIComponent(
                        INTERFACE_ID,
                        row + ICON_OFFSET,
                        drop.itemId,
                        1,
                    )
                } else {
                    player.packets.sendItemOnIComponent(
                        INTERFACE_ID,
                        row + ICON_OFFSET,
                        -1,
                        0,
                    )
                }

                player.packets.sendTextOnComponent(
                    INTERFACE_ID,
                    row + NAME_OFFSET,
                    "<col=ffffff><b>${drop.tableName}</b></col>",
                )

                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_LABEL, "")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_VALUE, "")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + CHANCE_LABEL, "")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + CHANCE_VALUE, "")
            }
            if (drop.type == DropType.NOTHING) {
                player.packets.sendItemOnIComponent(
                    INTERFACE_ID,
                    row + ICON_OFFSET,
                    -1,
                    0,
                )

                player.packets.sendSpriteOnIComponent(
                    INTERFACE_ID,
                    row + ICON_OFFSET,
                    NOTHING_SPRITE,
                )
            } else {
                player.packets.sendItemOnIComponent(
                    INTERFACE_ID,
                    row + ICON_OFFSET,
                    drop.itemId,
                    (drop.amount ?: 1..1).last,
                )
            }

            player.packets.sendTextOnComponent(INTERFACE_ID, row + NAME_OFFSET, wrap(name))
            if (drop.type != DropType.MEGA_TABLE && drop.type != DropType.RARE_TABLE && drop.type != DropType.GEM_TABLE &&
                drop.type != DropType.NOTHING
            ) {
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_LABEL, "Amount:")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_VALUE, (drop.amount ?: 1..1).toDisplayString())
            } else {
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_LABEL, "")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_VALUE, "")
            }
            if (drop.type == DropType.NOTHING) {
                // player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_LABEL, "<br>" + wrap("Removed if wearing ring of wealth"))
            }
            player.packets.sendTextOnComponent(INTERFACE_ID, row + CHANCE_LABEL, "Chance:")
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                row + CHANCE_VALUE,
                colourForDrop(drop, buildRarityText(drop)),
            )

            row += ROW_STRIDE
        }
        // refreshScrollbar(player, drops.size)
    }

    private fun renderDrops(
        player: Player,
        drops: List<DropDisplay>,
    ) {
        var row = ROW_START
        while (row <= ROW_END) {
            hideRow(player, row)
            row += ROW_STRIDE
        }

        val maxRows = ((ROW_END - ROW_START) / ROW_STRIDE) + 1
        refreshScrollbar(player, drops.size)
        row = ROW_START

        for (drop in drops.take(maxRows)) {
            val name =
                when (drop.type) {
                    DropType.SUB_TABLE -> drop.tableName ?: "Category"
                    DropType.MEGA_TABLE -> "Mega table"
                    DropType.RARE_TABLE -> "Rare table"
                    DropType.GEM_TABLE -> "Gem table"
                    DropType.NOTHING -> "Nothing"
                    else -> ItemDefinitions.getItemDefinitions(drop.itemId).name
                }

            showRow(player, row)

            if (drop.type == DropType.SUB_TABLE) {
                if (drop.itemId > 0) {
                    player.packets.sendItemOnIComponent(
                        INTERFACE_ID,
                        row + ICON_OFFSET,
                        drop.itemId,
                        1,
                    )
                } else {
                    player.packets.sendItemOnIComponent(
                        INTERFACE_ID,
                        row + ICON_OFFSET,
                        -1,
                        0,
                    )
                }

                player.packets.sendTextOnComponent(
                    INTERFACE_ID,
                    row + NAME_OFFSET,
                    "<col=ffffff><b>${drop.tableName}</b></col>",
                )

                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_LABEL, "")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_VALUE, "")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + CHANCE_LABEL, "")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + CHANCE_VALUE, "")
            }

            if (drop.type == DropType.NOTHING) {
                player.packets.sendItemOnIComponent(
                    INTERFACE_ID,
                    row + ICON_OFFSET,
                    -1,
                    0,
                )

                player.packets.sendSpriteOnIComponent(
                    INTERFACE_ID,
                    row + ICON_OFFSET,
                    NOTHING_SPRITE,
                )
            } else {
                player.packets.sendItemOnIComponent(
                    INTERFACE_ID,
                    row + ICON_OFFSET,
                    drop.itemId,
                    (drop.amount).last,
                )
            }

            player.packets.sendTextOnComponent(INTERFACE_ID, row + NAME_OFFSET, wrap(name))

            if (
                drop.type != DropType.MEGA_TABLE &&
                drop.type != DropType.RARE_TABLE &&
                drop.type != DropType.GEM_TABLE &&
                drop.type != DropType.NOTHING
            ) {
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_LABEL, "Amount:")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_VALUE, drop.amount.toDisplayString())
            } else {
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_LABEL, "")
                player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_VALUE, "")
            }

            player.packets.sendTextOnComponent(INTERFACE_ID, row + CHANCE_LABEL, "Chance:")
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                row + CHANCE_VALUE,
                colourForDrop(drop, buildRarityText(drop)),
            )

            row += ROW_STRIDE
        }
    }

    fun selectItem(
        player: Player,
        itemId: Int,
        autoSelect: Boolean,
    ) {
        val itemName = ItemDefinitions.getItemDefinitions(itemId).name

        updateTitle(player, "Viewing Sources", "Filtered by item: $itemName")

        player.temporaryAttributtes[ATTR_ITEM_FILTER] = itemName
        player.temporaryAttributtes[ATTR_IN_SEARCH] = true

        val results = mutableListOf<DropTableSource>()
        val seen = HashSet<String>()

        for (source in DropTableRegistry.getAllSources()) {
            val table = tableFor(source) ?: continue

            for (drop in table.getAllDropsForDisplay(dropRateMult())) {
                if (drop.itemId == itemId) {
                    val key = sourceName(source).lowercase()

                    if (seen.add(key)) {
                        results += source
                    }

                    break
                }
            }
        }

        player.temporaryAttributtes[ATTR_FOUND] = results
        player.temporaryAttributtes[ATTR_PAGE] = 0

        sendSourceList(player)

        if (autoSelect) {
            val current =
                player.temporaryAttributtes[ATTR_CURRENT] as? DropTableSource

            if (current == null && results.isNotEmpty()) {
                selectSource(player, results[0])
            }
        }
    }

    private fun nextPage(player: Player) {
        val list =
            player.temporaryAttributtes[ATTR_FOUND] as? List<*>
                ?: return

        val page = player.temporaryAttributtes[ATTR_PAGE] as? Int ?: 0
        val maxPage = (list.size + 12) / 13

        if (page + 1 >= maxPage) return

        player.temporaryAttributtes[ATTR_PAGE] = page + 1
        sendSourceList(player)
    }

    private fun previousPage(player: Player) {
        val page = player.temporaryAttributtes[ATTR_PAGE] as? Int ?: 0
        if (page <= 0) return

        player.temporaryAttributtes[ATTR_PAGE] = page - 1
        sendSourceList(player)
    }

    private fun updatePreviousButtonText(
        player: Player,
        text: String,
    ) {
        player.packets.sendTextOnComponent(INTERFACE_ID, PREVIOUS_BUTTON, text)
    }

    private fun updatePageButtons(player: Player) {
        val list = player.temporaryAttributtes[ATTR_FOUND] as? List<*>
        val page = player.temporaryAttributtes[ATTR_PAGE] as? Int ?: 0

        val hasFilter =
            player.temporaryAttributtes.containsKey(ATTR_ITEM_FILTER) ||
                player.temporaryAttributtes.containsKey(ATTR_SOURCE_FILTER)
        val inSearch = player.temporaryAttributtes.containsKey(ATTR_IN_SEARCH)
        val hasSelected = player.temporaryAttributtes.containsKey(ATTR_CURRENT)

        // Check if we're in a subtable (has stack entries)
        val stack = player.temporaryAttributtes[ATTR_TABLE_STACK] as? MutableList<TableFrame>
        val inSubTable = !stack.isNullOrEmpty()

        val canClear = page == 0 && (hasFilter || inSearch || hasSelected || list.isNullOrEmpty())

        // Hide previous button if at root level with no filters/selections
        player.packets.sendHideIComponent(
            INTERFACE_ID,
            PREVIOUS_BUTTON,
            page == 0 && !(hasFilter || inSearch || hasSelected) && !inSubTable,
        )

        // Update previous button text based on context
        updatePreviousButtonText(
            player,
            when {
                inSubTable -> "Go Back"
                canClear -> "Clear"
                else -> "Previous"
            },
        )

        // Hide next button if:
        // - In subtable (shouldn't have next page navigation)
        // - Or at last page of list
        if (inSubTable) {
            player.packets.sendHideIComponent(INTERFACE_ID, NEXT_BUTTON, true)
        } else if (list.isNullOrEmpty()) {
            player.packets.sendHideIComponent(INTERFACE_ID, NEXT_BUTTON, true)
        } else {
            val maxPage = (list.size + 12) / 13
            player.packets.sendHideIComponent(
                INTERFACE_ID,
                NEXT_BUTTON,
                page + 1 >= maxPage,
            )
        }
    }

    private fun refreshScrollbar(
        player: Player,
        rows: Int,
    ) {
        val contentHeight = rows * 35
        val needsScroll = contentHeight > 264

        player.packets.sendCSVarInteger(
            350,
            if (needsScroll) contentHeight else 225,
        )

        // player.packets.sendRunScript(10006)
    }

    private fun updateTitle(
        player: Player,
        line1: String,
        line2: String? = null,
    ) {
        val text = if (line2 == null) line1 else "$line1<br>$line2"

        player.packets.sendTextOnComponent(INTERFACE_ID, TITLE_COMPONENT, text)
    }

    private fun hideRow(
        player: Player,
        row: Int,
    ) {
        player.packets.sendHideIComponent(INTERFACE_ID, row + ICON_OFFSET, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + NAME_OFFSET, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + AMOUNT_LABEL, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + AMOUNT_VALUE, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + CHANCE_LABEL, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + CHANCE_VALUE, true)
    }

    private fun showRow(
        player: Player,
        row: Int,
    ) {
        player.packets.sendHideIComponent(INTERFACE_ID, row + ICON_OFFSET, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + NAME_OFFSET, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + AMOUNT_LABEL, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + AMOUNT_VALUE, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + CHANCE_LABEL, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + CHANCE_VALUE, false)
    }

    private const val COL_ALWAYS = "66ccff"
    private const val COL_GREEN = "00c000"
    private const val COL_YELLOW = "ffff00"
    private const val COL_ORANGE = "ff8200"
    private const val COL_RED = "ff0000"

    private fun wrapCol(
        hex: String,
        text: String,
    ) = "<col=$hex>$text</col>"

    private fun colourByDenom(
        text: String,
        denom: Int,
    ): String =
        when {
            denom <= 20 -> wrapCol(COL_GREEN, text)

            // common
            denom <= 64 -> wrapCol(COL_YELLOW, text)

            // uncommon
            denom <= 512 -> wrapCol(COL_ORANGE, text)

            // rare
            else -> wrapCol(COL_RED, text) // very rare
        }

    private fun buildRarityText(drop: DropDisplay): String {
        val rawBase = drop.rarityText
        val formattedBase =
            if (rawBase.startsWith("1/")) {
                val denom =
                    rawBase
                        .substringAfter("1/")
                        .replace(",", "") // safety
                        .toIntOrNull()

                if (denom != null) {
                    "1/${denom.formatChance()}"
                } else {
                    rawBase
                }
            } else {
                rawBase
            }
        val weight = drop.weight
        val total = drop.totalWeight
        val nothing = drop.nothingWeight

        if (weight == null || total == null || nothing == null) {
            return formattedBase
        }

        if (drop.type == DropType.NOTHING) {
            return "$formattedBase;Never"
        }

        val adjustedTotal = total - nothing
        val rowDenom = adjustedTotal / weight
        // here, formatBase string to add , isntead of 10000, it become 10,000
        return "$formattedBase;1/${rowDenom.formatChance()}"
    }

    private fun colourForDrop(
        drop: DropDisplay,
        rarity: String,
    ): String {
        return when (drop.type) {
            DropType.ALWAYS -> {
                wrapCol(COL_ALWAYS, rarity)
            }

            DropType.NOTHING -> {
                wrapCol(COL_GREEN, rarity)
            }

            DropType.MEGA_TABLE, DropType.RARE_TABLE -> {
                wrapCol(COL_ORANGE, rarity)
            }

            DropType.SUB_TABLE -> {
                val realDenom =
                    rarity
                        .substringAfter("1/")
                        .substringBefore(";")
                        .toIntOrNull() ?: drop.baseDenominator
                colourByDenom(rarity, realDenom)
            }

            DropType.CHARM -> {
                val percent = drop.percentage ?: return wrapCol(COL_YELLOW, rarity)

                when {
                    percent >= 5.0 -> {
                        wrapCol(COL_GREEN, rarity)
                    }

                    percent >= 1.5 -> {
                        wrapCol(COL_YELLOW, rarity)
                    }

                    percent >= 0.5 -> {
                        wrapCol(COL_ORANGE, rarity)
                    }

                    else -> {
                        wrapCol(COL_RED, rarity)
                    }
                }
            }

            DropType.MAIN,
            DropType.MINOR,
            DropType.SPECIAL,
            DropType.PREROLL,
            DropType.TERTIARY,
            -> {
                val realDenom = rarity.substringAfter("1/").substringBefore(";").toIntOrNull() ?: drop.baseDenominator
                colourByDenom(rarity, realDenom)
            }

            else -> {
                wrapCol(COL_YELLOW, rarity)
            }
        }
    }

    private fun wrap(
        name: String,
        max: Int = 18,
    ): String {
        if (name.length <= max) return name

        val words = name.split(" ")

        val line1 = StringBuilder()
        val line2 = StringBuilder()

        for (word in words) {
            val target =
                if (
                    line1.isEmpty() ||
                    line1.length + 1 + word.length <= max
                ) {
                    line1
                } else {
                    line2
                }

            if (target.isNotEmpty()) target.append(" ")
            target.append(word)
        }

        return if (line2.isEmpty()) {
            line1.toString()
        } else {
            "$line1<br>$line2"
        }
    }

    private fun Int.formatChance(): String = "%,d".format(this)
}
