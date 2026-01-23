package com.rs.kotlin.game.player.combat.damage

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Player

object DamageScaler {

    private const val SCALE = 10

    private fun ceilToScale(value: Int): Int {
        if (value <= 0) return value
        return ((value + SCALE - 1) / SCALE) * SCALE
    }

    private fun isEnabled(attacker: Entity?, defender: Entity): Player? {
        val player =
            when {
                attacker is Player -> attacker
                defender is Player -> defender
                else -> null
            } ?: return null

        return if (player.varsManager.getBitValue(1485) == 1) player else null
    }

    fun getScaledDamage(attacker: Entity?, defender: Entity, hit: Hit): Int {
        val player = isEnabled(attacker, defender) ?: return hit.damage

        // don't scale heals or absorbs
        if (hit.look == Hit.HitLook.HEALED_DAMAGE ||
            hit.look == Hit.HitLook.ABSORB_DAMAGE) {
            return hit.damage
        }

        return ceilToScale(hit.damage)
    }

    fun getScaledMaxHit(attacker: Entity?, defender: Entity, hit: Hit): Int {
        val player = isEnabled(attacker, defender) ?: return hit.maxHit
        if (hit.maxHit <= 0) return hit.maxHit

        return ceilToScale(hit.maxHit)
    }
}
