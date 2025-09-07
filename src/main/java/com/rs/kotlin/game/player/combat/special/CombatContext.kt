package com.rs.kotlin.game.player.combat.special

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.World
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.range.RangedAmmo
import kotlin.math.max

data class CombatContext(
    val attacker: Player,
    val defender: Entity,
    val weapon: Weapon,
    val weaponId: Int,
    val ammo: RangedAmmo? = null,
    val combat: CombatStyle,
    val attackStyle: AttackStyle,
    val attackBonusType: AttackBonusType,
    val hit: Hit? = null,
    val usingSpecial: Boolean = false
)

private fun CombatContext.resolveMultipliers(
    accuracyMultiplier: Double,
    damageMultiplier: Double
): Pair<Double, Double> {
    val accMul = if (usingSpecial && weapon.special != null) {
        weapon.special!!.accuracyMultiplier
    } else accuracyMultiplier

    val dmgMul = if (usingSpecial && weapon.special != null) {
        weapon.special!!.damageMultiplier
    } else damageMultiplier

    return accMul to dmgMul
}

fun CombatContext.rollHit(
    type: CombatType,
    accuracyMultiplier: Double = 1.0,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    spellId: Int = -1,
    target: Entity = defender
): Hit {
    val (accMul, dmgMul) = resolveMultipliers(accuracyMultiplier, damageMultiplier)
    return registerHit(
        combatType = type,
        accuracyMultiplier = accMul,
        damageMultiplier = dmgMul,
        hitLook = hitLook,
        spellId = spellId,
        target = target
    )
}

fun CombatContext.multiHit(
    type: CombatType,
    hits: Int = 1,
    delay: Int = 0,
    accuracyMultiplier: Double = 1.0,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    spellId: Int = -1,
    target: Entity = defender
): List<Hit> {
    val (accMul, dmgMul) = resolveMultipliers(accuracyMultiplier, damageMultiplier)
    val pending = (0 until hits).map {
        val hit = registerHit(
            combatType = type,
            accuracyMultiplier = accMul,
            damageMultiplier = dmgMul,
            hitLook = hitLook,
            spellId = spellId,
            target = target
        )
        PendingHit(hit, target, delay)
    }
    combat.delayHits(*pending.toTypedArray())
    return pending.map { it.hit }
}


fun CombatContext.rollMelee(acc: Double = 1.0, dmg: Double = 1.0, look: Hit.HitLook? = null) =
    rollHit(CombatType.MELEE, acc, dmg, look)

fun CombatContext.rollRanged(acc: Double = 1.0, dmg: Double = 1.0, look: Hit.HitLook? = null) =
    rollHit(CombatType.RANGED, acc, dmg, look)

fun CombatContext.rollMagic(
    acc: Double = 1.0,
    dmg: Double = 1.0,
    look: Hit.HitLook? = null,
    spellId: Int = -1
) = rollHit(CombatType.MAGIC, acc, dmg, look, spellId)

fun CombatContext.createHit(
    damage: Int,
    combatType: CombatType = CombatType.MELEE,
    hitLook: Hit.HitLook? = null,
    target: Entity = defender
): Hit = combat.addHit(
    damage = damage,
    attacker = attacker,
    defender = target,
    combatType = combatType,
    hitLook = hitLook
)

fun CombatContext.registerHit(
    combatType: CombatType = CombatType.MELEE,
    accuracyMultiplier: Double = 1.0,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    spellId: Int = -1,
    target: Entity = defender
): Hit = combat.registerHit(
    attacker = attacker,
    defender = target,
    combatType = combatType,
    attackStyle = attackStyle,
    weapon = weapon,
    spellId = spellId,
    accuracyMultiplier = accuracyMultiplier,
    damageMultiplier = damageMultiplier,
    hitLook = hitLook
)

