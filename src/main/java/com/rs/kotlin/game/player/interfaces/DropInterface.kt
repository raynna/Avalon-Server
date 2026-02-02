package com.rs.kotlin.game.player.interfaces

import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.core.cache.defintions.NPCDefinitions
import com.rs.java.game.player.Player
import com.rs.kotlin.game.npc.drops.DropTableRegistry.getDropTableForNpc
import java.util.Locale
import java.util.Locale.getDefault
import java.util.concurrent.CompletableFuture

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

    /**
     * Configuration
     */

    private const val INTERFACE_ID = 3005

    private const val ROW_START = 73
    private const val ROW_END = 641
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

    /**
     * Temporary Attribute Keys
     */

    private const val ATTR_FOUND_NPCS = "drop_viewer_found_npcs"
    private const val ATTR_NPC_PAGE = "drop_viewer_npc_page"
    private const val ATTR_ITEM_FILTER = "drop_viewer_item_filter"
    private const val ATTR_CURRENT_NPC = "current_drop_npc"
    private const val ATTR_CURRENT_NPC_NAME = "current_drop_npc_name"
    private const val ATTR_IN_SEARCH_MODE = "drop_viewer_in_search"


    /**
     * Utility
     */

    fun getGlobalDropRateMultiplier(): Double {
        var mult = 1.0
        if (Settings.DROP_MULTIPLIER > 1.0)
            mult *= Settings.DROP_MULTIPLIER
        return mult
    }

    fun getNpcDrops(npcId: Int): Array<DropDisplay?> {
        val table = getDropTableForNpc(npcId) ?: return emptyArray()
        return table
            .getAllDropsForDisplay(getGlobalDropRateMultiplier())
            .toTypedArray()
    }

    /**
     * Open Interface
     */

    fun open(player: Player) {
        CompletableFuture.runAsync {
            player.interfaceManager.sendInterface(INTERFACE_ID)

            // Hide all rows
            var row = ROW_START
            while (row <= ROW_END) {
                hideRow(player, row)
                row += ROW_STRIDE
            }

            player.packets.sendHideIComponent(INTERFACE_ID, PREVIOUS_BUTTON, true)
            player.packets.sendHideIComponent(INTERFACE_ID, NEXT_BUTTON, true)
            refreshScrollbar(player, 0)
            buildInitialNpcList(player)
            sendNpcList(player)
        }
    }

    /**
     * Builds initial NPC list containing every NPC that has drops,
     * removing duplicate names and sorting alphabetically.
     */
    private fun buildInitialNpcList(player: Player) {

        val results = mutableListOf<Int>()
        val seenNames = HashSet<String>()

        for ((npcId, def) in NPCDefinitions.getNpcDefinitions()) {

            val table = getDropTableForNpc(npcId) ?: continue

            val drops =
                table.getAllDropsForDisplay(getGlobalDropRateMultiplier())

            if (drops.isEmpty())
                continue

            val name = def.name.lowercase(getDefault())

            // Prevent duplicates of same NPC name
            if (!seenNames.add(name))
                continue

            results.add(npcId)
        }

        // Sort alphabetically by NPC name
        results.sortBy { NPCDefinitions.getNPCDefinitions(it).name }

        player.temporaryAttributtes[ATTR_FOUND_NPCS] = results
        player.temporaryAttributtes[ATTR_NPC_PAGE] = 0
    }



    /**
     * NPC Selection
     */

    fun selectNpc(player: Player, npcId: Int) {
        val rows = getNpcDrops(npcId).size
        refreshScrollbar(player, rows)
        val npcName = NPCDefinitions.getNPCDefinitions(npcId).name
        player.temporaryAttributtes[ATTR_CURRENT_NPC] = npcId
        player.temporaryAttributtes[ATTR_CURRENT_NPC_NAME] = npcName

        val filter = player.temporaryAttributtes[ATTR_ITEM_FILTER] as? String

        if (filter != null)
            updateTitle(player, "Viewing: $npcName", "Filtered by item: $filter")
        else
            updateTitle(player, "Viewing: $npcName")

        var clearRow = ROW_START
        while (clearRow <= ROW_END) {
            hideRow(player, clearRow)
            clearRow += ROW_STRIDE
        }

        val maxRows = ((ROW_END - ROW_START) / ROW_STRIDE) + 1
        val drops = getNpcDrops(npcId).take(maxRows)

        var row = ROW_START

        for (drop in drops) {
            if (drop == null) continue
            if (row > ROW_END) break

            val def = ItemDefinitions.getItemDefinitions(drop.itemId)

            showRow(player, row)

            player.packets.sendItemOnIComponent(
                INTERFACE_ID,
                row + ICON_OFFSET,
                drop.itemId,
                (drop.amount ?: 1..1).last
            )

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                row + NAME_OFFSET,
                wrapItemName(def.name)
            )

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                row + AMOUNT_LABEL,
                "Amount:"
            )

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                row + CHANCE_LABEL,
                "Chance:"
            )

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                row + AMOUNT_VALUE,
                (drop.amount ?: 1..1).toDisplayString()
            )

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                row + CHANCE_VALUE,
                drop.rarity
            )

            row += ROW_STRIDE
        }
        refreshScrollbar(player, rows)
        updatePageButtons(player);
    }

    private fun refreshScrollbar(player: Player, rows: Int) {

        val rowHeight = 35
        val viewportHeight = 264

        val contentHeight = rows * rowHeight
        val needsScroll = contentHeight > viewportHeight

        player.packets.sendHideIComponent(3005, 649, !needsScroll)

        player.packets.sendCSVarInteger(
            350,
            if (needsScroll) contentHeight else viewportHeight
        )

        player.packets.sendRunScript(10006)
    }


    /**
     * NPC List (Left Panel)
     */

    fun sendNpcList(player: Player) {

        for (i in NPC_LIST_START..NPC_LIST_END)
            player.packets.sendTextOnComponent(INTERFACE_ID, i, "")

        val list =
            player.temporaryAttributtes[ATTR_FOUND_NPCS] as? MutableList<Int>

        if (list.isNullOrEmpty()) {
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                NPC_LIST_START,
                "No results found."
            )
            updateTitle(player, "No results found.")
            updatePageButtons(player)
            return
        }

        val page =
            (player.temporaryAttributtes[ATTR_NPC_PAGE] as? Int) ?: 0

        for (i in 0 until 13) {
            val index = i + page * 13
            if (index >= list.size) break

            val name =
                NPCDefinitions.getNPCDefinitions(list[index]).name

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                NPC_LIST_START + i,
                name
            )
        }

        updatePageButtons(player)
    }

    /**
     * Button Handling
     */

    fun handleButtons(player: Player, componentId: Int) {

        when (componentId) {

            // Find Item
            23 -> {
                player.packets.sendInputNameScript("Enter item name")
                player.temporaryAttributtes["drop_find"] = true
            }

            // Find NPC
            27 -> {
                player.packets.sendInputNameScript("Enter NPC name")
                player.temporaryAttributtes["npc_find"] = true
                player.temporaryAttributtes.remove(ATTR_ITEM_FILTER)
            }

            // Next Page
            51 -> nextPage(player)

            // Previous / Clear
            50 -> {

                val page =
                    (player.temporaryAttributtes[ATTR_NPC_PAGE] as? Int) ?: 0

                val hasItemFilter =
                    player.temporaryAttributtes.containsKey(ATTR_ITEM_FILTER)

                val inSearchMode =
                    player.temporaryAttributtes.containsKey(ATTR_IN_SEARCH_MODE)

                val hasNpcSelected =
                    player.temporaryAttributtes.containsKey(ATTR_CURRENT_NPC)

                if (page <= 0 && (hasItemFilter || inSearchMode || hasNpcSelected)) {
                    clearViewer(player)
                } else {
                    previousPage(player)
                }

            }
        }

        // Clicking NPC name
        // Clicking NPC name
        if (componentId in NPC_LIST_START..NPC_LIST_END) {

            val list =
                player.temporaryAttributtes[ATTR_FOUND_NPCS] as? MutableList<Int>
                    ?: return

            val page =
                (player.temporaryAttributtes[ATTR_NPC_PAGE] as? Int) ?: 0

            val index = componentId - NPC_LIST_START + page * 13
            val npcId = list.getOrNull(index) ?: return

            player.temporaryAttributtes.remove(ATTR_ITEM_FILTER)
            selectNpc(player, npcId)
        }


        // Clicking item icon
        if (componentId in ROW_START..ROW_END &&
            (componentId - ROW_START) % ROW_STRIDE == 0
        ) {

            val slot = (componentId - ROW_START) / ROW_STRIDE

            val npcId =
                player.temporaryAttributtes[ATTR_CURRENT_NPC] as? Int ?: return

            val drop = getNpcDrops(npcId).getOrNull(slot) ?: return

            searchByItem(player, drop.itemId, false)
        }
    }


    /**
     * Searching
     */

    private fun searchByItem(player: Player, itemId: Int, autoSelect: Boolean) {

        val itemName =
            ItemDefinitions.getItemDefinitions(itemId).name

        val npcName =
            player.temporaryAttributtes[ATTR_CURRENT_NPC_NAME] as? String
                ?: "NPCs"

        updateTitle(player, "Viewing: $npcName", "Filtered by item: $itemName")

        player.temporaryAttributtes[ATTR_ITEM_FILTER] = itemName
        player.temporaryAttributtes[ATTR_IN_SEARCH_MODE] = true

        val results = mutableListOf<Int>()
        val seen = HashSet<String>()

        for ((npcId, def) in NPCDefinitions.getNpcDefinitions()) {

            val table = getDropTableForNpc(npcId) ?: continue
            val drops = table.getAllDropsForDisplay(getGlobalDropRateMultiplier())

            for (drop in drops) {
                if (drop.itemId == itemId) {
                    if (seen.add(def.name.lowercase(getDefault())))
                        results.add(npcId)
                    break
                }
            }
        }

        player.temporaryAttributtes[ATTR_FOUND_NPCS] = results
        player.temporaryAttributtes[ATTR_NPC_PAGE] = 0

        sendNpcList(player)

        if (autoSelect && results.isNotEmpty())
            selectNpc(player, results[0])
    }


    /**
     * Pagination
     */

    private fun nextPage(player: Player) {
        val list =
            player.temporaryAttributtes[ATTR_FOUND_NPCS] as? MutableList<Int>
                ?: return

        val page =
            (player.temporaryAttributtes[ATTR_NPC_PAGE] as? Int) ?: 0

        val maxPage = (list.size + 12) / 13

        if (page + 1 >= maxPage) return

        player.temporaryAttributtes[ATTR_NPC_PAGE] = page + 1
        sendNpcList(player)
    }

    private fun previousPage(player: Player) {

        val page =
            (player.temporaryAttributtes[ATTR_NPC_PAGE] as? Int) ?: 0

        if (page <= 0) return

        player.temporaryAttributtes[ATTR_NPC_PAGE] = page - 1
        sendNpcList(player)
    }

    private fun updatePageButtons(player: Player) {

        val list =
            player.temporaryAttributtes[ATTR_FOUND_NPCS] as? MutableList<Int>

        if (list.isNullOrEmpty()) {
            player.packets.sendHideIComponent(INTERFACE_ID, PREVIOUS_BUTTON, true)
            player.packets.sendHideIComponent(INTERFACE_ID, NEXT_BUTTON, true)
            return
        }

        val page =
            (player.temporaryAttributtes[ATTR_NPC_PAGE] as? Int) ?: 0

        val maxPage = (list.size + 12) / 13

        val hasItemFilter =
            player.temporaryAttributtes.containsKey(ATTR_ITEM_FILTER)

        val inSearchMode =
            player.temporaryAttributtes.containsKey(ATTR_IN_SEARCH_MODE)

        val hasNpcSelected =
            player.temporaryAttributtes.containsKey(ATTR_CURRENT_NPC)

        val canClear =
            page <= 0 && (hasItemFilter || inSearchMode || hasNpcSelected)

        player.packets.sendHideIComponent(INTERFACE_ID, PREVIOUS_BUTTON, false)

        player.packets.sendHideIComponent(
            INTERFACE_ID,
            NEXT_BUTTON,
            page + 1 >= maxPage
        )

        if (canClear) {
            setPreviousButtonText(player, "Clear")
        } else {
            setPreviousButtonText(player, "Previous")
        }
    }


    private fun clearViewer(player: Player) {

        player.temporaryAttributtes.remove(ATTR_ITEM_FILTER)
        player.temporaryAttributtes.remove(ATTR_IN_SEARCH_MODE)
        player.temporaryAttributtes.remove(ATTR_CURRENT_NPC)
        player.temporaryAttributtes.remove(ATTR_CURRENT_NPC_NAME)

        // Hide item rows
        var row = ROW_START
        while (row <= ROW_END) {
            hideRow(player, row)
            row += ROW_STRIDE
        }

        buildInitialNpcList(player)

        player.temporaryAttributtes[ATTR_NPC_PAGE] = 0

        sendNpcList(player)
        updateTitle(player, "Drop Viewer")
        refreshScrollbar(player, 0)
    }





    /**
     * Title
     */

    private fun updateTitle(player: Player, line1: String, line2: String? = null) {

        val text =
            if (line2 == null) line1
            else "$line1<br>$line2"

        player.packets.sendTextOnComponent(
            INTERFACE_ID,
            TITLE_COMPONENT,
            text
        )
    }

    private fun setPreviousButtonText(player: Player, text: String) {
        player.packets.sendTextOnComponent(
            INTERFACE_ID,
            PREVIOUS_BUTTON,
            text
        )
    }

    /**
     * Row Helpers
    */

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

    private fun wrapItemName(name: String, maxLineLength: Int = 18): String {
        if (name.length <= maxLineLength)
            return name

        val words = name.split(" ")
        val line1 = StringBuilder()
        val line2 = StringBuilder()

        for (word in words) {
            if (line1.length + word.length + 1 <= maxLineLength) {
                if (line1.isNotEmpty()) line1.append(" ")
                line1.append(word)
            } else {
                if (line2.isNotEmpty()) line2.append(" ")
                line2.append(word)
            }
        }

        return if (line2.isEmpty())
            line1.toString()
        else
            "${line1}<br>${line2}"
    }

}
