package com.rs.kotlin.game.player.combat.melee

import com.rs.kotlin.game.player.combat.AttackStyle
import com.rs.kotlin.game.player.combat.SpecialAttack
import com.rs.kotlin.game.player.combat.Weapon
import com.rs.kotlin.game.player.combat.WeaponStyle

data class MeleeWeapon(
    override val itemId: Int,
    override val name: String,
    override val weaponStyle: WeaponStyle,
    override val attackSpeed: Int? = 4,
    override val attackRange: Int? = null,
    override val attackDelay: Int? = null,
    override val animationId: Int? = null,
    override val specialAttack: SpecialAttack? = null,
    val animations: Map<AttackStyle, Int> = emptyMap()
) : Weapon
