package com.rs.kotlin.game.player.combat.melee

open class MeleeData {
    open val weapons: List<MeleeWeapon> = emptyList()

    private val weaponMap: Map<Int, MeleeWeapon> by lazy {
        weapons.associateBy { it.itemId }
    }

    fun getWeaponByItemId(itemId: Int): MeleeWeapon? = weaponMap[itemId]
}
