package com.rs.kotlin.game.player.interfaces

import com.rs.java.game.item.Item
import com.rs.java.game.item.ItemsContainer
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.actions.skills.summoning.Summoning
import com.rs.java.game.player.content.presets.Preset
import com.rs.java.utils.Utils
import com.rs.kotlin.game.world.util.Msg.warn

object PresetInterface {

    const val INTERFACE_ID = 3053

    private const val VIEWING_TITLE = 299

    private const val PRESET_LIST_START = 230
    private const val PRESET_LIST_END = 290
    private const val PRESET_LIST_STRIDE = 3
    private const val CURRENT_SETUP_COMPONENT = 28

    private const val INVENTORY_CONTAINER = 203
    private const val INVENTORY_KEY = 100

    private const val MAX_VISIBLE_PRESETS = 20

    private const val ATTR_SELECTED = "preset_selected_index"
    private const val ATTR_CONFIRM_DELETE = "preset_confirm_delete"
    private const val ATTR_CONFIRM_OVERWRITE = "preset_confirm_overwrite"
    private const val ATTR_RENAME_PRESET = "preset_rename_target"
    private const val ATTR_OPENED_FROM_BANK = "preset_opened_from_bank"

    private const val FIRE_SURGE_SPRITE = 814
    private const val BARRAGE_SPRITE = 564
    private const val VENGEANCE_SPRITE = 328
    private const val PIETY_SPRITE = 946
    private const val TURMOIL_SPRITE = 2321

    private const val SAVE_BUTTON = 143
    private const val DELETE_PARENT = 144
    private const val DELETE_BUTTON = 146
    private const val GEARUP_PARENT = 147
    private const val GEARUP_BUTTON = 149
    private const val OVERWRITE_PARENT = 219
    private const val OVERWRITE_BUTTON = 221

    private const val SECURITY_POPUP = 292
    private const val SECURITY_TEXT = 294
    private const val SECURITY_YES = 295
    private const val SECURITY_NO = 297

    private const val BACK_TO_BANK = 208

    private const val PRAYERBOOK_SPRITE = 42
    private const val PRAYERBOOK_BUTTON = 31
    private const val PRAYERBOOK_TEXT = 32

    private const val SPELLBOOK_SPRITE = 43
    private const val SPELLBOOK_BUTTON = 35
    private const val SPELLBOOK_TEXT = 36

    private const val FAMILIAR_SPRITE = 226
    private const val FAMILIAR_BUTTON = 224
    private const val FAMILIAR_TEXT = 225

    private val EQUIPMENT_SLOT_COMPONENT = linkedMapOf(
        0 to 157,
        1 to 163,
        2 to 160,
        13 to 166,
        3 to 169,
        4 to 172,
        5 to 175,
        7 to 178,
        9 to 181,
        10 to 184,
        12 to 187
    )

    private val SKILL_COMPONENTS = linkedMapOf(
        0 to 45,
        2 to 53,
        1 to 61,
        3 to 93,
        4 to 69,
        5 to 85,
        6 to 77,
        23 to 211
    )

    fun open(player: Player, bank: Boolean = false) {
        if (bank) {
            player.temporaryAttributtes[ATTR_OPENED_FROM_BANK] = true
        }
        player.interfaceManager.sendInterface(INTERFACE_ID)
        renderPresetList(player)
        renderCurrentSetup(player)
        applyCloseBehavior(player)
        player.packets.sendHideIComponent(INTERFACE_ID, BACK_TO_BANK, !bank)
    }

    private fun applyCloseBehavior(player: Player) {

        val fromBank = player.temporaryAttributtes[ATTR_OPENED_FROM_BANK] as? Boolean ?: false

        if (fromBank) {
            player.setCloseInterfacesEvent {
                player.temporaryAttributtes.remove(ATTR_OPENED_FROM_BANK)
                player.bank.openBank()
            }
        } else {
            player.temporaryAttributtes.remove(ATTR_OPENED_FROM_BANK)
            player.setCloseInterfacesEvent(null)
        }
    }


