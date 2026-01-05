package com.rs.kotlin.game.player.combat.magic.special

import com.rs.java.game.item.meta.GreaterRunicStaffMetaData
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.magic.WeaponSpellRegistry

object ObliterationWeapon : WeaponSpellRegistry.Provider {

    override val weaponIds = setOf(24457)

    override fun hasWeapon(player: Player): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return false
        return weapon.isAnyOf("item.obliteration")
    }
}
