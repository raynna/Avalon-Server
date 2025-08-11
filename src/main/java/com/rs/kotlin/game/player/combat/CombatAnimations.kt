package com.rs.kotlin.game.player.combat

import com.rs.kotlin.game.player.combat.melee.StandardMelee
import com.rs.kotlin.game.player.combat.range.RangeData

object CombatAnimations {

    private const val DEFAULT_ANIMATION = 422
    private const val DEFAULT_RANGE_ANIMATION = 426

    fun getAnimation(itemId: Int, attackStyle: AttackStyle, styleIndex: Int): Int {
        StandardMelee.getWeaponByItemId(itemId)?.let { meleeWeapon ->
            return meleeWeapon.animations[StyleKey(attackStyle, styleIndex)] ?: meleeWeapon.animationId?: DEFAULT_ANIMATION
        }

        RangeData.getWeaponByItemId(itemId)?.let { rangedWeapon ->
            return rangedWeapon.animationId?: DEFAULT_RANGE_ANIMATION
        }
        if (itemId == -2) {
            val goliathGloves = StandardMelee.getGoliathWeapon()
            return goliathGloves.animations[StyleKey(attackStyle, styleIndex)] ?: DEFAULT_ANIMATION
        }
        val unarmed = StandardMelee.getDefaultWeapon()
        return unarmed.animations[StyleKey(attackStyle, styleIndex)] ?: DEFAULT_ANIMATION
    }
}
