package com.rs.kotlin.game.player.combat.range


abstract class RangeData {

    abstract val weapons: List<RangedWeapon>
    abstract val ammunition: List<RangedAmmo>

    val weaponMap: Map<Int, RangedWeapon> by lazy {
        weapons.flatMap { weapon ->
            weapon.itemId.map { id -> id to weapon }
        }.toMap()
    }

    val ammoMap: Map<Int, RangedAmmo> by lazy {
        ammunition.flatMap { ammo ->
            ammo.itemId.map { id -> id to ammo }
        }.toMap()
    }

    companion object {
        private val allData: List<RangeData> = listOf(StandardRanged)

        fun getWeaponByItemId(itemId: Int): RangedWeapon? {
            return allData.firstNotNullOfOrNull { rangeData ->
                rangeData.weaponMap[itemId]
            }
        }

        @JvmStatic
        fun getAmmoByItemId(itemId: Int): RangedAmmo? {
            return allData.firstNotNullOfOrNull { rangeData ->
                rangeData.ammoMap[itemId]
            }
        }

    }
}

enum class AmmoType {
    ARROW,
    BOLT,
    DART,
    THROWING,
    JAVELIN,
    CHINCHOMPA,
    THROWNAXE,
    CANNON,
    NONE
}

enum class EffectType {
    DRAGONFIRE,
}