fun CombatContext.registerDamage(
    combatType: CombatType = CombatType.MELEE,
    damageMultiplier: Double = 1.0,
    hitLook: Hit.HitLook? = null,
    spellId: Int = -1,
    target: Entity = defender
): Hit = combat.registerDamage(
    attacker = attacker,
    defender = target,
    combatType = combatType,
    attackStyle = attackStyle,
    weapon = weapon,
    spellId = spellId,
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
            ), defender, delays[i]
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
            ), defender, delays[i]
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
        PendingHit(hit, defender, delay)
    }

    combat.delayHits(*pendingHits.toTypedArray())
    return pendingHits.map { it.hit }
}

fun CombatContext.meleeHit(target: Entity = defender, hits: Int = 1, delay: Int = 0, acc: Double = 1.0, dmg: Double = 1.0, look: Hit.HitLook? = null) =
    multiHit(CombatType.MELEE, hits, delay, acc, dmg, look, target = target)

fun CombatContext.rangedHit(target: Entity = defender, hits: Int = 1, delay: Int = 0, acc: Double = 1.0, dmg: Double = 1.0, look: Hit.HitLook? = null) =
    multiHit(CombatType.RANGED, hits, delay, acc, dmg, look, target = target)

fun CombatContext.magicHit(target: Entity = defender, hits: Int = 1, delay: Int = 0, acc: Double = 1.0, dmg: Double = 1.0, look: Hit.HitLook? = null, spellId: Int = -1) =
    multiHit(CombatType.MAGIC, hits, delay, acc, dmg, look, spellId, target = target)

fun CombatContext.getDragonClawsHits(swings: Int = 4): List<Hit> {
    val hits = MutableList(swings) { rollMelee() }//roll for accuracy & damage
    val firstHitIndex = hits.indexOfFirst { it.damage > 0 }
    hits.forEach { it.critical = false }
    val hit = CombatCalculations.calculateMeleeMaxHit(attacker, defender)
    val maxHit = hit.maxHit
    val baseMax = hit.baseMaxHit
    if (firstHitIndex == -1) {//all misses
        if (Math.random() < 2.0 / 3.0) {
            val patterns = listOf(
                intArrayOf(9, 9, 0, 0),//randomise 1-9 for each pattern where damage isnt 0
                intArrayOf(0, 0, 9, 9),
                intArrayOf(9, 0, 9, 0),
                intArrayOf(0, 9, 0, 9)
            )
            val pattern = patterns.random()
            hits.forEachIndexed { i, h ->
                h.damage = if (pattern[i] > 0) Utils.random(1, pattern[i]) else 0
            }
        }
    } else {//not a full miss
        when (firstHitIndex) {
            0 -> {
                val first = ((maxHit / 2).. maxHit - 1).random()
                hits[0].damage = first//if maxHit 400 = 200-400
                hits[1].damage = (hits[0].damage + 1) / 2//half of above
                hits[2].damage = (hits[1].damage + 1) / 2//halv of above
                hits[3].damage = hits[2].damage + 1//same as above
            }
            1 -> {
                val second = ((maxHit * 3 / 8)..(maxHit * 7 / 8)).random()
                hits[1].damage = second//if maxHit 400 = 150-350
                hits[2].damage = (hits[1].damage + 1) / 2//half of above
                hits[3].damage = hits[2].damage//same as above
            }
            2 -> {
                val third = ((maxHit / 4)..(maxHit * 3 / 4)).random()
                hits[2].damage = third//if maxHit 400 = 100-300
                hits[3].damage = hits[2].damage//same as above
            }

            3 -> {
                val fourth = ((maxHit / 4)..(maxHit * 5 / 4)).random()
                hits[3].damage = fourth//if maxHit 400 = 100-500
            }
        }
        val firstNonZero = hits[firstHitIndex]//set critical marks based of first hit in success rolls
        if (firstNonZero.checkCritical(firstNonZero.damage, baseMax)) {
            for (i in firstHitIndex until hits.size) {
                hits[i].critical = true
            }
        }
    }
    return hits
}

fun Entity.withinDistanceOf(other: Entity, distance: Int): Boolean {
    val thisCenterX = this.x + this.size / 2.0
    val thisCenterY = this.y + this.size / 2.0
    val otherCenterX = other.x + other.size / 2.0
    val otherCenterY = other.y + other.size / 2.0

    val dx = thisCenterX - otherCenterX
    val dy = thisCenterY - otherCenterY

    val effectiveDist = max(
        kotlin.math.abs(dx) - (this.size + other.size) / 2.0,
        kotlin.math.abs(dy) - (this.size + other.size) / 2.0
    )

    val result = effectiveDist <= distance
    return result
}

