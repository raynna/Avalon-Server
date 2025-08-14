package com.rs.kotlin.game.player.combat

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.magic.Spell
import com.rs.kotlin.game.player.combat.magic.Spellbook
import com.rs.kotlin.game.player.combat.special.CombatContext

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
    fun addHit(
        damage: Int,
        attacker: Player,
        defender: Entity,
        combatType: CombatType,
        hitLook: Hit.HitLook? = null
    ): Hit {
        val resolvedHitLook = hitLook ?: when (combatType) {
            CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
            CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
            CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
        }
        val hit = Hit(attacker, damage, resolvedHitLook)
        if (hit.isCriticalHit && !hit.isCombatLook) {
            hit.critical = false
        }
        return hit
    }

    fun registerHit(
        attacker: Player,
        defender: Entity,
        combatType: CombatType,
        attackStyle: AttackStyle = AttackStyle.ACCURATE,
        weapon: Weapon? = null,
        spellId: Int = -1,
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
                    requireNotNull(spellId) { "Spell required for magic attack" }
                    CombatCalculations.calculateMagicMaxHit(attacker, defender, spellId)
                }
            }
        } else {
            Hit(attacker, 0, resolvedHitLook)
        }
        hit.look = resolvedHitLook
        if (!landed) {
            hit.landed = false
        }
        if (hit.isCriticalHit && !hit.isCombatLook) {
            hit.critical = false
        }
        return hit
    }

    fun executeEffect(combatContext: CombatContext): Boolean {
        combatContext.weapon.effect?.let { effect ->
            CombatAnimations.getAnimation(combatContext.weaponId, combatContext.attackStyle, combatContext.attacker.combatDefinitions.attackStyle).let { combatContext.attacker.animate(it) }
            effect.execute(combatContext)
            return true;
        }
        return false;
    }

    fun executeSpecialAttack(combatContext: CombatContext): Boolean {
        if (combatContext.attacker.combatDefinitions.isUsingSpecialAttack) {
            val special = combatContext.weapon.special
            if (special != null) {
                val specialEnergy = combatContext.attacker.combatDefinitions.specialAttackPercentage
                if (specialEnergy >= special.energyCost) {
                    val specialContext = combatContext.copy(usingSpecial = true)
                    special.execute(specialContext)
                    combatContext.attacker.combatDefinitions.decreaseSpecialAttack(special.energyCost)
                    return true
                } else {
                    combatContext.attacker.message("You don't have enough special attack energy.")
                    combatContext.attacker.combatDefinitions.switchUsingSpecialAttack()
                    return false
                }
            } else {
                combatContext.attacker.message("This weapon has no special attack.")
                combatContext.attacker.combatDefinitions.switchUsingSpecialAttack()
                return false
            }
        }
        return false
    }



}
