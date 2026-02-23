package com.rs.kotlin.game.player.combat

import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.combat.melee.StandardMelee
import com.rs.kotlin.game.player.combat.range.RangeData

object CombatUtils {
    private const val DEFAULT_ANIMATION = 422
    private const val DEFAULT_RANGE_ANIMATION = 426

    fun getBlockAnimation(player: Player): Int {
        val shieldId = player.equipment.getShieldId()

        ShieldBlockAnimations.getBlockAnimationFor(shieldId)?.let {
            if (it > 0) return it
        }

        val weaponId = player.equipment.getWeaponId()

        StandardMelee.getWeaponByItemId(weaponId)?.blockAnimationId?.let {
            if (it > 0) return it
        }

        RangeData.getWeaponByItemId(weaponId)?.blockAnimationId?.let {
            if (it > 0) return it
        }

        return ShieldBlockAnimations.DEFAULT_BLOCK_ANIM
    }

    fun getSound(
        itemId: Int,
        attackStyle: AttackStyle,
        styleIndex: Int,
    ): Int {
        StandardMelee.getWeaponByItemId(itemId)?.let { meleeWeapon ->
            return meleeWeapon.sounds[StyleKey(attackStyle, styleIndex)] ?: meleeWeapon.soundId ?: 2548
        }

        RangeData.getWeaponByItemId(itemId)?.let { rangedWeapon ->
            return rangedWeapon.soundId ?: 2702
        }
        if (itemId == -2) {
            val goliathGloves = StandardMelee.getGoliathWeapon()
            return goliathGloves.sounds[StyleKey(attackStyle, styleIndex)] ?: Rscm.lookup("sound.punch")
        }
        val unarmed = StandardMelee.getDefaultWeapon()
        return unarmed.sounds[StyleKey(attackStyle, styleIndex)] ?: Rscm.lookup("sound.punch")
    }

    fun getBlockSound(player: Player): Int {
        val shieldId = player.equipment.getShieldId()
        if (shieldId != -1) {
            println("shieldId: $shieldId")
            val shieldName =
                player.equipment
                    .getItem(Equipment.SLOT_SHIELD.toInt())
                    ?.definitions
                    ?.name
                    ?.lowercase() ?: ""
            println("name contains shield: $shieldName ${shieldName.contains("shield")}")
            if (shieldName.contains("shield")) {
                return Rscm.lookup("sound.shield_block")
            }
        }

        val bodyId = player.equipment.getChestId()
        if (bodyId != -1) {
            val bodyName =
                player.equipment
                    .getItem(Equipment.SLOT_CHEST.toInt())
                    ?.definitions
                    ?.name
                    ?.lowercase() ?: ""

            return when {
                bodyName.contains("platebody") -> Rscm.lookup("sound.metal_block")
                bodyName.contains("chainbody") -> Rscm.lookup("sound.metal_block")
                else -> Rscm.lookup("sound.human_block")
            }
        }

        return Rscm.lookup("sound.human_block")
    }

    fun getSound(player: Player): Int {
        val (weaponId, attackStyle, styleIndex) =
            resolveStyle(player) ?: return Rscm.lookup("sound.punch")

        return getSound(weaponId, attackStyle, styleIndex)
    }

    fun getAnimation(
        itemId: Int,
        attackStyle: AttackStyle,
        styleIndex: Int,
    ): Int {
        StandardMelee.getWeaponByItemId(itemId)?.let { meleeWeapon ->
            return meleeWeapon.animations[StyleKey(attackStyle, styleIndex)] ?: meleeWeapon.animationId ?: DEFAULT_ANIMATION
        }

        RangeData.getWeaponByItemId(itemId)?.let { rangedWeapon ->
            return rangedWeapon.animationId ?: DEFAULT_RANGE_ANIMATION
        }
        if (itemId == -2) {
            val goliathGloves = StandardMelee.getGoliathWeapon()
            return goliathGloves.animations[StyleKey(attackStyle, styleIndex)] ?: DEFAULT_ANIMATION
        }
        val unarmed = StandardMelee.getDefaultWeapon()
        return unarmed.animations[StyleKey(attackStyle, styleIndex)] ?: DEFAULT_ANIMATION
    }

    fun getAnimation(player: Player): Int {
        val (weaponId, attackStyle, styleIndex) =
            resolveStyle(player) ?: return DEFAULT_ANIMATION

        return getAnimation(weaponId, attackStyle, styleIndex)
    }

    private fun resolveStyle(player: Player): Triple<Int, AttackStyle, Int>? {
        val weaponId = player.equipment.weaponId
        val styleIndex = player.combatDefinitions.attackStyle

        val attackStyle =
            Weapon
                .getWeapon(weaponId)
                .weaponStyle
                .styleSet
                .styleAt(styleIndex)
                ?: return null

        return Triple(weaponId, attackStyle, styleIndex)
    }
}
