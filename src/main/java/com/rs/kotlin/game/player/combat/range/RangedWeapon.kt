package com.rs.kotlin.game.player.combat.range

import com.rs.java.game.Graphics
import com.rs.kotlin.game.player.combat.special.SpecialAttack
import com.rs.kotlin.game.player.combat.Weapon
import com.rs.kotlin.game.player.combat.WeaponStyle
import com.rs.kotlin.game.player.combat.special.SpecialEffect

data class RangedWeapon(
    override val itemId: List<Int>,
    override val name: String,
    override val weaponStyle: WeaponStyle,
    override val attackSpeed: Int? = -1,
    override val attackRange: Int? = null,
    override val attackDelay: Int? = null,
    override val animationId: Int? = null,
    override val blockAnimationId: Int? = null,
    override val special: SpecialAttack? = null,
    override val effect: SpecialEffect? = null,
    val poisonSeverity: Int = -1,
    val projectileId: Int? = null,
    val ammoType: AmmoType? = null,
    val maxAmmoTier: AmmoTier? = null,
    val allowedAmmoIds: Set<Int>? = null
) : Weapon