    fun handleButtons(player: Player, componentId: Int) {

        when (componentId) {
            CURRENT_SETUP_COMPONENT -> {
                clearConfirmations(player)
                renderCurrentSetup(player)
                highlightSelection(player, -1)
                renderMetaTexts(player, null)
                return
            }

            SAVE_BUTTON -> {
                val preset = getSelectedPreset(player)
                if (preset != null) {
                    handleRename(player, preset)
                } else {
                    handleSave(player)
                }
            }

            OVERWRITE_BUTTON -> {
                val preset = getSelectedPreset(player) ?: return
                showSecurityPopup(
                    player,
                    "Are you sure you want to overwrite?",
                    "OVERWRITE"
                )
                return
            }

            DELETE_BUTTON -> {
                val preset = getSelectedPreset(player) ?: return
                showSecurityPopup(
                    player,
                    "Are you sure you want to delete?",
                    "DELETE"
                )
                return
            }

            SECURITY_YES -> {
                val action = player.temporaryAttributtes["PRESET_SECURITY_ACTION"] as? String
                hideSecurityPopup(player)

                when (action) {
                    "OVERWRITE" -> performOverwrite(player)
                    "DELETE" -> performDelete(player)
                }
                return
            }

            SECURITY_NO -> {
                hideSecurityPopup(player)
                return
            }

            GEARUP_BUTTON -> {
                handleGearUp(player)
                return
            }

            PRAYERBOOK_BUTTON, PRAYERBOOK_TEXT -> {
                if (player.inPkingArea()) {
                    warn(player, "You can't switch prayerbook in player killing areas.")
                    return
                }
                val preset = getSelectedPreset(player)
                if (preset == null) {
                    player.prayer.setPrayerBook(!player.prayer.isAncientCurses)
                } else {
                    preset.switchPrayerBook()
                }
                renderMetaTexts(player, preset)
                return
            }

            SPELLBOOK_BUTTON, SPELLBOOK_TEXT -> {
                if (player.inPkingArea()) {
                    warn(player, "You can't switch spellbook in player killing areas.")
                    return
                }
                val preset = getSelectedPreset(player)
                if (preset == null) {
                    val spellBook = player.getCombatDefinitions().spellBook.toInt()
                    player.getCombatDefinitions().setSpellBook(if (spellBook == 0) 1 else if (spellBook == 1) 2 else 0)
                } else {
                    preset.switchSpellBook()
                }
                renderMetaTexts(player, preset)
                return
            }

            FAMILIAR_BUTTON, FAMILIAR_TEXT -> {
                renderMetaTexts(player, getSelectedPreset(player))
                return
            }
            BACK_TO_BANK -> {
                player.temporaryAttributtes.remove(ATTR_OPENED_FROM_BANK)
                player.setCloseInterfacesEvent(null)
                player.bank.openBank()
                return
            }
        }
        for ((skillId, baseComponent) in SKILL_COMPONENTS) {
            val levelComponent = baseComponent

            if (componentId == levelComponent) {
                handleLevelEdit(player, skillId)
                return
            }
        }
        if (componentId in PRESET_LIST_START..PRESET_LIST_END &&
            (componentId - PRESET_LIST_START) % PRESET_LIST_STRIDE == 0
        ) {
            clearConfirmations(player)
            val index = (componentId - PRESET_LIST_START) / PRESET_LIST_STRIDE
            renderPreset(player, index)
            highlightSelection(player, index)
            return
        }
    }

    private fun handleRename(player: Player, preset: Preset) {

        clearConfirmations(player)

        player.temporaryAttributtes[ATTR_RENAME_PRESET] = preset.name

        player.packets.sendRunScript(
            109,
            "Enter new preset name:"
        )
    }


    private fun handleLevelEdit(player: Player, skillId: Int) {
        if (player.inPkingArea()) {
            warn(player, "You can't change levels in player killing areas.")
            return
        }
        val preset = getSelectedPreset(player)
        if (preset == null) {
            player.packets.sendGameMessage("Select a preset first.")
            return
        }

        player.temporaryAttributtes["PRESET_EDIT_SKILL"] = skillId

        player.packets.sendRunScript(
            108,
            "Enter level (1-99):"
        )
    }

