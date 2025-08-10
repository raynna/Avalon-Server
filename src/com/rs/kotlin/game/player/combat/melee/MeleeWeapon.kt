package com.rs.kotlin.game.player.combat.melee

import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.special.SpecialAttack

data class MeleeWeapon(
    override val itemId: Int,
    override val name: String,
    override val weaponStyle: WeaponStyle,
    override val attackSpeed: Int? = 4,
    override val attackRange: Int? = null,
    override val attackDelay: Int? = null,
    override val animationId: Int? = null,
    override val specialAttack: SpecialAttack? = null,
    val animations: Map<StyleKey, Int> = emptyMap()
) : Weapon
