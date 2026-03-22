package raynna.game.player.combat.magic.special

import raynna.game.item.meta.GreaterRunicStaffMetaData
import raynna.game.player.Equipment
import raynna.game.player.Player
import raynna.game.player.combat.magic.WeaponSpellRegistry

object ObliterationWeapon : WeaponSpellRegistry.Provider {

    override val weaponIds = setOf(24457)

    override fun hasWeapon(player: Player): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return false
        return weapon.isAnyOf("item.obliteration")
    }
}
