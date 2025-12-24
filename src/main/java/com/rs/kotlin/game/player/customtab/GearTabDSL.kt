// app/tabs/GearTabDSL.kt
package com.rs.kotlin.game.player.customtab

import com.rs.java.game.World
import com.rs.kotlin.game.player.AccountCreation
import com.rs.java.game.player.Player
import com.rs.java.game.player.content.presets.Preset
import com.rs.java.utils.Utils

object GearTabDSL {

    private const val KEY = "gear"
    private const val IFACE = 3002

    // reuse constants from your Java:
    private const val BLUE = 62
    private const val GREEN = 61
    private const val RED = 60
    private const val PURPLE = 59
    private const val YELLOW = 26
    private const val BACK = 58
    private const val FWD = 27

    private val chrome = TabChromeIds(
        ifaceId = IFACE,
        firstSlot = 3, lastSlot = 22,
        backButton = BACK, forwardButton = FWD,
        blueStar = BLUE, greenStar = GREEN, redStar = RED, purpleStar = PURPLE, yellowStar = YELLOW,
        blueHighlighted = 12184, greenHighlighted = 12182, redHighlighted = 12186, purpleHighlighted = 12185, yellowHighlighted = 12187
    )

    private fun findOther(name: String?): Player? {
        if (name == null) return null
        val disp = Utils.formatPlayerNameForDisplay(name)
        return World.getPlayerByDisplayName(disp) ?: AccountCreation.loadPlayer(disp)
    }

    fun open(player: Player, otherName: String? = null) {
        player.temporaryAttributes()["CUSTOMTAB"] = 3
        player.temporaryAttributes().remove("ACHIEVEMENTTAB")
        player.temporaryAttributes().remove("SELECTEDGEAR")
        if (otherName == null) player.temporaryAttributes().remove("OTHERPRESET_NAME")
        doOpen(player, otherName)
    }

    fun handle(player: Player, compId: Int, otherName: String? = null): Boolean =
        TabRuntime.handleButton(player, buildPage(player, otherName), compId)

    private fun doOpen(player: Player, otherName: String?) {
        TabRuntime.open(player, buildPage(player, otherName))
    }

