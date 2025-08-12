package com.rs.kotlin.game.player.combat.melee

open class MeleeData {
    open val weapons: List<MeleeWeapon> = emptyList()

    private val weaponMap: Map<Int, MeleeWeapon> by lazy {
        weapons.flatMap { weapon ->
            weapon.itemId.map { it to weapon }
        }.toMap()
    }

    fun getWeaponByItemId(itemId: Int): MeleeWeapon? = weaponMap[itemId]
}
