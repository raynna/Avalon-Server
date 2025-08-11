package com.rs.kotlin.game.player.combat.special

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.magic.Spell
import com.rs.kotlin.game.player.combat.magic.Spellbook
import com.rs.kotlin.game.player.combat.range.RangedAmmo

data class CombatContext(
    val attacker: Player,
    val defender: Entity,
    val weapon: Weapon,
    val ammo: RangedAmmo? = null,
    val combat: CombatStyle,
    val attackStyle: AttackStyle,
    val usingSpecial: Boolean = false
)

fun CombatContext.registerHit(
    combatType: CombatType = CombatType.MELEE,
    accuracyMultiplier: Double = 1.0,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    spellId: Int = -1
): Hit = combat.registerHit(
    attacker = attacker,
    defender = defender,
    combatType = combatType,
    attackStyle = attackStyle,
    weapon = weapon,
    spellId = spellId,
    accuracyMultiplier = accuracyMultiplier,
    damageMultiplier = damageMultiplier,
    hitLook = hitLook
)

fun CombatContext.repeatHits(
    count: Int, combatType: CombatType = CombatType.MELEE, delays: List<Int> = List(count) { 0 }
) {
    val special = weapon.special!!
    val pendingHits = (0 until count).map { i ->
        PendingHit(
            registerHit(
                combatType = combatType,
                accuracyMultiplier = special.accuracyMultiplier,
                damageMultiplier = special.damageMultiplier
            ), delays[i]
        )
    }
    combat.delayHits(*pendingHits.toTypedArray())
}

fun CombatContext.repeatHits(
    combatType: CombatType = CombatType.MELEE,
    delays: List<Int>,
    damageMultipliers: List<Double> = List(delays.size) { weapon.special!!.damageMultiplier },
    accuracyMultipliers: List<Double> = List(delays.size) { weapon.special!!.accuracyMultiplier }
) {
    require(delays.size == damageMultipliers.size && delays.size == accuracyMultipliers.size) {
        "All lists must be the same size"
    }
    val pendingHits = delays.indices.map { i ->
        PendingHit(
            registerHit(
                combatType = combatType,
                accuracyMultiplier = accuracyMultipliers[i],
                damageMultiplier = damageMultipliers[i]
            ), delays[i]
        )
    }
    combat.delayHits(*pendingHits.toTypedArray())
}

fun CombatContext.forcedHit(
    damageMultiplier: Double = 1.0,
    combatType: CombatType = CombatType.MELEE,
    hitLook: Hit.HitLook = Hit.HitLook.REGULAR_DAMAGE,
    baseDamage: Int? = 0,
    delay: Int = 0,
    hits: Int = 1
): List<Hit> {

    val pendingHits = (0 until hits).map {
        val hit = when (combatType) {
            CombatType.MELEE -> CombatCalculations.calculateMeleeMaxHit(attacker, defender, damageMultiplier)
            CombatType.RANGED -> CombatCalculations.calculateRangedMaxHit(attacker, defender, damageMultiplier)
            CombatType.MAGIC -> {
                requireNotNull(baseDamage) { "Spell required for magic attack" }
                CombatCalculations.calculateMagicMaxHit(attacker, defender, baseDamage)
            }
        }
        hit.look = hitLook
        if (hit.look == Hit.HitLook.REGULAR_DAMAGE && hit.isCriticalHit) {
            hit.critical = false;
        }
        PendingHit(hit, delay)
    }

    combat.delayHits(*pendingHits.toTypedArray())
    return pendingHits.map { it.hit }
}

fun CombatContext.meleeHit(
    accuracyMultiplier: Double = 1.0,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    delay: Int = 0,
    hits: Int = 1
): List<Hit> {
    val accMul = if (usingSpecial && weapon.special != null) {
        weapon.special!!.accuracyMultiplier
    } else accuracyMultiplier

    val dmgMul = if (usingSpecial && weapon.special != null) {
        weapon.special!!.damageMultiplier
    } else damageMultiplier

    val pendingHits = (0 until hits).map {
        val hit = registerHit(
            combatType = CombatType.MELEE,
            accuracyMultiplier = accMul,
            damageMultiplier = dmgMul,
            hitLook = hitLook
        )
        PendingHit(hit, delay)
    }

    combat.delayHits(*pendingHits.toTypedArray())
    return pendingHits.map { it.hit }
}