    private fun buildPage(player: Player, otherName: String?): TabPage {
        val other = findOther(otherName)
        val viewingOther = other != null
        val pageTitle = if (viewingOther) "${other!!.displayName}<br> Presets" else "Gear Setups"

        val page = tabPage(KEY, chrome, pageTitle) {
            onBack = { p -> SettingsTabDSL.open(p) }
            onForward = { _ -> /* hidden for this page */ }

            // per-open chrome tune-up
            onBeforeOpen { p ->
                val pk = p.packets
                // Base visibility
                pk.sendHideIComponent(IFACE, BLUE, false)
                pk.sendHideIComponent(IFACE, GREEN, !viewingOther) // search or rename
                pk.sendHideIComponent(IFACE, RED, true)            // shown only when a row is selected
                pk.sendHideIComponent(IFACE, PURPLE, !viewingOther) // save/overwrite only on own presets
                pk.sendHideIComponent(IFACE, YELLOW, !viewingOther) // delete only on own presets
                // set sprites according to state
                pk.sendIComponentSprite(IFACE, BLUE, "sprite.leave_door")
                pk.sendIComponentSprite(IFACE, GREEN, "sprite.search") // will switch to rename when selection exists
                pk.sendIComponentSprite(IFACE, RED, "sprite.add_to_bag")
                pk.sendIComponentSprite(IFACE, PURPLE, "sprite.add_note")
                pk.sendIComponentSprite(IFACE, YELLOW, "sprite.remove_note")

                // selection-dependent chrome
                val selectedId = p.temporaryAttributes()["SELECTEDGEAR"] as? Int
                val pkVisSel = selectedId != null
                // show load + delete only when selected (own page)
                pk.sendHideIComponent(IFACE, RED, !pkVisSel)                 // load
                pk.sendHideIComponent(IFACE, YELLOW, !pkVisSel || viewingOther) // delete only if own & selected

                // When selected, green star becomes RENAME icon
                if (!viewingOther) {
                    pk.sendIComponentSprite(IFACE, GREEN, if (pkVisSel) "sprite.edit_note" else "sprite.search")
                } else {
                    pk.sendHideIComponent(IFACE, GREEN, true)
                }

                // reset confirm icons unless currently confirming
                if (p.temporaryAttributes()["CONFIRM_OVERWRITE"] != true) {
                    pk.sendIComponentSprite(IFACE, PURPLE, "sprite.add_note")
                }
                if (p.temporaryAttributes()["CONFIRM_DELETE"] != true) {
                    pk.sendIComponentSprite(IFACE, YELLOW, "sprite.remove_note")
                }
            }

            // Rows (own or other)
            val entries: Iterable<Map.Entry<String, Preset>> =
                (other ?: player).presetManager.PRESET_SETUPS.entries

            for ((name, preset) in entries) {
                action(
                    text = { p ->
                        val id = preset.getId(other ?: player)
                        val sel = p.temporaryAttributes()["SELECTEDGEAR"] as? Int
                        if (sel != null && sel == id) "$name<img=12>" else name
                    }
                ) { p ->
                    val id = preset.getId(other ?: player)
                    val sel = p.temporaryAttributes()["SELECTEDGEAR"] as? Int
                    // toggle selection
                    if (sel != null && sel == id) {
                        p.temporaryAttributes().remove("SELECTEDGEAR")
                        p.packets.sendIComponentSprite(IFACE, GREEN, "sprite.search")
                        p.packets.sendHideIComponent(IFACE, RED, true)
                        p.packets.sendHideIComponent(IFACE, YELLOW, true)
                        p.packets.sendIComponentSprite(IFACE, PURPLE, "sprite.add_note")
                        removeTransientFlags(p)
                    } else {
                        p.temporaryAttributes()["SELECTEDGEAR"] = id
                        if (!viewingOther) {
                            p.packets.sendIComponentSprite(IFACE, GREEN, 1832) // rename icon
                            p.packets.sendHideIComponent(IFACE, RED, false)
                            p.packets.sendHideIComponent(IFACE, YELLOW, false)
                            p.packets.sendIComponentSprite(IFACE, PURPLE, "sprite.out_of_bag")
                            p.packets.sendIComponentSprite(IFACE, YELLOW, "sprite.remove_note")
                        }
                        removeTransientFlags(p)
                    }
                }
            }

            // Star buttons:

            // Blue (62): back/home (exit “other” view if active, else go settings)
            handle(BLUE) { p ->
                if (viewingOther) {
                    open(p, null)
                } else {
                    SettingsTabDSL.open(p)
                }
            }

            // Green (61): rename selected (own) OR search other presets (no selection)
            handle(GREEN) { p ->
                val sel = p.temporaryAttributes()["SELECTEDGEAR"] as? Int
                if (sel != null) {
                    // rename
                    val source = (other ?: player).presetManager.PRESET_SETUPS.entries
                    val exists = source.firstOrNull { it.value.getId(other ?: player) == sel }
                    if (exists != null) {
                        p.temporaryAttributes()["RENAME_SETUP"] = true
                        p.temporaryAttributes()["SELECTED_RENAME"] = sel
                        p.packets.sendRunScript(109, "Enter new setup name: ")
                    }
                } else {
                    // search others
                    p.temporaryAttribute().remove("SAVESETUP")
                    p.temporaryAttribute()["OTHERPRESET"] = true
                    p.packets.sendRunScript(109, "Search for other players presets: ")
                }
            }

            // Red (60): load selected
            handle(RED) { p ->
                val sel = p.temporaryAttributes()["SELECTEDGEAR"] as? Int
                if (sel == null) { p.packets.sendGameMessage("You don't have any gear setup selected."); return@handle }
                val srcOwner = (other ?: player)
                val entry = srcOwner.presetManager.PRESET_SETUPS.entries
                    .firstOrNull { it.value.getId(srcOwner) == sel } ?: return@handle
                player.presetManager.loadPreset(entry.key, if (other != null) other else null)
                if (other != null) open(p, null) else open(p, null) // refresh own list view
            }

            // Purple (59): save/overwrite (own only)
            handle(PURPLE) { p ->
                if (viewingOther) return@handle
                val sel = p.temporaryAttributes()["SELECTEDGEAR"] as? Int
                if (sel != null) {
                    val confirm = p.temporaryAttributes()["CONFIRM_OVERWRITE"] as? Boolean ?: false
                    if (confirm) {
                        var keyToOverwrite: String? = null
                        for ((k, v) in p.presetManager.PRESET_SETUPS) if (v.getId(p) == sel) { keyToOverwrite = k; break }
                        if (keyToOverwrite != null) {
                            p.presetManager.removePreset(keyToOverwrite)
                            p.presetManager.savePreset(keyToOverwrite)
                            p.packets.sendGameMessage("Preset \"$keyToOverwrite\" has been overwritten.")
                            open(p, null)
                            p.temporaryAttributes().remove("SELECTEDGEAR")
                            p.temporaryAttributes().remove("CONFIRM_OVERWRITE")
                        } else {
                            p.packets.sendGameMessage("Could not find the preset to overwrite.")
                            p.temporaryAttributes().remove("CONFIRM_OVERWRITE")
                        }
                    } else {
                        p.packets.sendGameMessage("Are you sure you want to overwrite this preset? Click <img=14> to confirm.")
                        p.packets.sendIComponentSprite(IFACE, PURPLE, "sprite.green_checkmark_2")
                        p.temporaryAttributes()["CONFIRM_OVERWRITE"] = true
                    }
                } else {
                    p.temporaryAttribute().remove("OTHERPRESET")
                    p.temporaryAttribute()["SAVESETUP"] = true
                    p.packets.sendRunScript(109, "Enter setup name: ")
                }
            }

            // Yellow (26): delete (own only)
            handle(YELLOW) { p ->
                if (viewingOther) return@handle
                val sel = p.temporaryAttributes()["SELECTEDGEAR"] as? Int
                if (sel == null) { p.packets.sendGameMessage("You don't have any gear setup selected."); return@handle }
                val confirm = p.temporaryAttributes()["CONFIRM_DELETE"] as? Boolean ?: false
                if (confirm) {
                    val entry = p.presetManager.PRESET_SETUPS.entries.firstOrNull { it.value.getId(p) == sel }
                    if (entry != null) {
                        p.presetManager.removePreset(entry.key)
                        p.packets.sendGameMessage("Preset \"${entry.key}\" has been deleted.")
                        open(p, null)
                        p.temporaryAttributes().remove("SELECTEDGEAR")
                        p.temporaryAttributes().remove("CONFIRM_DELETE")
                    } else {
                        p.packets.sendGameMessage("Could not find the preset to delete.")
                        p.temporaryAttributes().remove("CONFIRM_DELETE")
                    }
                } else {
                    p.packets.sendGameMessage("Are you sure you want to delete this preset? Click <img=14> to confirm.")
                    p.packets.sendIComponentSprite(IFACE, YELLOW, "sprite.green_checkmark_2")
                    p.temporaryAttributes()["CONFIRM_DELETE"] = true
                }
            }
        }
        return page
    }

    private fun removeTransientFlags(p: Player) {
        p.temporaryAttributes().remove("CONFIRM_OVERWRITE")
        p.temporaryAttributes().remove("CONFIRM_DELETE")
        p.temporaryAttributes().remove("RENAME_SETUP")
    }
}