fun CombatContext.getMultiAttackTargets(
    maxDistance: Int,
    maxTargets: Int
): List<Entity> {
    val possibleTargets = mutableListOf<Entity>()
    val attacker = this.attacker
    val target = this.defender
    if (target is NPC)
        println("[DEBUG] MultiAttack: attacker=${attacker.displayName}, target=${target.id}, maxDist=$maxDistance, maxTargets=$maxTargets")

    possibleTargets.add(target)
    target.checkMultiArea()
    if (!target.isAtMultiArea && !target.isForceMultiArea) {
        println("[DEBUG] Target is not in multi area, returning single target.")
        return possibleTargets
    }

    val regions = target.mapRegionsIds
    if (target is NPC)
        println("[DEBUG] Regions around target=${target.id} -> $regions")

    regionLoop@ for (regionId in regions) {
        val region = World.getRegion(regionId) ?: continue

        when (target) {
            is Player -> {
                val playerIndexes = region.playerIndexes ?: continue
                for (playerIndex in playerIndexes) {
                    val p2 = World.getPlayers().get(playerIndex) ?: continue
                    when {
                        p2 == attacker -> println("[DEBUG] Reject: same as attacker")
                        p2 == target -> println("[DEBUG] Reject: same as main target")
                        p2.isDead -> println("[DEBUG] Reject: player dead")
                        !p2.hasStarted() -> println("[DEBUG] Reject: player not started")
                        p2.hasFinished() -> println("[DEBUG] Reject: player finished")
                        !p2.canPvp -> println("[DEBUG] Reject: cannot pvp")
                        !p2.isAtMultiArea -> println("[DEBUG] Reject: not in multi area")
                        !p2.withinDistanceOf(target, maxDistance) -> println("[DEBUG] Reject: too far from target")
                        !attacker.controlerManager.canHit(p2) -> println("[DEBUG] Reject: cannot hit")
                        possibleTargets.size >= maxTargets -> {
                            println("[DEBUG] Reject: already at maxTargets")
                            break@regionLoop
                        }
                        else -> {
                            possibleTargets.add(p2)
                        }
                    }
                }
            }

            is NPC -> {
                val npcIndexes = region.npCsIndexes ?: continue
                for (npcIndex in npcIndexes) {
                    val n = World.getNPCByIndex(npcIndex) ?: continue
                    println("[DEBUG] Checking candidate npc=${n.id} near target=${target.id}")
                    when {
                        n == target -> println("[DEBUG] Reject: same as main target")
                        n == attacker.familiar -> println("[DEBUG] Reject: is familiar")
                        n.isDead -> println("[DEBUG] Reject: npc dead")
                        n.hasFinished() -> println("[DEBUG] Reject: npc finished")
                        !n.isAtMultiArea && !n.isForceMultiAttacked -> println("[DEBUG] Reject: not in multi area")
                        !n.withinDistanceOf(target, maxDistance) -> println("[DEBUG] Reject: too far from target")
                        !n.definitions.hasAttackOption() -> println("[DEBUG] Reject: no attack option")
                        !attacker.controlerManager.canHit(n) -> println("[DEBUG] Reject: cannot hit")
                        possibleTargets.size >= maxTargets -> {
                            println("[DEBUG] Reject: already at maxTargets")
                            break@regionLoop
                        }
                        else -> {
                            println("[DEBUG] ACCEPT npc=${n.id}")
                            possibleTargets.add(n)
                        }
                    }
                }
            }

            else -> {
                println("[DEBUG] Target type not handled: ${target::class.simpleName}")
                break@regionLoop
            }
        }
    }

    return possibleTargets
}



class SpecialHitBuilder(private val context: CombatContext) {
    private val hits = mutableListOf<PendingHit>()
    private val special = context.weapon.special
    private val effect = context.weapon.effect


