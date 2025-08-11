package com.rs.kotlin.game.player.combat
import com.rs.kotlin.game.player.combat.melee.StandardMelee
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.special.SpecialAttack

interface Weapon {
    val itemId: Int
    val name: String
    val weaponStyle: WeaponStyle
    val attackSpeed: Int? get() = null
    val attackRange: Int? get() = null
    val attackDelay: Int? get() = null
    val animationId: Int? get() = null
    val specialAttack: SpecialAttack? get() = null

    companion object {
        fun getWeapon(itemId: Int): Weapon {
            return RangeData.getWeaponByItemId(itemId) ?: StandardMelee.getWeaponByItemId(itemId) ?: StandardMelee.getDefaultWeapon()
        }
    }
}
