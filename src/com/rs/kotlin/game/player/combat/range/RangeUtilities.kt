package com.rs.kotlin.game.player.combat.range


abstract class RangeUtilities(val id: Int) {
    companion object {
        const val STANDARD_ID = 300
        const val SPECIAL_ID = 301

        val STANDARD: StandardRanged by lazy { StandardRanged }
        val SPECIAL: SpecialRanged by lazy { SpecialRanged }

        @JvmStatic
        fun get(id: Int): RangeUtilities? = when (id) {
            STANDARD_ID -> STANDARD
            SPECIAL_ID -> SPECIAL
            else -> null
        }

        @JvmStatic
        fun getWeaponByItemId(itemId: Int): RangedWeapon? {
            return StandardRanged.weapons.find { it.itemId == itemId }
                ?: SpecialRanged.weapons.find { it.itemId == itemId }
        }

        @JvmStatic
        fun getAmmoByItemId(itemId: Int): RangedAmmo? {
            return StandardRanged.ammunition.find { it.itemId == itemId }
                ?: SpecialRanged.ammunition.find { it.itemId == itemId }
        }
    }

    abstract val weapons: List<RangedWeapon>
    abstract val ammunition: List<RangedAmmo>
}

data class SpecialAttack(
    val name: String,
    val energyCost: Int,
    val damageMultiplier: Double,
    val accuracyMultiplier: Double,
    val specialProjectileId: Int? = null,
    val specialEffect: SpecialEffect? = null
)

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