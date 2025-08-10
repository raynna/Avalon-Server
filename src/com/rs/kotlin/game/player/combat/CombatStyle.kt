package com.rs.kotlin.game.player.combat

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.magic.Spell

interface CombatStyle {
    fun canAttack(attacker: Player, defender: Entity): Boolean
    fun getAttackSpeed(): Int
    fun getHitDelay(): Int
    fun getAttackDistance(): Int
    fun attack()
    fun onStop(interrupted: Boolean)
    fun delayHits(vararg hits: PendingHit)
    fun onHit(hit: Hit)
    fun scheduleHit(delay: Int, action: () -> Unit) {
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                action()
            }
        }, delay)
    }
    fun registerHit(
        attacker: Player,
        defender: Entity,
        combatType: CombatType,
        attackStyle: AttackStyle = AttackStyle.ACCURATE,
        weapon: Weapon? = null,
        spell: Spell? = null,
        accuracyMultiplier: Double = 1.0,
        damageMultiplier: Double = 1.0,
        hitLook: Hit.HitLook? = null
    ): Hit {
        val resolvedHitLook = hitLook ?: when (combatType) {
            CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
            CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
            CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
        }

        val landed = when (combatType) {
            CombatType.MELEE -> {
                requireNotNull(weapon) { "Weapon required for melee attack" }
                CombatCalculations.calculateMeleeAccuracy(attacker, defender, accuracyMultiplier)
            }
            CombatType.RANGED -> {
                requireNotNull(weapon) { "Weapon required for ranged attack" }
                CombatCalculations.calculateRangedAccuracy(attacker, defender, accuracyMultiplier)
            }
            CombatType.MAGIC -> CombatCalculations.calculateMagicAccuracy(attacker, defender)
        }

        val hit = if (landed) {
            when (combatType) {
                CombatType.MELEE -> CombatCalculations.calculateMeleeMaxHit(attacker, defender, damageMultiplier)
                CombatType.RANGED -> CombatCalculations.calculateRangedMaxHit(attacker, defender, damageMultiplier)
                CombatType.MAGIC -> {
                    requireNotNull(spell) { "Spell required for magic attack" }
                    CombatCalculations.calculateMagicMaxHit(attacker, defender, spell)
                }
                else -> null
            }
        } else Hit(defender, 0, resolvedHitLook)
        if (resolvedHitLook != null) {
            hit?.setLook(resolvedHitLook)
        }
        if (spell != null && hit != null) {//TODO temporary fix for things like entangle and curse spells to hit
            if (landed && spell.damage == -1)
                hit.damage = Utils.random(1, 10)
        }
        return hit!!
    }



}
