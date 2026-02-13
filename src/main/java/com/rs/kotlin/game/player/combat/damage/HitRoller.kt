package com.rs.kotlin.game.player.combat.damage

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatCalculations
import com.rs.kotlin.game.player.combat.CombatType

class HitRoller(
    private val attacker: Player,
    private val target: Entity,
    private val type: CombatType
) {
    private var accuracyMultiplier = 1.0
    private var damageMultiplier = 1.0
    private var forcedRange: ((Int) -> IntRange)? = null
    private var forcedLook: Hit.HitLook? = null
    private var spellId: Int = -1

    fun accuracy(mult: Double) = apply { accuracyMultiplier = mult }
    fun damageMultiplier(mult: Double) = apply { damageMultiplier = mult }
    fun spell(id: Int) = apply { spellId = id }
    fun look(look: Hit.HitLook) = apply { forcedLook = look }

    fun damageFromMaxHit(range: (Int) -> IntRange) = apply {
        forcedRange = range
    }

    fun roll(mode: CombatHitRoll = CombatHitRoll.NORMAL): Hit {
        val accurate = when (mode) {
            CombatHitRoll.GUARANTEED -> true
            CombatHitRoll.NORMAL ->
                Math.random() < CombatCalculations.getHitChance(
                    attacker,
                    target,
                    type,
                    accuracyMultiplier
                )
        }

        val maxHit = when (type) {
            CombatType.MELEE ->
                CombatCalculations.getMeleeMaxHit(attacker, target, damageMultiplier)

            CombatType.RANGED ->
                CombatCalculations.getRangedMaxHit(attacker, target, damageMultiplier)

            CombatType.MAGIC ->
                CombatCalculations.getMagicMaxHit(
                    attacker,
                    target,
                    baseDamage = -1,
                    spellId = spellId,
                    specialMultiplier = damageMultiplier
                )
        }

        val look = forcedLook ?: when (type) {
            CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
            CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
            CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
        }

        val hit = Hit(attacker, 0, maxHit.max, look)
        hit.baseMaxHit = maxHit.base
        hit.maxHit = maxHit.max

        if (!accurate) {
            hit.damage = 0
            return hit
        }

        hit.damage = forcedRange?.let {
            val range = it(maxHit.max)
            if (range.first >= range.last) range.first
            else Utils.random(range.first, range.last)
        } ?: Utils.random(maxHit.min, maxHit.max)

        if (target is NPC && target.id == 4474) {
            hit.damage = forcedRange?.invoke(hit.maxHit)?.last ?: hit.maxHit
        }

        val critThreshold = (maxHit.base * 0.99).toInt()
        if (hit.damage >= critThreshold) {
            hit.setCriticalMark()
            hit.critical = true
        }
        return hit
    }
}
