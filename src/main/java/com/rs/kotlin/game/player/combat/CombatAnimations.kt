package com.rs.kotlin.game.player.combat

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.combat.melee.StandardMelee
import com.rs.kotlin.game.player.combat.range.RangeData

object CombatAnimations {

    private const val DEFAULT_ANIMATION = 422
    private const val DEFAULT_RANGE_ANIMATION = 426

    fun getBlockAnimation(player: Player): Int {
        val shieldId = player.equipment.getShieldId()
        ShieldBlockAnimations.getBlockAnimationFor(shieldId)?.let { return it }

        val weaponId = player.equipment.getWeaponId()
        StandardMelee.getWeaponByItemId(weaponId)?.blockAnimationId?.let { return it }

        return ShieldBlockAnimations.DEFAULT_BLOCK_ANIM
    }

    fun getSound(itemId: Int, attackStyle: AttackStyle, styleIndex: Int): Int {
        StandardMelee.getWeaponByItemId(itemId)?.let { meleeWeapon ->
            return meleeWeapon.sounds[StyleKey(attackStyle, styleIndex)] ?: meleeWeapon.soundId?: 2548
        }

        RangeData.getWeaponByItemId(itemId)?.let { rangedWeapon ->
            return rangedWeapon.soundId?: 2702
        }
        if (itemId == -2) {
            val goliathGloves = StandardMelee.getGoliathWeapon()
            return goliathGloves.sounds[StyleKey(attackStyle, styleIndex)] ?: Rscm.lookup("sound.punch")
        }
        val unarmed = StandardMelee.getDefaultWeapon()
        return unarmed.sounds[StyleKey(attackStyle, styleIndex)] ?: Rscm.lookup("sound.punch")
    }

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
