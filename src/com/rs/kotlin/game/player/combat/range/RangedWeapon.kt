package com.rs.kotlin.game.player.combat.range

import com.rs.kotlin.game.player.combat.WeaponStyle

data class RangedWeapon(
    val itemId: Int,
    val name: String,
    val weaponType: WeaponStyle,
    val levelRequired: Int,
    val attackSpeed: Int,
    val attackRange: Int,
    val animationId: Int,
    val projectileId: Int? = null,
    val specialAttack: SpecialAttack? = null,
    val ammoType: AmmoType
)
