package com.rs.kotlin.game.player.combat
import com.rs.java.game.Graphics
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.combat.melee.MeleeWeapon
import com.rs.kotlin.game.player.combat.melee.StandardMelee
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.range.RangedWeapon
import com.rs.kotlin.game.player.combat.special.SpecialAttack
import com.rs.kotlin.game.player.combat.special.SpecialEffect

interface  Weapon {
    val itemId: List<Int>  //
    val name: String
    val weaponStyle: WeaponStyle
    val attackSpeed: Int? get() = null
    val attackRange: Int? get() = null
    val attackDelay: Int? get() = null
    val animationId: Int? get() = null
    val startGfx: Graphics? get() = null
    val soundId: Int? get() = null
    val blockAnimationId: Int? get() = null
    val special: SpecialAttack? get() = null
    val effect: SpecialEffect? get() = null

    companion object {

        fun getCurrentWeapon(player: Player): Weapon =
            getRangedWeapon(player) ?: getMeleeWeapon(player)

        fun getCurrentWeaponId(player: Player): Int {
            return getCurrentWeapon(player).itemId[0]
        }

        fun getRangedWeapon(player: Player): RangedWeapon? {
            val weaponId = player.equipment.weaponId
            return RangeData.getWeaponByItemId(weaponId)
        }

        fun getMeleeWeapon(player: Player): MeleeWeapon {
            val weaponId = player.equipment.weaponId

            StandardMelee.getWeaponByItemId(weaponId)?.let { return it }

            val gloves = player.equipment.getItem(Equipment.SLOT_HANDS.toInt())
            if (player.combatDefinitions.hasGoliath(gloves)) {
                return StandardMelee.getGoliathWeapon()
            }

            return StandardMelee.getDefaultWeapon()
        }


        @JvmStatic
        fun isRangedWeapon(player: Player): Boolean {
            val weaponId = player.equipment.getWeaponId()
            return RangeData.getWeaponByItemId(weaponId) != null
        }


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