    private fun renderPresetList(player: Player) {

        val presets = player.presetManager.PRESET_SETUPS.values.toList()

        var component = PRESET_LIST_START

        for (i in 0 until MAX_VISIBLE_PRESETS) {

            val preset = presets.getOrNull(i)

            val text = preset?.name ?: "Empty"

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                component,
                text
            )

            component += PRESET_LIST_STRIDE
        }

        player.packets.sendTextOnComponent(
            INTERFACE_ID,
            CURRENT_SETUP_COMPONENT,
            "Current Setup"
        )
    }

    private fun highlightSelection(player: Player, index: Int) {

        val presets = player.presetManager.PRESET_SETUPS.values.toList()

        var component = PRESET_LIST_START

        for (i in 0 until MAX_VISIBLE_PRESETS) {

            val preset = presets.getOrNull(i)
            val baseName = preset?.name ?: "Empty"

            val text = when {
                index == -1 && component == CURRENT_SETUP_COMPONENT -> baseName
                i == index -> "<col=ffff00>$baseName"
                else -> baseName
            }

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                component,
                text
            )

            component += PRESET_LIST_STRIDE
        }

        val currentText =
            if (index == -1) "<col=ffff00>Current Setup"
            else "Current Setup"

        player.packets.sendTextOnComponent(
            INTERFACE_ID,
            CURRENT_SETUP_COMPONENT,
            currentText
        )

        player.temporaryAttributtes[ATTR_SELECTED] = index
    }

    private fun renderCurrentSetup(player: Player) {
        clearEquipment(player)
        renderEquipment(player, player.equipment.items.containerItems)
        renderInventory(player, player.inventory.items.containerItems)
        renderLevels(player, null)
        renderMetaTexts(player, null)
        player.temporaryAttributtes[ATTR_SELECTED] = -1
        updateActionButtons(player, -1)
        updateTitle(player, "Viewing: Current Setup")
    }

    private fun renderInventory(player: Player, items: Array<Item?>) {

        val container = ItemsContainer<Item>(28, false)

        for (slot in items.indices) {
            val item = items[slot] ?: continue
            container.set(slot, item)
        }

        player.packets.sendInterSetItemsOptionsScript(
            INTERFACE_ID,
            INVENTORY_CONTAINER,
            INVENTORY_KEY,
            4,
            7,
            "Add to bank"
        )

        player.packets.sendUnlockOptions(
            INTERFACE_ID,
            INVENTORY_CONTAINER,
            0,
            27,
            0
        )

        player.packets.sendItems(INVENTORY_KEY, container)
        player.packets.sendUpdateItems(INVENTORY_KEY, container, 28)
    }

    private fun renderPreset(player: Player, index: Int) {

        val preset = getPresetByIndex(player, index)

        if (preset == null) {
            renderEmptyPreview(player)
            return
        }

        clearEquipment(player)
        renderEquipment(player, preset.equipment)
        renderInventory(player, preset.inventory)
        renderLevels(player, preset.levels)
        renderMetaTexts(player, preset)
        updateActionButtons(player, index)
        updateTitle(player,"Viewing: ${preset.name}")
    }

    private fun renderEquipment(player: Player, equipment: Array<Item?>) {

        for ((slot, componentId) in EQUIPMENT_SLOT_COMPONENT) {

            val item = equipment.getOrNull(slot)

            if (item == null) {
                player.packets.sendItemOnIComponent(INTERFACE_ID, componentId, -1, 0)
            } else {
                player.packets.sendItemOnIComponent(INTERFACE_ID, componentId, item.id, item.amount)
            }
        }
    }

    private fun renderLevels(player: Player, levels: IntArray?) {

        val skillIndexMap = mapOf(
            Skills.ATTACK to 0,
            Skills.DEFENCE to 1,
            Skills.STRENGTH to 2,
            Skills.HITPOINTS to 3,
            Skills.RANGE to 4,
            Skills.PRAYER to 5,
            Skills.MAGIC to 6,
            Skills.SUMMONING to 7
        )
        for ((skillId, baseComponent) in SKILL_COMPONENTS) {

            val index = skillIndexMap[skillId]

            val level = if (levels != null && index != null && index < levels.size) {
                levels[index]
            } else {
                player.skills.getLevelForXp(skillId)
            }
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                baseComponent + 3,
                level.toString()
            )

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                baseComponent + 4,
                level.toString()
            )
        }
    }


    private fun handleGearUp(player: Player) {
        val preset = getSelectedPreset(player)
        if (preset == null) {
            player.packets.sendGameMessage("You don't have any preset selected.")
            return
        }
        player.presetManager.loadPreset(preset.name, null)
    }

    private fun handleSave(player: Player) {
        if (player.presetManager.PRESET_SETUPS.size - 1 >= MAX_VISIBLE_PRESETS) {
            warn(player,"You can't have more than $MAX_VISIBLE_PRESETS presets.")
            return
        }
        player.temporaryAttributtes.remove(ATTR_CONFIRM_DELETE)
        player.temporaryAttributtes.remove(ATTR_CONFIRM_OVERWRITE)

        player.temporaryAttributtes["PRESET_SAVE_PROMPT"] = true
        player.packets.sendRunScript(109, "Enter setup name: ")
    }

    private fun renderEmptyPreview(player: Player) {

        clearEquipment(player)

        val emptyContainer = ItemsContainer<Item>(28, false)
        player.packets.sendItems(INVENTORY_KEY, emptyContainer)
        player.packets.sendUpdateItems(INVENTORY_KEY, emptyContainer, 28)

        for ((_, baseComponent) in SKILL_COMPONENTS) {
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                baseComponent + 3,
                "1"
            )
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                baseComponent + 4,
                "1"
            )
        }

        player.packets.sendTextOnComponent(INTERFACE_ID, PRAYERBOOK_TEXT, "Regular")
        player.packets.sendTextOnComponent(INTERFACE_ID, SPELLBOOK_TEXT, "Modern")
        player.packets.sendTextOnComponent(INTERFACE_ID, FAMILIAR_TEXT, "None")
        updateActionButtons(player, -1)
        updateTitle(player, "Viewing: Empty Preset")
    }

    private fun clearConfirmations(player: Player) {
        player.temporaryAttributtes.remove(ATTR_CONFIRM_DELETE)
        player.temporaryAttributtes.remove(ATTR_CONFIRM_OVERWRITE)
    }

    fun getSelectedPreset(player: Player): Preset? {
        val idx = player.temporaryAttributtes[ATTR_SELECTED] as? Int ?: -1
        if (idx < 0) return null
        return getPresetByIndex(player, idx)
    }

    fun selectPresetByName(player: Player, name: String) {
        val list = player.presetManager.PRESET_SETUPS.values.toList()

        val index = list.indexOfFirst {
            it.name.equals(name, ignoreCase = true)
        }

        if (index == -1) {
            renderCurrentSetup(player)
            highlightSelection(player, -1)
            return
        }

        renderPreset(player, index)
        highlightSelection(player, index)
    }

    private fun renderMetaTexts(player: Player, preset: Preset?) {

        val curses = preset?.isAncientCurses ?: player.prayer.isAncientCurses
        val spellBook = preset?.spellBook?.toInt() ?: player.combatDefinitions.spellBook.toInt()
        val pouch = preset?.familiar ?: player.familiar?.pouch
        val prayerText = if (curses) "Curses" else "Regular"
        val spellText = when (spellBook) {
            0 -> "Modern"
            1 -> "Ancient"
            2 -> "Lunar"
            else -> "Spellbook $spellBook"
        }
        val familiarText = pouch?.let {
            Utils.capitalizeFirst(it.name.replace("_", " "))
        } ?: "None"


        player.packets.sendTextOnComponent(INTERFACE_ID, PRAYERBOOK_TEXT, prayerText)
        player.packets.sendTextOnComponent(INTERFACE_ID, SPELLBOOK_TEXT, spellText)
        player.packets.sendTextOnComponent(INTERFACE_ID, FAMILIAR_TEXT, familiarText)

        updatePrayerSprite(player, curses)
        updateSpellbookSprite(player, spellBook)
        updateFamiliarSprite(player, pouch)
    }

    private fun updatePrayerSprite(player: Player, curses: Boolean) {

        val spriteId = if (curses) {
            TURMOIL_SPRITE
        } else {
            PIETY_SPRITE
        }

        player.packets.sendSpriteOnIComponent(
            INTERFACE_ID,
            PRAYERBOOK_SPRITE,
            spriteId
        )
    }

    private fun updateSpellbookSprite(player: Player, spellBook: Int) {

        val spriteId = when (spellBook) {
            0 -> FIRE_SURGE_SPRITE
            1 -> VENGEANCE_SPRITE
            2 -> BARRAGE_SPRITE
            else -> FIRE_SURGE_SPRITE
        }

        player.packets.sendSpriteOnIComponent(
            INTERFACE_ID,
            SPELLBOOK_SPRITE,
            spriteId
        )
    }

    private fun updateFamiliarSprite(player: Player, pouch: Summoning.Pouch?) {

        val spriteId = pouch?.realPouchId ?: -1

        player.packets.sendItemOnIComponent(
            INTERFACE_ID,
            FAMILIAR_SPRITE,
            spriteId, 1
        )
    }




    private fun showSecurityPopup(player: Player, message: String, action: String) {

        player.temporaryAttributtes["PRESET_SECURITY_ACTION"] = action

        player.packets.sendTextOnComponent(
            INTERFACE_ID,
            SECURITY_TEXT,
            message
        )

        // unhide component 292
        player.packets.sendHideIComponent(
            INTERFACE_ID,
            SECURITY_POPUP,
            false
        )
    }

    private fun hideSecurityPopup(player: Player) {
        player.packets.sendHideIComponent(
            INTERFACE_ID,
            SECURITY_POPUP,
            true
        )

        player.temporaryAttributtes.remove("PRESET_SECURITY_ACTION")
    }

    private fun performOverwrite(player: Player) {
        val preset = getSelectedPreset(player) ?: return

        val key = preset.name.lowercase()
        player.presetManager.removePreset(key)
        player.presetManager.savePreset(key)

        player.packets.sendGameMessage("Preset \"$key\" has been overwritten.")
        renderPresetList(player)
        selectPresetByName(player, key)
    }

    private fun performDelete(player: Player) {
        val preset = getSelectedPreset(player) ?: return

        val key = preset.name.lowercase()
        player.presetManager.removePreset(key)

        player.packets.sendGameMessage("Preset \"$key\" has been deleted.")

        renderPresetList(player)
        renderCurrentSetup(player)
        highlightSelection(player, -1)
        updateActionButtons(player, -1)
    }


    private fun updateActionButtons(player: Player, index: Int) {

        val presets = player.presetManager.PRESET_SETUPS.values.toList()
        val preset = presets.getOrNull(index)

        val isCurrentSetup = index < 0
        val isEmpty = preset == null

        val saveText = if (!isCurrentSetup && !isEmpty) "Edit" else "Save"
        player.packets.sendTextOnComponent(INTERFACE_ID, SAVE_BUTTON, saveText)

        val hideOtherButtons = isCurrentSetup || isEmpty

        player.packets.sendHideIComponent(INTERFACE_ID, DELETE_PARENT, hideOtherButtons)
        player.packets.sendHideIComponent(INTERFACE_ID, OVERWRITE_PARENT, hideOtherButtons)
        player.packets.sendHideIComponent(INTERFACE_ID, GEARUP_PARENT, hideOtherButtons)
    }

    private fun updateTitle(player: Player, line1: String, line2: String? = null) {

        val text = if (line2 == null)
            line1
        else
            "$line1<br>$line2"

        player.packets.sendTextOnComponent(
            INTERFACE_ID,
            VIEWING_TITLE,
            text
        )
    }


    private fun clearEquipment(player: Player) {
        for (componentId in EQUIPMENT_SLOT_COMPONENT.values) {
            player.packets.sendItemOnIComponent(INTERFACE_ID, componentId, -1, 0)
        }
    }

    private fun getPresetByIndex(player: Player, index: Int): Preset? {
        val list = player.presetManager.PRESET_SETUPS.values.toList()
        return list.getOrNull(index)
    }
}