fun CombatContext.rangedHit(
    accuracyMultiplier: Double = 1.0,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    delay: Int = 0,
    hits: Int = 1
): List<Hit> {
    val accMul = if (usingSpecial && weapon.special != null) {
        weapon.special!!.accuracyMultiplier
    } else accuracyMultiplier

    val dmgMul = if (usingSpecial && weapon.special != null) {
        weapon.special!!.damageMultiplier
    } else damageMultiplier
    val pendingHits = (0 until hits).map {
        val hit = registerHit(
            combatType = CombatType.RANGED,
            accuracyMultiplier = accMul,
            damageMultiplier = dmgMul,
            hitLook = hitLook
        )
        PendingHit(hit, delay)
    }
    combat.delayHits(*pendingHits.toTypedArray())
    return pendingHits.map { it.hit }
}

fun CombatContext.magicHit(
    accuracyMultiplier: Double = 1.0,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    spellId: Int = -1,
    delay: Int = 0,
    hits: Int = 1
): List<Hit> {
    val accMul = if (usingSpecial && weapon.special != null) {
        weapon.special!!.accuracyMultiplier
    } else accuracyMultiplier

    val dmgMul = if (usingSpecial && weapon.special != null) {
        weapon.special!!.damageMultiplier
    } else damageMultiplier
    val pendingHits = (0 until hits).map {
        val hit = registerHit(
            combatType = CombatType.MAGIC,
            accuracyMultiplier = accMul,
            damageMultiplier = dmgMul,
            hitLook = hitLook,
            spellId = spellId
        )
        PendingHit(hit, delay)
    }
    combat.delayHits(*pendingHits.toTypedArray())
    return pendingHits.map { it.hit }
}

class SpecialHitBuilder(private val context: CombatContext) {
    private val hits = mutableListOf<PendingHit>()
    private val special = context.weapon.special!!

    private fun addHit(
        type: CombatType, damageMultiplier: Double, accuracyMultiplier: Double, delay: Int
    ): Hit {
        val h = context.registerHit(
            combatType = type, accuracyMultiplier = accuracyMultiplier, damageMultiplier = damageMultiplier
        )
        hits += PendingHit(h, delay)
        return h
    }

    fun melee(
        damageMultiplier: Double = special.damageMultiplier,
        accuracyMultiplier: Double = special.accuracyMultiplier,
        delay: Int = 0
    ) = addHit(CombatType.MELEE, damageMultiplier, accuracyMultiplier, delay)

    fun ranged(
        damageMultiplier: Double = special.damageMultiplier,
        accuracyMultiplier: Double = special.accuracyMultiplier,
        delay: Int = 0
    ) = addHit(CombatType.RANGED, damageMultiplier, accuracyMultiplier, delay)

    fun magic(
        damageMultiplier: Double = special.damageMultiplier,
        accuracyMultiplier: Double = special.accuracyMultiplier,
        delay: Int = 0
    ) = addHit(CombatType.MAGIC, damageMultiplier, accuracyMultiplier, delay)

    fun nextHit(
        baseHit: Hit, scale: Double, delay: Int = 0
    ): Hit {
        val newHit = Hit(baseHit.source, (baseHit.damage * scale).toInt(), baseHit.look)
        hits += PendingHit(newHit, delay)
        return newHit
    }

    internal fun build(): List<PendingHit> = hits
}

fun CombatContext.hits(block: SpecialHitBuilder.() -> Unit) {
    val builder = SpecialHitBuilder(this)
    builder.block()
    combat.delayHits(*builder.build().toTypedArray())
}



