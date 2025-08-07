package com.rs.kotlin.game.player.combat.range


abstract class RangeData {

    abstract val weapons: List<RangedWeapon>
    abstract val ammunition: List<RangedAmmo>

    val weaponMap: Map<Int, RangedWeapon> by lazy { weapons.associateBy { it.itemId } }
    val ammoMap: Map<Int, RangedAmmo> by lazy { ammunition.associateBy { it.itemId } }

    companion object {
        private val allData: List<RangeData> = listOf(StandardRanged, SpecialRanged)

        @JvmStatic
        fun getWeaponByItemId(itemId: Int): RangedWeapon? {
            return allData.firstNotNullOfOrNull { it.weaponMap[itemId] }
        }

        @JvmStatic
        fun getAmmoByItemId(itemId: Int): RangedAmmo? {
            return allData.firstNotNullOfOrNull { it.ammoMap[itemId] }
        }
    }
}


data class SpecialEffect(
    val type: EffectType,
    val chance: Double,
    val damage: Int,
    val duration: Int? = null
)

enum class AmmoType {
    ARROW,
    BOLT,
    DART,
    KNIFE,
    JAVELIN,
    CHINCHOMPA,
    THROWNAXE
}

enum class EffectType {
    DRAGONFIRE,
}