package com.rs.kotlin.game.player.combat.range

import com.rs.kotlin.game.player.combat.SpecialAttack
import com.rs.kotlin.game.player.combat.Weapon
import com.rs.kotlin.game.player.combat.WeaponStyle

data class RangedWeapon(
    override val itemId: Int,
    override val name: String,
    override val weaponStyle: WeaponStyle,
    override val attackSpeed: Int? = 4,
    override val attackRange: Int? = null,
    override val attackDelay: Int? = null,
    override val animationId: Int? = null,
    override val specialAttack: SpecialAttack? = null,
    val poisonSeverity: Int = -1,
    val projectileId: Int? = null,
    val ammoType: AmmoType,
    val maxAmmoTier: AmmoTier? = null,
    val allowedAmmoIds: Set<Int>? = null
) : Weapon
