// app/tabs/TeleportTabDSL.kt
package com.rs.kotlin.game.player.customtab

import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.java.game.player.content.customtab.TeleportTab
import com.rs.java.game.player.controllers.WildernessController
import com.rs.java.game.player.teleportation.Teleports.TeleportLocations
import com.rs.kotlin.game.player.combat.magic.SpellHandler

object TeleportTabDSL {

    private const val KEY = "teleports"
    private const val IFACE = 3002
    private const val BACK = 58
    private const val FWD = 27
    private const val GREEN = 61
    private const val BLUE = 62

    // categories
    private const val CITY = 0
    private const val SKILLING = 1
    private const val MONSTERS = 2
    private const val DUNGEONS = 3
    private const val BOSSES = 4
    private const val MINIGAMES = 5
    private const val WILD = 6

    // skilling subcats (match your Java)
    private const val SK_MINING = 13
    private const val SK_SMITHING = 14
    private const val SK_FISHING = 15
    private const val SK_COOKING = 16
    private const val SK_WC = 17
    private const val SK_FARMING = 18
    private const val SK_AGILITY = 19
    private const val SK_THIEVING = 20
    private const val SK_RC = 21
    private const val SK_HUNTER = 22

    private val chrome = TabChromeIds(
        ifaceId = IFACE,
        firstSlot = 3, lastSlot = 22,
        backButton = BACK, forwardButton = FWD,
        blueStar = BLUE, greenStar = GREEN, redStar = 60, purpleStar = 59, yellowStar = 26,
        blueHighlighted = 12184, greenHighlighted = 12182, redHighlighted = 12186, purpleHighlighted = 12185, yellowHighlighted = 12187
    )

    fun open(player: Player) {
        player.temporaryAttributes().remove("ACHIEVEMENTTAB")
        player.temporaryAttributes().remove("DANGEROUSTELEPORT")
        player.temporaryAttributes().remove("TELEPORTTYPE")
        player.temporaryAttributes()["CUSTOMTAB"] = 1
        TabRuntime.open(player, buildMainPage())
    }

    fun handle(player: Player, compId: Int): Boolean {
        val danger = player.temporaryAttributes()["DANGEROUSTELEPORT"] as? WorldTile
        val type = player.temporaryAttribute()["TELEPORTTYPE"] as? Int

        val page = when {
            danger != null -> buildDangerPage(danger, type)
            type == null -> buildMainPage()
            type >= SK_MINING -> buildSkillingSubPage(type)
            else -> buildCategoryPage(type)
        }
        return TabRuntime.handleButton(player, page, compId)
    }

    // MAIN
    private fun buildMainPage(): TabPage = tabPage(KEY, chrome, "Teleports") {
        onBack = { p -> JournalTabDSL.open(p) }     // your code uses JournalTab on BACK
        onForward = { p -> SettingsTabDSL.open(p) } // your code uses SettingsTab on FWD

        onBeforeOpen { p ->
            val pk = p.packets
            pk.sendSpriteOnIComponent(IFACE, GREEN, chrome.greenHighlighted)
        }

        // Title
        label { "<col=f4ee42>Teleports" }

        // Category entries (3..9 match your constants)
        action({ "<col=f4ee42>City Teleports" }) { p -> openCategory(p, CITY) }
        action({ "<col=f4ee42>Skilling Teleports" }) { p -> openCategory(p, SKILLING) }
        action({ "<col=f4ee42>Monster Teleports" }) { p -> openCategory(p, MONSTERS) }
        action({ "<col=f4ee42>Dungeon/Slayer Teleports" }) { p -> openCategory(p, DUNGEONS) }
        action({ "<col=f4ee42>Boss Teleports" }) { p -> openCategory(p, BOSSES) }
        action({ "<col=f4ee42>Minigame Teleports" }) { p -> openCategory(p, MINIGAMES) }
        action({ "<col=f4ee42>Wilderness Teleports" }) { p -> openCategory(p, WILD) }

        spacer()

        // Previous teleport (component 11 in your Java)
        action({ "<u>Previous Teleport" }) { p ->
            val prev = p.temporaryAttributes()["PREVIOUSTELEPORT"] as? WorldTile
            if (prev != null) sendTeleport(p, prev, -1) else p.packets.sendGameMessage("You don't have any previous teleport location.")
        }
    }

    // CATEGORY LIST
    private fun buildCategoryPage(type: Int): TabPage = tabPage("$KEY:$type", chrome, mainCategoryName(type)) {
        onBack = { p ->
            if (type >= SK_MINING) openCategory(p, SKILLING) else open(p)
        }
        onForward = { p -> SettingsTabDSL.open(p) }

        onBeforeOpen { p ->
            p.temporaryAttributes().remove("DANGEROUSTELEPORT")
            p.temporaryAttributes()["TELEPORTTYPE"] = type
            p.packets.sendHideIComponent(IFACE, FWD, true)
            p.packets.sendSpriteOnIComponent(IFACE, GREEN, chrome.greenHighlighted)
        }

        // rows
        for (t in TeleportTab.TeleportTabData.entries) {
            if (t.category != type) continue

            // Header that expands to skilling subpages
            if (t.skilling) {
                action({ "<col=f4ee42>${t.text}" }) { p ->
                    openSkilling(p, t.componentId + 10) // same calculation as Java
                }
                continue
            }

            action({ rowText(t) }) { p ->
                val loc = TeleportLocations.getLocation(t.name)
                if (loc == null) {
                    p.message("This teleport is not handled yet.")
                    open(p); return@action
                }
                if (t.dangerous) {
                    sendDangerous(p, loc.location, type)
                } else {
                    sendTeleport(p, loc.location, type)
                }
            }
        }
    }

