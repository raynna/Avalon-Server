package com.rs.kotlin.game.player.combat.melee

import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.special.SpecialAttack
import com.rs.kotlin.game.player.combat.special.SpecialEffect

data class MeleeWeapon(
    override val itemId: List<Int>,
    override val name: String,
    override val weaponStyle: WeaponStyle,
    override val attackSpeed: Int? = 4,
    override val attackRange: Int? = null,
    override val attackDelay: Int? = null,
    override val animationId: Int? = null,
    override val special: SpecialAttack? = null,
    override val effect: SpecialEffect? = null,
    val animations: Map<StyleKey, Int> = emptyMap()
) : Weapon
