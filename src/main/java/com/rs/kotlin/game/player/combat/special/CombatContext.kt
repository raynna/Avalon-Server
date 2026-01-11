package com.rs.kotlin.game.player.combat.special

import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.World
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.range.RangedAmmo
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

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
    val usingSpecial: Boolean = false,
    val guaranteedBoltEffect: Boolean = false
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
    baseDamage: Int = -1,
    spellId: Int = -1,
    target: Entity = defender
): Hit {
    val (accMul, dmgMul) = resolveMultipliers(accuracyMultiplier, damageMultiplier)
    return registerHit(
        combatType = type,
        accuracyMultiplier = accMul,
        damageMultiplier = dmgMul,
        hitLook = hitLook,
        baseDamage = baseDamage,
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
    baseDamage: Int = -1,
    spellId: Int = -1
) = rollHit(CombatType.MAGIC, acc, dmg, look, baseDamage, spellId)

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
    baseDamage: Int = -1,
    spellId: Int = -1,
    target: Entity = defender
): Hit = combat.registerHit(
    attacker = attacker,
    defender = target,
    combatType = combatType,
    attackStyle = attackStyle,
    weapon = weapon,
    baseDamage = baseDamage,
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
                val first = if (defender is NPC && defender.id == 4474) maxHit - 1 else ((maxHit / 2).. maxHit - 1).random()
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

fun CombatContext.applyScytheHits(victim: Entity) {
    val attacker = this.attacker

    val scytheHits = mutableListOf<Hit>()

    hits {
        var hit = rollMelee()
        hit = nextHit(baseHit = hit)
        scytheHits += hit

        if (victim.size > 1 || (victim is NPC && victim.name.contains("dummy", ignoreCase = true))) {
            hit = nextHit(baseHit = hit, scale = 0.5)
            scytheHits += hit
        }

        if (victim.size > 2 || (victim is NPC && victim.name.contains("dummy", ignoreCase = true))) {
            hit = nextHit(baseHit = hit, scale = 0.5, delay = 1)
            scytheHits += hit
        }
    }

    if (scytheHits.isNotEmpty()) {
        val firstNonZero = scytheHits.firstOrNull { it.damage > 0 }
        if (firstNonZero != null) {
            val maxCheck = CombatCalculations.calculateMeleeMaxHit(attacker, victim)
            if (firstNonZero.checkCritical(firstNonZero.damage, maxCheck.baseMaxHit)) {
                for (i in scytheHits.indexOf(firstNonZero) until scytheHits.size) {
                    scytheHits[i].critical = true
                }
            }
        }
    }
}

fun CombatContext.startChainAttack(
    settings: ChainSettings,
    animationId: Int = -1,
    graphicsId: Int = -1,
    projectile: Projectile = Projectile.ARROW,
    projectileId: Int = -1,
    endGraphicsId: Int = -1,
    maxTargets: Int,
    bounceRange: Int
) {
    val deathSpreadUsed = mutableSetOf<Entity>()

    attacker.animate(animationId)
    attacker.gfx(graphicsId, 100)

    fireChain(
        context = this,
        settings = settings,
        source = attacker,
        target = defender,
        previousTarget = null,
        rootTarget = defender,
        projectile = projectile,
        projectileId = projectileId,
        endGraphicsId = endGraphicsId,
        deathSpreadUsed = deathSpreadUsed,
        bouncesLeft = maxTargets,
        deathSpreadAmount = settings.deathSpreadAmount,
        bounceRange = bounceRange,
        chainMode = settings.chainMode,
        isFirstHit = true,
        bounceIndex = 0
    )
}




fun fireChain(
    context: CombatContext,
    settings: ChainSettings,
    source: Entity,
    target: Entity,
    previousTarget: Entity?,
    rootTarget: Entity,
    projectile: Projectile,
    projectileId: Int,
    endGraphicsId: Int,
    deathSpreadUsed: MutableSet<Entity>,
    bouncesLeft: Int,
    deathSpreadAmount: Int,
    bounceRange: Int,
    chainMode: ChainMode,
    isFirstHit: Boolean,
    bounceIndex: Int,
    startDelay: Int = 0
) {

    if (deathSpreadUsed.size > 500) return

    val calcType = settings.firstCombatType
    val displayType =
        if (isFirstHit) settings.firstCombatType
        else settings.spreadCombatType

    val damageMultiplier = when (settings.damageScaleMode) {
        DamageScaleMode.ABSOLUTE ->
            if (isFirstHit) 1.0 else settings.damageMultiplier
        DamageScaleMode.PER_BOUNCE ->
            Math.pow(settings.damageMultiplier, bounceIndex.toDouble())
    }

    val hit = context.registerHit(
        combatType = calcType,
        target = target,
        damageMultiplier = damageMultiplier
    )

    hit.look = when (displayType) {
        CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
        CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
        CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
    }

    val useProjectile = if (isFirstHit) projectile else settings.projectile
    val useProjectileId = if (isFirstHit) projectileId else settings.projectileId

    // ---- helper to continue chain ----
    fun continueChain(remainder: Int) {
        if (bouncesLeft <= 0) return

        val pickedThisSpread = mutableSetOf<Entity>()

        val nextTargets = context.findChainTargets(
            source = target,
            rootTarget = rootTarget,
            previousTarget = previousTarget,
            pickedThisSpread = pickedThisSpread,
            bounceRange = bounceRange,
            chainMode = chainMode,
            maxPicks = bouncesLeft,
            excludeRoot = isFirstHit
        )

        nextTargets.forEach { next ->
            fireChain(
                context,
                settings,
                target,
                next,
                target,
                rootTarget,
                settings.projectile,
                settings.projectileId,
                endGraphicsId,
                deathSpreadUsed,
                if (chainMode == ChainMode.SPREAD_ALL) 0 else bouncesLeft - 1,
                deathSpreadAmount,
                bounceRange,
                chainMode,
                false,
                bounceIndex + 1,
                remainder
            )
        }
    }

    val result =
        if (isFirstHit && calcType == CombatType.MELEE) {
            continueChain(0)
            ProjectileManager.ProjectileResult(0, 0)
        } else ProjectileManager.sendResult(
            projectile = useProjectile,
            gfxId = useProjectileId,
            attacker = source,
            defender = target,
            delayOffset = startDelay,
            hitGraphic = if (settings.projectileEnd != -1) Graphics(settings.projectileEnd) else null
        ) { remainder ->
            continueChain(remainder)
        }

    val impactTicks = result.impactTicks

    context.combat.delayHits(
        PendingHit(
            hit,
            target,
            impactTicks
        ) {

            if (hit.damage <= 0) return@PendingHit

            val died = hit.damage >= target.hitpoints

            if (
                !isFirstHit &&
                died &&
                settings.deathSpread &&
                target !in deathSpreadUsed
            ) {

                deathSpreadUsed += target

                val pickedThisDeathSpread = mutableSetOf<Entity>()

                val deathTargets = context.findChainTargets(
                    source = target,
                    rootTarget = rootTarget,
                    previousTarget = previousTarget,
                    pickedThisSpread = pickedThisDeathSpread,
                    bounceRange = bounceRange,
                    chainMode = chainMode,
                    maxPicks = deathSpreadAmount,
                    excludeRoot = false
                )

                deathTargets.forEach { next ->
                    fireChain(
                        context,
                        settings,
                        target,
                        next,
                        target,
                        rootTarget,
                        useProjectile,
                        useProjectileId,
                        settings.projectileEnd,
                        deathSpreadUsed,
                        if (chainMode == ChainMode.SPREAD_ALL) 0 else deathSpreadAmount - 1,
                        deathSpreadAmount,
                        bounceRange,
                        chainMode,
                        false,
                        bounceIndex + 1,
                        0
                    )
                }
            }
        }
    )
}




fun CombatContext.findChainTargets(
    source: Entity,
    rootTarget: Entity,
    previousTarget: Entity?,
    pickedThisSpread: MutableSet<Entity>,
    bounceRange: Int,
    chainMode: ChainMode,
    maxPicks: Int,
    excludeRoot: Boolean
): List<Entity> {

    val attacker = this.attacker
    val candidates = mutableListOf<Pair<Entity, Int>>()

    for (dx in -bounceRange..bounceRange) {
        for (dy in -bounceRange..bounceRange) {

            val distSq = dx * dx + dy * dy
            if (distSq > bounceRange * bounceRange) continue

            val tile = source.tile.transform(dx, dy, 0)

            for (e in World.getEntitiesAt(tile)) {
                if (e == attacker) continue
                if (excludeRoot && e == rootTarget) continue
                if (e == source) continue
                if (e in pickedThisSpread) continue
                if (e.isDead || e.hasFinished()) continue
                if (!attacker.controlerManager.canHit(e)) continue
                if (!canChainReach(source, e, bounceRange)) continue
                if (!e.isAtMultiArea && !e.isForceMultiArea) continue

                candidates += e to distSq
            }
        }
    }

    if (candidates.isEmpty()) return emptyList()

    return when (chainMode) {

        ChainMode.NEAREST -> {
            val chosen = candidates.minBy { it.second }.first
            pickedThisSpread += chosen
            listOf(chosen)
        }

        ChainMode.FARTHEST -> {
            val chosen = candidates.maxBy { it.second }.first
            pickedThisSpread += chosen
            listOf(chosen)
        }

        ChainMode.ORDERED -> {
            candidates.sortedBy { it.second }
                .map { it.first }
                .filter { pickedThisSpread.add(it) }
                .take(maxPicks)
        }

        ChainMode.RANDOM_NEARBY -> {
            val nearby = candidates.sortedBy { it.second }.take(4)
            val chosen = nearby.random().first
            pickedThisSpread += chosen
            listOf(chosen)
        }

        ChainMode.SPREAD_ALL -> {
            candidates
                .shuffled()
                .map { it.first }
                .filter { pickedThisSpread.add(it) }
                .take(maxPicks)
        }
    }
}



fun canChainReach(source: Entity, target: Entity, maxDistance: Int): Boolean {
    if (!source.clipedProjectile(target, false))
        return false

    val size = source.size
    val dx = target.x - source.x
    val dy = target.y - source.y

    return dx <= size + maxDistance &&
            dx >= -1 - maxDistance &&
            dy <= size + maxDistance &&
            dy >= -1 - maxDistance
}

fun CombatContext.getScytheTargets(
    maxTargets: Int = 3
): List<Entity> {
    val attacker = this.attacker
    val target = this.defender
    val possibleTargets = mutableListOf<Entity>()
    possibleTargets.add(target)

    val baseTile = attacker.tile
    val rawDir = attacker.direction
    val dir = (rawDir / 2048) and 0x7 // normalize 0–7
    //println("[DEBUG] Attacker=${attacker.displayName} rawDir=$rawDir normalizedDir=$dir tile=$baseTile")

    val arcTiles = when (dir) {
        0 -> listOf( // South
            baseTile.transform(0, -1, 0),
            baseTile.transform(-1, -1, 0),
            baseTile.transform(1, -1, 0)
        )
        1 -> listOf( // South-West
            baseTile.transform(-1, -1, 0),
            baseTile.transform(-1, 0, 0),
            baseTile.transform(0, -1, 0)
        )
        2 -> listOf( // West
            baseTile.transform(-1, 0, 0),
            baseTile.transform(-1, -1, 0),
            baseTile.transform(-1, 1, 0)
        )
        3 -> listOf( // North-West
            baseTile.transform(-1, 1, 0),
            baseTile.transform(-1, 0, 0),
            baseTile.transform(0, 1, 0)
        )
        4 -> listOf( // North
            baseTile.transform(0, 1, 0),
            baseTile.transform(-1, 1, 0),
            baseTile.transform(1, 1, 0)
        )
        5 -> listOf( // North-East
            baseTile.transform(1, 1, 0),
            baseTile.transform(1, 0, 0),
            baseTile.transform(0, 1, 0)
        )
        6 -> listOf( // East
            baseTile.transform(1, 0, 0),
            baseTile.transform(1, -1, 0),
            baseTile.transform(1, 1, 0)
        )
        7 -> listOf( // South-East
            baseTile.transform(1, -1, 0),
            baseTile.transform(0, -1, 0),
            baseTile.transform(1, 0, 0)
        )
        else -> emptyList()
    }


    //println("[DEBUG] ArcTiles=$arcTiles")

    for (tile in arcTiles) {
        if (possibleTargets.size >= maxTargets) break

        val entitiesHere = World.getEntitiesAt(tile)
        //println("[DEBUG] Checking tile=$tile entities=${entitiesHere.size}")

        for (entity in entitiesHere) {
            //println("[DEBUG] Candidate entity=${entity} at $tile")

            if (entity == attacker) {
                //println("[DEBUG] Reject: is attacker")
                continue
            }
            if (entity == target) {
                //println("[DEBUG] Reject: is main target")
                continue
            }
            if (entity.isDead || entity.hasFinished()) {
                //println("[DEBUG] Reject: dead/finished")
                continue
            }
            if (!attacker.controlerManager.canHit(entity)) {
                //println("[DEBUG] Reject: cannot hit")
                continue
            }
            if (!entity.isAtMultiArea && !entity.isForceMultiArea) {
                //println("[DEBUG] Reject: not in multi area")
                continue
            }

            //println("[DEBUG] ACCEPT entity=${entity}")
            possibleTargets.add(entity)
            if (possibleTargets.size >= maxTargets) break
        }
    }

    //println("[DEBUG] Final targets=${possibleTargets.map { it.toString() }}")
    return possibleTargets
}

fun CombatContext.getMultiAttackTargets(
    maxDistance: Int,
    maxTargets: Int
): List<Entity> {
    val possibleTargets = mutableListOf<Entity>()
    val attacker = this.attacker
    val target = this.defender
    //if (target is NPC)
    //    println("[DEBUG] MultiAttack: attacker=${attacker.displayName}, target=${target.id}, maxDist=$maxDistance, maxTargets=$maxTargets")

    possibleTargets.add(target)
    target.checkMultiArea()
    if (!target.isAtMultiArea && !target.isForceMultiArea) {
        //  println("[DEBUG] Target is not in multi area, returning single target.")
        return possibleTargets
    }

    val regions = target.mapRegionsIds
    //if (target is NPC)
    //    println("[DEBUG] Regions around target=${target.id} -> $regions")

    regionLoop@ for (regionId in regions) {
        val region = World.getRegion(regionId) ?: continue

        when (target) {
            is Player -> {
                val playerIndexes = region.playerIndexes ?: continue
                for (playerIndex in playerIndexes) {
                    val p2 = World.getPlayers().get(playerIndex) ?: continue
                    when {
                        //p2 == attacker -> println("[DEBUG] Reject: same as attacker")
                        //p2 == target -> println("[DEBUG] Reject: same as main target")
                        //p2.isDead -> println("[DEBUG] Reject: player dead")
                        //!p2.hasStarted() -> println("[DEBUG] Reject: player not started")
                        //p2.hasFinished() -> println("[DEBUG] Reject: player finished")
                        //!p2.canPvp -> println("[DEBUG] Reject: cannot pvp")
                        //!p2.isAtMultiArea -> println("[DEBUG] Reject: not in multi area")
                        //!p2.withinDistanceOf(target, maxDistance) -> println("[DEBUG] Reject: too far from target")
                        //!attacker.controlerManager.canHit(p2) -> println("[DEBUG] Reject: cannot hit")
                        possibleTargets.size >= maxTargets -> {
                            //println("[DEBUG] Reject: already at maxTargets")
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
                    //println("[DEBUG] Checking candidate npc=${n.id} near target=${target.id}")
                    when {
                        // n == target -> println("[DEBUG] Reject: same as main target")
                        // n == attacker.familiar -> println("[DEBUG] Reject: is familiar")
                        // n.isDead -> println("[DEBUG] Reject: npc dead")
                        // n.hasFinished() -> println("[DEBUG] Reject: npc finished")
                        //!n.isAtMultiArea && !n.isForceMultiAttacked -> println("[DEBUG] Reject: not in multi area")
                        //!n.withinDistanceOf(target, maxDistance) -> println("[DEBUG] Reject: too far from target")
                        //!n.definitions.hasAttackOption() -> println("[DEBUG] Reject: no attack option")
                        // !attacker.controlerManager.canHit(n) -> println("[DEBUG] Reject: cannot hit")
                        possibleTargets.size >= maxTargets -> {
                            //  println("[DEBUG] Reject: already at maxTargets")
                            break@regionLoop
                        }
                        else -> {
                            // println("[DEBUG] ACCEPT npc=${n.id}")
                            possibleTargets.add(n)
                        }
                    }
                }
            }

            else -> {
                //println("[DEBUG] Target type not handled: ${target::class.simpleName}")
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
        hits += PendingHit(hit, defender, delay)
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
        hits += PendingHit(hit, defender, delay)
        return hit
    }

    fun createHit(
        type: CombatType = CombatType.MELEE,
        damageMultiplier: Double = 1.0,
        accuracyMultiplier: Double = 1.0,
        delay: Int = 0,
        baseDamage: Int = -1,
        spellId: Int = -1
    ): Hit {
        val accMultiplier = special?.takeIf { context.usingSpecial && it.accuracyMultiplier > 1.0 }?.accuracyMultiplier
            ?: accuracyMultiplier

        val dmgMultiplier = special?.takeIf { context.usingSpecial && it.damageMultiplier > 1.0 }?.damageMultiplier
            ?: damageMultiplier

        val h = context.registerHit(
            combatType = type, baseDamage = baseDamage, spellId = spellId, accuracyMultiplier = accMultiplier, damageMultiplier = dmgMultiplier
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
        spellId: Int = -1,
        baseDamage: Int = -1
    ) = createHit(CombatType.MAGIC, spellId = spellId, baseDamage = baseDamage, damageMultiplier = damageMultiplier, accuracyMultiplier =  accuracyMultiplier, delay = delay)



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



