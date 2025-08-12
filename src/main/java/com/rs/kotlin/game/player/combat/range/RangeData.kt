package com.rs.kotlin.game.player.combat.range


abstract class RangeData {

    abstract val weapons: List<RangedWeapon>
    abstract val ammunition: List<RangedAmmo>

    val weaponMap: Map<Int, RangedWeapon> by lazy {
        weapons.flatMap { weapon ->
            weapon.itemId.map { id -> id to weapon }
        }.toMap()
    }
    val ammoMap: Map<Int, RangedAmmo> by lazy { ammunition.associateBy { it.itemId } }

    companion object {
        private val allData: List<RangeData> = listOf(StandardRanged, SpecialRanged)

        fun getWeaponByItemId(itemId: Int): RangedWeapon? {
            return allData.firstNotNullOfOrNull { rangeData ->
                rangeData.weaponMap[itemId]
            }
        }

        @JvmStatic
        fun getAmmoByItemId(itemId: Int): RangedAmmo? {
            return allData.firstNotNullOfOrNull { it.ammoMap[itemId] }
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
    NONE
}

enum class EffectType {
    DRAGONFIRE,
}