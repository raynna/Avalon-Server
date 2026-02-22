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


    private fun dropRateMult(): Double =
        if (Settings.DROP_MULTIPLIER > 1.0) Settings.DROP_MULTIPLIER else 1.0

    private fun tableFor(source: DropTableSource): DropTable? =
        DropTableRegistry.getTableForSource(source)

    private fun sourceName(source: DropTableSource): String =
        when (source) {
            is DropTableSource.Npc ->
                NPCDefinitions.getNPCDefinitions(source.id).name
            is DropTableSource.Named ->
                source.key.replaceFirstChar { it.uppercase() }
            is DropTableSource.Item ->
                ItemDefinitions.getItemDefinitions(source.id).name
            is DropTableSource.Object ->
                DropTableRegistry.getObjectAlias(source.id)
                    ?: "Object ${source.id}"
        }

    fun open(player: Player, buildList: Boolean = true) {
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

            if (table.getAllDropsForDisplay(dropRateMult()).isEmpty())
                continue

            val name = sourceName(source).lowercase()

            if (seen.add(name))
                results += source
        }

        results.sortBy { sourceName(it) }

        player.temporaryAttributtes[ATTR_FOUND] = results
        player.temporaryAttributtes[ATTR_PAGE] = 0
    }

    fun openForSource(player: Player, source: DropTableSource) {

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


    fun openForItem(player: Player, itemId: Int) {

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
                    if (seen.add(key))
                        results += source

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



    fun openForObject(player: Player, objectId: Int) {

        val bound = DropTableRegistry.getSourceForObject(objectId)

        if (bound != null) {
            openForSource(player, bound)
            return
        }

        open(player, false)
        selectItem(player, objectId, true)
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


    fun handleButtons(player: Player, componentId: Int) {

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

                val page = player.temporaryAttributtes[ATTR_PAGE] as? Int ?: 0

                val hasFilter =
                    player.temporaryAttributtes.containsKey(ATTR_ITEM_FILTER) || player.temporaryAttributtes.containsKey(ATTR_SOURCE_FILTER)

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

            //player.temporaryAttributtes.remove(ATTR_ITEM_FILTER)

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

            val table = tableFor(source) ?: return

            val drop =
                table.getAllDropsForDisplay(dropRateMult())
                    .getOrNull(slot)
                    ?: return

            selectItem(player, drop.itemId, false)
        }
    }


    fun sendSourceList(player: Player) {

        for (i in NPC_LIST_START..NPC_LIST_END)
            player.packets.sendTextOnComponent(INTERFACE_ID, i, "")

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
                sourceName(list[index])
            )
        }

        updatePageButtons(player)
    }

    fun selectSource(player: Player, source: DropTableSource) {

        val table = tableFor(source) ?: return

        player.temporaryAttributtes[ATTR_CURRENT] = source
        player.temporaryAttributtes[ATTR_CURRENT_NAME] = sourceName(source)

        val itemFilter =
            player.temporaryAttributtes[ATTR_ITEM_FILTER] as? String

        val sourceFilter =
            player.temporaryAttributtes[ATTR_SOURCE_FILTER] as? String

        when {
            itemFilter != null ->
                updateTitle(
                    player,
                    "Viewing: ${sourceName(source)}",
                    "Filtered by item: $itemFilter"
                )

            sourceFilter != null ->
                updateTitle(
                    player,
                    "Viewing: ${sourceName(source)}",
                    "Filtered by: $sourceFilter"
                )

            else ->
                updateTitle(
                    player,
                    "Viewing: ${sourceName(source)}"
                )
        }


        renderTable(player, table)
    }

    private fun renderTable(player: Player, table: DropTable) {

        var row = ROW_START
        while (row <= ROW_END) {
            hideRow(player, row)
            row += ROW_STRIDE
        }

        val drops =
            table.getAllDropsForDisplay(dropRateMult())

        val maxRows = ((ROW_END - ROW_START) / ROW_STRIDE) + 1
        refreshScrollbar(player, drops.size)
        row = ROW_START

        for (drop in drops.take(maxRows)) {

            val def = ItemDefinitions.getItemDefinitions(drop.itemId)

            showRow(player, row)

            player.packets.sendItemOnIComponent(
                INTERFACE_ID,
                row + ICON_OFFSET,
                drop.itemId,
                (drop.amount ?: 1..1).last
            )

            player.packets.sendTextOnComponent(INTERFACE_ID, row + NAME_OFFSET, wrap(def.name))
            player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_LABEL, "Amount:")
            player.packets.sendTextOnComponent(INTERFACE_ID, row + CHANCE_LABEL, "Chance:")
            player.packets.sendTextOnComponent(INTERFACE_ID, row + AMOUNT_VALUE, (drop.amount ?: 1..1).toDisplayString())
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                row + CHANCE_VALUE,
                colourForDrop(drop)
            )

            row += ROW_STRIDE
        }
        //refreshScrollbar(player, drops.size)
    }

    fun selectItem(player: Player, itemId: Int, autoSelect: Boolean) {

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

                    if (seen.add(key))
                        results += source

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

        val list = player.temporaryAttributtes[ATTR_FOUND] as? List<*>
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

    private fun updatePreviousButtonText(player: Player, text: String) {
        player.packets.sendTextOnComponent(INTERFACE_ID, PREVIOUS_BUTTON, text)
    }

    private fun updatePageButtons(player: Player) {

        val list =
            player.temporaryAttributtes[ATTR_FOUND] as? List<*>

        val page =
            player.temporaryAttributtes[ATTR_PAGE] as? Int ?: 0

        val hasFilter =
            player.temporaryAttributtes.containsKey(ATTR_ITEM_FILTER) || player.temporaryAttributtes.containsKey(ATTR_SOURCE_FILTER)

        val inSearch =
            player.temporaryAttributtes.containsKey(ATTR_IN_SEARCH)

        val hasSelected =
            player.temporaryAttributtes.containsKey(ATTR_CURRENT)

        val canClear =
            page == 0 && (hasFilter || inSearch || hasSelected || list.isNullOrEmpty())

        player.packets.sendHideIComponent(
            INTERFACE_ID,
            PREVIOUS_BUTTON,
            page == 0 && !(hasFilter || inSearch || hasSelected)
        )

        updatePreviousButtonText(
            player,
            if (canClear) "Clear" else "Previous"
        )

        if (list.isNullOrEmpty()) {
            player.packets.sendHideIComponent(INTERFACE_ID, NEXT_BUTTON, true)
            return
        }

        val maxPage = (list.size + 12) / 13

        player.packets.sendHideIComponent(
            INTERFACE_ID,
            NEXT_BUTTON,
            page + 1 >= maxPage
        )
    }



    private fun refreshScrollbar(player: Player, rows: Int) {

        val contentHeight = rows * 35
        val needsScroll = contentHeight > 264

        player.packets.sendCSVarInteger(350,
            if (needsScroll) contentHeight else 225)

        //player.packets.sendRunScript(10006)
    }

    private fun updateTitle(player: Player, line1: String, line2: String? = null) {

        val text = if (line2 == null) line1 else "$line1<br>$line2"

        player.packets.sendTextOnComponent(INTERFACE_ID, TITLE_COMPONENT, text)
    }

    private fun hideRow(player: Player, row: Int) {
        player.packets.sendHideIComponent(INTERFACE_ID, row + ICON_OFFSET, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + NAME_OFFSET, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + AMOUNT_LABEL, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + AMOUNT_VALUE, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + CHANCE_LABEL, true)
        player.packets.sendHideIComponent(INTERFACE_ID, row + CHANCE_VALUE, true)
    }

    private fun showRow(player: Player, row: Int) {
        player.packets.sendHideIComponent(INTERFACE_ID, row + ICON_OFFSET, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + NAME_OFFSET, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + AMOUNT_LABEL, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + AMOUNT_VALUE, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + CHANCE_LABEL, false)
        player.packets.sendHideIComponent(INTERFACE_ID, row + CHANCE_VALUE, false)
    }

    private fun extractDenominator(rarity: String): Double? {
        if (!rarity.startsWith("1/")) return null
        return rarity.substringAfter("1/").toDoubleOrNull()
    }

    private const val COL_ALWAYS = "66ccff"
    private const val COL_GREEN  = "00c000"
    private const val COL_YELLOW = "ffff00"
    private const val COL_ORANGE = "ff8200"
    private const val COL_RED    = "ff0000"

    private fun wrapCol(hex: String, text: String) = "<col=$hex>$text</col>"

    private fun colourByDenom(text: String, denom: Int): String = when {
        denom <= 20  -> wrapCol(COL_GREEN, text)   // common
        denom <= 64  -> wrapCol(COL_YELLOW, text)  // uncommon
        denom <= 512 -> wrapCol(COL_ORANGE, text)  // rare
        else           -> wrapCol(COL_RED, text)     // very rare
    }

    private fun colourForDrop(drop: DropDisplay): String {
        val rarity = drop.rarityText

        return when (drop.type) {
            DropType.ALWAYS ->
                wrapCol(COL_ALWAYS, rarity)
            DropType.CHARM -> {

                val percent = drop.percentage ?: return wrapCol(COL_YELLOW, rarity)

                when {
                    percent >= 5.0 ->
                        wrapCol(COL_GREEN, rarity)

                    percent >= 1.5 ->
                        wrapCol(COL_YELLOW, rarity)

                    percent >= 0.5 ->
                        wrapCol(COL_ORANGE, rarity)

                    else ->
                        wrapCol(COL_RED, rarity)
                }
            }
            DropType.MAIN,
            DropType.MINOR,
            DropType.SPECIAL,
            DropType.PREROLL,
            DropType.TERTIARY ->
                colourByDenom(rarity, drop.baseDenominator)

            else ->
                wrapCol(COL_YELLOW, rarity)
        }
    }

    private fun wrap(name: String, max: Int = 18): String {

        if (name.length <= max) return name

        val words = name.split(" ")
        val l1 = StringBuilder()
        val l2 = StringBuilder()

        for (w in words) {
            if (l1.length + w.length + 1 <= max) {
                if (l1.isNotEmpty()) l1.append(" ")
                l1.append(w)
            } else {
                if (l2.isNotEmpty()) l2.append(" ")
                l2.append(w)
            }
        }

        return if (l2.isEmpty()) l1.toString() else "$l1<br>$l2"
    }
}