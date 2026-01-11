package com.rs.kotlin.game.player.combat.range.special

import com.rs.java.game.item.Item
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils

object SwiftGloves {

    private val SWIFT_WEAPON_IDS = setOf(
        Item.getId("item.dark_bow"),
        Item.getId("item.dark_bow_blue"),
        Item.getId("item.dark_bow_green"),
        Item.getId("item.dark_bow_white"),
        Item.getId("item.dark_bow_yellow"),
        Item.getId("item.magic_shortbow"),
        Item.getId("item.magic_longbow"),
        Item.getId("item.guthix_bow"),
        Item.getId("item.zamorak_bow"),
        Item.getId("item.saradomin_bow"),
        Item.getId("item.comp_ogre_bow"),
        Item.getId("item.sagaie"),
        Item.getId("item.toktz_xil_ul"),
    )

    private val SWIFT_NAME_MATCHES = listOf(
        "crystal bow",
        "zaryte bow",
        "karil",
        "morrigan",
        " dart",
        " crossbow",
        " javelin",
        " thrownaxe",
        " throwing",
    )

    private fun isSwiftWeapon(attacker: Player): Boolean {
        val weapon = attacker.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return false

        if (weapon.id in SWIFT_WEAPON_IDS) return true

        val name = weapon.name.lowercase()
        return SWIFT_NAME_MATCHES.any { name.contains(it) }
    }

    fun swiftGlovesProc(attacker: Player): Boolean {
        val hasGloves = attacker.equipment
            .getItem(Equipment.SLOT_HANDS.toInt())
            ?.isAnyOf(
                "item.swift_gloves_red",
                "item.swift_gloves_yellow",
                "item.swift_gloves_black",
                "item.swift_gloves_white"
            ) == true

        if (!hasGloves) return false
        if (!isSwiftWeapon(attacker)) return false

        return Utils.roll(1, 5)
    }
}
