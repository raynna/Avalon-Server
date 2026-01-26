package com.rs.kotlin.game.player.combat.range

import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player


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

        fun getCurrentAmmo(player: Player): RangedAmmo? {
            val ammoItem = player.equipment.items[Equipment.SLOT_ARROWS.toInt()]
            return ammoItem?.let { getAmmoByItemId(it.id) }
        }

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
    MORRIGAN_THROWING,
    JAVELIN,
    CHINCHOMPA,
    THROWNAXE,
    CANNON,
    NONE
}

enum class EffectType {
    DRAGONFIRE,
}