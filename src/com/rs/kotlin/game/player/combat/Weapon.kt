package com.rs.kotlin.game.player.combat

interface Weapon {
    val itemId: Int
    val name: String
    val weaponStyle: WeaponStyle
    val attackSpeed: Int? get() = null
    val attackRange: Int? get() = null
    val attackDelay: Int? get() = null
    val animationId: Int? get() = null
    val specialAttack: SpecialAttack? get() = null
}
