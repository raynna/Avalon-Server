package com.rs.kotlin.game.player.combat.magic.special

import com.rs.java.game.item.meta.GreaterRunicStaffMetaData
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.magic.WeaponSpellRegistry

object GreaterRunicStaffWeapon : WeaponSpellRegistry.Provider {

    override val weaponIds = setOf(22498, 22494)

    override fun hasWeapon(player: Player): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return false
        if (!weapon.isAnyOf("item.greater_runic_staff_inactive", "item.greater_runic_staff_uncharged", "item.greater_runic_staff_charged")) {
            return false;
        }
        if (weapon.metadata == null) {
            weapon.metadata = GreaterRunicStaffMetaData(0, 0)
        }
        val data = weapon.metadata
        return !(data is GreaterRunicStaffMetaData && data.charges < 1)
    }

    fun getSpellId(attacker: Player): Int {
        val weapon = attacker.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return -1
        val data = weapon.metadata as? GreaterRunicStaffMetaData ?: return -1
        if (data.spellId == -1) {
            attacker.message("Your greater runic staff has no spell selected");
            return -1;
        }
        if (data.charges <= 0) {
            attacker.message("Your greater runic staff has no charges.")
            return -1
        }
        return data.spellId
    }

    fun consumeCharge(player: Player, amount: Int = 1) {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return
        val data = weapon.metadata as? GreaterRunicStaffMetaData ?: return

        data.removeCharges(amount)
        if (data.charges == 0) {
            player.message("Your greater runic staff has run out of charges.")
        }
    }

}
