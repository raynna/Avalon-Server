package com.rs.kotlin.game.player.combat.special

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.AttackStyle
import com.rs.kotlin.game.player.combat.CombatStyle
import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.player.combat.Weapon
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.magic.Spell
import com.rs.kotlin.game.player.combat.range.RangedAmmo

data class SpecialContext(
    val attacker: Player,
    val defender: Entity,
    val weapon: Weapon,
    val ammo: RangedAmmo? = null,
    val combat: CombatStyle,
    val attackStyle: AttackStyle,
)

fun SpecialContext.registerHit(
    combatType: CombatType = CombatType.MELEE,
    accuracyMultiplier: Double = 1.0,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    spell: Spell? = null
): Hit = combat.registerHit(
    attacker = attacker,
    defender = defender,
    combatType = combatType,
    attackStyle = attackStyle,
    weapon = weapon,
    spell = spell,
    accuracyMultiplier = accuracyMultiplier,
    damageMultiplier = damageMultiplier,
    hitLook = hitLook
)

fun SpecialContext.repeatHits(
    count: Int, combatType: CombatType = CombatType.MELEE, delays: List<Int> = List(count) { 0 }
) {
    val special = weapon.specialAttack!!
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

fun SpecialContext.repeatHits(
    combatType: CombatType = CombatType.MELEE,
    delays: List<Int>,
    damageMultipliers: List<Double> = List(delays.size) { weapon.specialAttack!!.damageMultiplier },
    accuracyMultipliers: List<Double> = List(delays.size) { weapon.specialAttack!!.accuracyMultiplier }
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

fun SpecialContext.melee(
    accuracyMultiplier: Double = weapon.specialAttack!!.accuracyMultiplier,
    damageMultiplier: Double = weapon.specialAttack!!.damageMultiplier,
    hitLook: Hit.HitLook? = null,
    delay: Int = 0,
    hits: Int = 1
): List<Hit> {
    val pendingHits = (0 until hits).map {
        val hit = registerHit(
            combatType = CombatType.MELEE,
            accuracyMultiplier = accuracyMultiplier,
            damageMultiplier = damageMultiplier,
            hitLook = hitLook
        )
        PendingHit(hit, delay)
    }
    combat.delayHits(*pendingHits.toTypedArray())
    return pendingHits.map { it.hit }
}

fun SpecialContext.ranged(
    accuracyMultiplier: Double = weapon.specialAttack!!.accuracyMultiplier,
    damageMultiplier: Double = weapon.specialAttack!!.damageMultiplier,
    hitLook: Hit.HitLook? = null,
    delay: Int = 0,
    hits: Int = 1
): List<Hit> {
    val pendingHits = (0 until hits).map {
        val hit = registerHit(
            combatType = CombatType.RANGED,
            accuracyMultiplier = accuracyMultiplier,
            damageMultiplier = damageMultiplier,
            hitLook = hitLook
        )
        PendingHit(hit, delay)
    }
    combat.delayHits(*pendingHits.toTypedArray())
    return pendingHits.map { it.hit }
}

fun SpecialContext.magic(
    accuracyMultiplier: Double = weapon.specialAttack!!.accuracyMultiplier,
    damageMultiplier: Double = weapon.specialAttack!!.damageMultiplier,
    hitLook: Hit.HitLook? = null,
    spell: Spell? = null,
    delay: Int = 0,
    hits: Int = 1
): List<Hit> {
    val pendingHits = (0 until hits).map {
        val hit = registerHit(
            combatType = CombatType.MAGIC,
            accuracyMultiplier = accuracyMultiplier,
            damageMultiplier = damageMultiplier,
            hitLook = hitLook,
            spell = spell
        )
        PendingHit(hit, delay)
    }
    combat.delayHits(*pendingHits.toTypedArray())
    return pendingHits.map { it.hit }
}

class SpecialHitBuilder(private val context: SpecialContext) {
    private val hits = mutableListOf<PendingHit>()
    private val special = context.weapon.specialAttack!!

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

fun SpecialContext.hits(block: SpecialHitBuilder.() -> Unit) {
    val builder = SpecialHitBuilder(this)
    builder.block()
    combat.delayHits(*builder.build().toTypedArray())
}



