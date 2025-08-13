package com.rs.kotlin.game.player.combat
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.combat.melee.StandardMelee
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.special.SpecialAttack
import com.rs.kotlin.game.player.combat.special.SpecialEffect

interface Weapon {
    val itemId: List<Int>  //
    val name: String
    val weaponStyle: WeaponStyle
    val attackSpeed: Int? get() = null
    val attackRange: Int? get() = null
    val attackDelay: Int? get() = null
    val animationId: Int? get() = null
    val blockAnimationId: Int? get() = null
    val special: SpecialAttack? get() = null
    val effect: SpecialEffect? get() = null

    companion object {
        fun getWeapon(itemId: Int): Weapon {
            return RangeData.getWeaponByItemId(itemId) ?: StandardMelee.getWeaponByItemId(itemId) ?: StandardMelee.getDefaultWeapon()
        }

        fun itemIds(vararg items: Any): List<Int> {
            return items.map {
                when (it) {
                    is Int -> it
                    is String -> {
                        val key = if (it.startsWith("item.")) it else "item.$it"
                        Rscm.lookup(key)
                    }
                    else -> throw IllegalArgumentException("Item must be Int or String")
                }
            }
        }
    }
}
