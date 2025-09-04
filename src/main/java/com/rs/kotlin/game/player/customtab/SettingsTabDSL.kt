// app/tabs/SettingsTab.kt
package com.rs.kotlin.game.player.customtab

import com.rs.java.game.player.Player
import com.rs.java.game.player.controlers.EdgevillePvPControler
import com.rs.java.game.player.controlers.WildernessControler
import com.rs.java.utils.HexColours
import com.rs.java.utils.HexColours.Colour
import com.rs.java.utils.Utils

object SettingsTabDSL {

    private val chrome = TabChromeIds(
        ifaceId = 3002,
        firstSlot = 3, lastSlot = 22,
        backButton = 58, forwardButton = 27,
        blueStar = 62, greenStar = 61, redStar = 60, purpleStar = 59, yellowStar = 26,
        blueHighlighted = 12184, greenHighlighted = 12182, redHighlighted = 12186, purpleHighlighted = 12185, yellowHighlighted = 12187
    )

    val page: TabPage = tabPage(
        key = "settings",
        chrome = chrome,
        title = "Toggles"
    ) {
        // === "Settings" section ===
        section("Settings")

        action(
            text = { p -> "Break Vials: " + onOff(p.toggles("BREAK_VIALS", false)) }
        ) { p ->
            p.toggles.put("BREAK_VIALS", !p.toggles("BREAK_VIALS", false))
        }

        action(
            text = { p -> "Dont Drop Free Items: " + onOff(p.toggles("IGNORE_LOW_VALUE", false)) }
        ) { p ->
            p.toggles.put("IGNORE_LOW_VALUE", !p.toggles("IGNORE_LOW_VALUE", false))
        }

        action(
            text = { p -> "Item Visuals: " + if (p.isOldItemsLook) green("2011") else green("2012") }
        ) { p -> p.switchItemsLook() }

        action(
            text = { p -> "Shift Dropping: " + onOff(p.isShiftDrop) }
        ) { p -> p.switchShiftDrop() }

        action(
            text = { p -> "Slow Drag: " + onOff(p.isSlowDrag) }
        ) { p ->
            p.switchSlowDrag()
            p.message("You have to relog before noticing changes.")
        }

        action(
            text = { p -> "Zoom: " + onOff(p.isZoom) }
        ) { p -> p.switchZoom() }

        action(
            text = { p -> "Health Overlay: " + onOff(p.toggles("HEALTH_OVERLAY", false)) }
        ) { p ->
            p.toggles.put("HEALTH_OVERLAY", !p.toggles("HEALTH_OVERLAY", false))
            p.temporaryAttribute().remove("overlay_state")
        }

        action(
            text = { p -> "Hitchance Overlay: " + onOff(p.toggles("HITCHANCE_OVERLAY", false)) }
        ) { p ->
            p.toggles.put("HITCHANCE_OVERLAY", !p.toggles("HITCHANCE_OVERLAY", false))
            p.temporaryAttribute().remove("overlay_state")
        }

        action(
            text = { p -> "Affected stats Overlay: " + onOff(p.toggles("LEVELSTATUS_OVERLAY", false)) }
        ) { p ->
            p.toggles.put("LEVELSTATUS_OVERLAY", !p.toggles("LEVELSTATUS_OVERLAY", false))
            p.temporaryAttribute().remove("overlay_state")
        }

        action(
            text = { p -> "KDR Overlay: " + onOff(p.toggles("KDRINTER", false)) }
        ) { p ->
            p.toggles.put("KDRINTER", !p.toggles("KDRINTER", false))
            val enabled = p.toggles("KDRINTER", false)
            val openNow = WildernessControler.isAtWild(p)
                || EdgevillePvPControler.isAtBank(p)
                || EdgevillePvPControler.isAtPvP(p)
            if (!enabled && p.interfaceManager.containsTab(10)) {
                p.interfaceManager.closeTab(p.interfaceManager.isResizableScreen, 10)
            } else if (enabled && openNow) {
                WildernessControler.showKDRInter(p)
            }
        }

        // === "Combat Settings" ===
        section("Combat Settings")

        action(
            text = { p -> "One XP per Hit: " + onOff(p.toggles("ONEXPPERHIT", false)) }
        ) { p ->
            p.toggles.put("ONEXPPERHIT", !p.toggles("ONEXPPERHIT", false))
            p.skills.switchXPPopup(true)
            p.skills.switchXPPopup(true)
        }

        action(
            text = { p ->
                val active = p.varsManager.getBitValue(1485) == 1
                "1x Hitpoints & prayer: " + onOff(active)
            }
        ) { p ->
            val active = p.varsManager.getBitValue(1485) == 1
            p.varsManager.sendVarBit(1485, if (active) 0 else 1, true)
            p.varsManager.forceSendVarBit(9816, p.prayer.prayerPoints)
            p.refreshHitPoints()
            p.skills.switchXPPopup(true)
            p.skills.switchXPPopup(true)
        }

        // === "Drop Settings" ===
        section("Drop Settings")

        action(
            text = { p -> "Lootbeams: " + onOff(p.toggles("LOOTBEAMS", false)) }
        ) { p ->
            p.toggles.put("LOOTBEAMS", !p.toggles("LOOTBEAMS", false))
        }

        action(
            text = { p -> "Untradeable Message: " + onOff(p.toggles("UNTRADEABLEMESSAGE", false)) }
        ) { p ->
            p.toggles.put("UNTRADEABLEMESSAGE", !p.toggles("UNTRADEABLEMESSAGE", false))
        }

        action(
            text = { p ->
                val dropStr = p.toggles["DROPVALUE"]?.let { p.getToggleValue(it) } ?: "0"
                val drop = dropStr.toIntOrNull() ?: 0
                val msg = if (drop < 1) HexColours.getMessage(Colour.RED, "0 - click to set")
                else HexColours.getMessage(Colour.GREEN, "${Utils.getFormattedNumber(drop.toDouble(), ',')} gp")
                "Valuable Drop: $msg"
            }
        ) { p ->
            p.temporaryAttributes()["SET_DROPVALUE"] = true
            p.packets.sendRunScript(108, arrayOf("Enter Amount:"))
        }

        // === "Developer Settings" ===
        section("Developer Settings")

        action(
            text = { p -> "Developer Mode: " + onOff(p.isDeveloperMode) }
        ) { p -> p.switchDeveloperMode() }

        // Navigation targets
        onBack = { p -> TeleportTabDSL.open(p) }
        onForward = { p -> GearTabDSL.open(p) }
    }

    fun register() = TabRegistry.register(page)

    fun open(player: Player) = TabRuntime.open(player, page)
    fun handle(player: Player, compId: Int) = TabRuntime.handleButton(player, page, compId)

    private fun onOff(b: Boolean) = if (b) green("On") else red("Off")
    private fun green(s: String) = "<col=04BB3B>$s"
    private fun red(s: String) = "<col=BB0404>$s"
}