    // SKILLING SUBCATEGORY
    private fun buildSkillingSubPage(type: Int): TabPage = tabPage("$KEY:sk:$type", chrome, skillingCategoryName(type)) {
        onBack = { p -> openCategory(p, SKILLING) }
        onBeforeOpen { p ->
            p.temporaryAttributes()["TELEPORTTYPE"] = type
            p.packets.sendHideIComponent(IFACE, FWD, true)
            p.packets.sendSpriteOnIComponent(IFACE, GREEN, chrome.greenHighlighted)
        }

        for (t in TeleportTab.TeleportTabData.entries) {
            if (t.category != type) continue
            action({ rowText(t) }) { p ->
                val loc = TeleportLocations.getLocation(t.name)
                if (loc == null) {
                    p.message("This teleport is not handled yet.")
                    open(p); return@action
                }
                if (t.dangerous) sendDangerous(p, loc.location, type) else sendTeleport(p, loc.location, type)
            }
        }
    }

    // DANGEROUS CONFIRM
    private fun buildDangerPage(tile: WorldTile, type: Int?): TabPage = tabPage("$KEY:danger", chrome, "Dangerous!") {
        onBack = { p -> openCategory(p, type ?: CITY) }
        onBeforeOpen { p ->
            // Hide most things and show a compact dialog layout á la your Java
            val pk = p.packets
            sendComponents(p) // keep existing icons visible
            for (i in 3..22) pk.sendHideIComponent(IFACE, i, true)
            for (i in 28..56) pk.sendHideIComponent(IFACE, i, true)
            pk.sendHideIComponent(IFACE, 3, false)
            pk.sendHideIComponent(IFACE, 7, false)
            pk.sendHideIComponent(IFACE, 9, false)
            pk.sendHideIComponent(IFACE, BLUE, false)
            pk.sendTextOnComponent(IFACE, 25, "Dangerous!")
            pk.sendTextOnComponent(IFACE, 3, "<br><br>This is a <col=BB0404>dangerous</col> teleport, <br>Are you sure you want <br>to teleport?")
            pk.sendTextOnComponent(IFACE, 7, "<col=04BB3B>Yes</col>, I want to teleport.")
            pk.sendTextOnComponent(IFACE, 9, "<col=BB0404>No</col>, I don't want to teleport.")
            pk.sendSpriteOnIComponent(IFACE, BLUE, 439)
            // stash state
            p.temporaryAttributes()["DANGEROUSTELEPORT"] = tile
        }

        // Yes (component 7)
        handle(7) { p ->
            val t = p.temporaryAttributes()["DANGEROUSTELEPORT"] as? WorldTile ?: return@handle
            sendTeleport(p, t, type ?: -1)
        }
        // No (component 9) and back button → return to list
        handle(9) { p -> openCategory(p, type ?: CITY) }
    }

    // helpers to navigate between pages
    private fun openCategory(p: Player, type: Int) {
        TabRuntime.open(p, buildCategoryPage(type))
    }

    private fun openSkilling(p: Player, type: Int) {
        TabRuntime.open(p, buildSkillingSubPage(type))
    }

    // text helpers
    private fun rowText(t: TeleportTab.TeleportTabData): String {
        val sb = StringBuilder()
        if (t.order != null) sb.append(t.order).append("-")
        sb.append(if (t.dangerous) "<col=BB0404>" else "<col=f4ee42>").append(t.text)
        if (t.dangerous) sb.append(" - Dangerous!")
        if (t.levelReq > 0) sb.append(" - Level: ").append(t.levelReq)
        return sb.toString()
    }

    private fun mainCategoryName(type: Int) = when (type) {
        CITY -> "City Teleports"
        SKILLING -> "Skilling Teleports"
        MONSTERS -> "Monster Teleports"
        DUNGEONS -> "Dungeon Teleports"
        BOSSES -> "Boss Teleports"
        MINIGAMES -> "Minigame Teleports"
        else -> "Wilderness Teleports"
    }

    private fun skillingCategoryName(type: Int) = when (type) {
        SK_MINING -> "Mining"
        SK_SMITHING -> "Smithing"
        SK_FISHING -> "Fishing"
        SK_COOKING -> "Cooking"
        SK_WC -> "Woodcutting"
        SK_FARMING -> "Farming"
        SK_AGILITY -> "Agility"
        SK_THIEVING -> "Thieving"
        SK_HUNTER -> "Hunter"
        else -> "Runecrafting"
    }

    // behavior
    private fun sendComponents(p: Player) {
        // reuse your existing QuestTab/CustomTab sprite & visibility bootstrap if needed
        // Here you likely already have CustomTab.sendComponents(p) in Java. Call that
        // via a Kotlin wrapper or migrate that logic here if you prefer.
    }

    private fun sendDangerous(p: Player, tile: WorldTile, type: Int) {
        p.temporaryAttributes()["DANGEROUSTELEPORT"] = tile
        TabRuntime.open(p, buildDangerPage(tile, type))
    }

    private fun sendTeleport(p: Player, tile: WorldTile, type: Int) {
        if (WildernessController.getWildLevel(p) >= 20 && p.isAtWild) {
            p.packets.sendGameMessage("You can't use this teleport deeper than 20 wilderness.")
            openCategory(p, type)
            return
        }
        if (p.isInCombat) {
            p.packets.sendGameMessage("You can't use this teleport in combat.")
            return
        }
        p.temporaryAttributes().remove("PREVIOUSTELEPORT")
        SpellHandler.sendTeleportSpell(p, tile);
        open(p)
        p.temporaryAttributes()["PREVIOUSTELEPORT"] = tile
    }
}