    fun addHit(
        defender: Entity = context.defender,
        damage: Int,
        look: Hit.HitLook? = null,
        type: CombatType = CombatType.MELEE,
        delay: Int = 0
    ): Hit {
        val resolvedHitLook = look ?: when (type) {
            CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
            CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
            CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
        }
        val hit = Hit(context.attacker, damage, resolvedHitLook)
        hits += PendingHit(hit, defender, delay);
        return hit
    }

    fun addHit(
        defender: Entity = context.defender,
        hit: Hit,
        look: Hit.HitLook? = null,
        delay: Int = 0
    ): Hit {
        val resolvedHitLook = look ?: hit.look
        hit.look = resolvedHitLook
        hits += PendingHit(hit, defender, delay);
        return hit
    }

    fun createHit(
        type: CombatType = CombatType.MELEE,
        damageMultiplier: Double = 1.0,
        accuracyMultiplier: Double = 1.0,
        delay: Int = 0,
        spellId: Int = -1
    ): Hit {
        val accMultiplier = special?.takeIf { context.usingSpecial && it.accuracyMultiplier > 1.0 }?.accuracyMultiplier
            ?: accuracyMultiplier

        val dmgMultiplier = special?.takeIf { context.usingSpecial && it.damageMultiplier > 1.0 }?.damageMultiplier
            ?: damageMultiplier

        val h = context.registerHit(
            combatType = type, spellId = spellId, accuracyMultiplier = accMultiplier, damageMultiplier = dmgMultiplier
        )
        hits += PendingHit(h, context.defender, delay)
        return h
    }

    fun melee(
        damageMultiplier: Double = special?.damageMultiplier ?: 1.0,
        accuracyMultiplier: Double = special?.accuracyMultiplier ?: 1.0,
        delay: Int = 0
    ) = createHit(CombatType.MELEE, damageMultiplier, accuracyMultiplier, delay)

    fun ranged(
        damageMultiplier: Double = 1.0,
        accuracyMultiplier: Double = 1.0,
        delay: Int = 0
    ) = createHit(CombatType.RANGED, damageMultiplier, accuracyMultiplier, delay)

    fun magic(
        damageMultiplier: Double = 1.0,
        accuracyMultiplier: Double = 1.0,
        delay: Int = 0,
        spellId: Int
    ) = createHit(CombatType.MAGIC, spellId = spellId, damageMultiplier = damageMultiplier, accuracyMultiplier =  accuracyMultiplier, delay = delay)

    fun CombatContext.applyBleed(
        baseHit: Hit,
        bleedPercent: Double = 0.75,
        maxTickDamage: Int = 50,
        initialDelay: Int = 1,
        tickInterval: Int = 1
    ) {
        if (baseHit.damage <= 0) return

        var remainingBleed = (baseHit.damage * bleedPercent).toInt()
        var delay = initialDelay

        while (remainingBleed > 0) {
            val tickDamage = minOf(maxTickDamage, remainingBleed)
            remainingBleed -= tickDamage

            this.hits {
                nextHit(Hit(baseHit.source, tickDamage, baseHit.look), delay = delay + tickInterval)
            }
            delay += tickInterval
        }
    }


    fun nextHit(
        baseHit: Hit, scale: Double = 1.0, delay: Int = 0
    ): Hit {
        val newHit = Hit(baseHit.source, (baseHit.damage * scale).toInt(), baseHit.look)
        hits += PendingHit(newHit, context.defender, delay)
        return newHit
    }

    fun nextHit(
        baseHit: Hit, maxHit: Int, delay: Int = 0
    ): Hit {
        val newHit = baseHit.copyWithDamage(Utils.random(maxHit))
        if (!newHit.landed)
            newHit.damage = 0
        hits += PendingHit(newHit, context.defender, delay)
        return newHit
    }

    internal fun build(): List<PendingHit> = hits
}

fun CombatContext.hits(block: SpecialHitBuilder.() -> Unit) {
    val builder = SpecialHitBuilder(this)
    builder.block()
    combat.delayHits(*builder.build().toTypedArray())
}



