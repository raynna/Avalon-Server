package com.rs.kotlin.game.player.combat.special

import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.World
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.CombatHitRoll
import com.rs.kotlin.game.player.combat.damage.HitBuilder
import com.rs.kotlin.game.player.combat.damage.HitRequest
import com.rs.kotlin.game.player.combat.damage.HitRoller
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.damage.ProcHitBuilder
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

fun CombatContext.addHit(type: CombatType, target: Entity = defender): HitBuilder {
    return HitBuilder(this, type, target)
}

fun hitRoll(type: CombatType, attacker: Player, target: Entity): HitRoller {
    return HitRoller(attacker, target, type)
}

fun CombatContext.procHit(target: Entity = defender): ProcHitBuilder {
    return ProcHitBuilder(this, target)
}

fun CombatContext.procHit(attacker: Player, target: Entity = defender): ProcHitBuilder {
    return ProcHitBuilder(this, target)
}

fun CombatContext.multiHit(
    targets: List<Entity>,
    build: (target: Entity) -> HitBuilder
) {
    for (target in targets) {
        build(target).roll(CombatHitRoll.NORMAL)
    }
}

fun CombatContext.addDerivedHit(
    source: Hit,
    target: Entity,
    damage: Int,
    delay: Int = 0
): Hit {
    val derived = source.copyWithDamage(damage)
    combat.delayHits(PendingHit(derived, target, delay))
    return derived
}



fun CombatContext.applyBleed(
    baseHit: Hit,
    bleedPercent: Double = 0.75,
    maxTickDamage: Int = 50,
    initialDelay: Int = 1,
    tickInterval: Int = 1
) {
    if (baseHit.damage <= 0) return

    var remaining = (baseHit.damage * bleedPercent).toInt()
    var delay = initialDelay

    while (remaining > 0) {
        val tickDamage = minOf(maxTickDamage, remaining)
        remaining -= tickDamage

        combat.delayHits(
            PendingHit(
                Hit(
                    attacker,
                    tickDamage,
                    Hit.HitLook.REGULAR_DAMAGE // or BLEED_DAMAGE
                ),
                defender,
                delay
            )
        )

        delay += tickInterval
    }
}

fun CombatContext.getDragonClawsHits(swings: Int = 4): List<Hit> {
    val hits = MutableList(swings) { hitRoll(CombatType.MELEE, attacker, defender).roll() }//roll for accuracy & damage
    val firstHitIndex = hits.indexOfFirst { it.damage > 0 }
    hits.forEach { it.critical = false }
    val maxValues = CombatCalculations.getMeleeMaxHit(attacker, defender)
    val maxHit = maxValues.max
    val baseMax = maxValues.base
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
        var hit = addHit(CombatType.MELEE).roll()
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
            val maxCheck = CombatCalculations.getMeleeMaxHit(attacker, victim)
            if (firstNonZero.checkCritical(firstNonZero.damage, maxCheck.base)) {
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
    additionalTargets: Int,
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
        bouncesLeft = additionalTargets,
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
    bounceIndex: Int
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
    val hit = hitRoll(calcType, context.attacker, target).damageMultiplier(damageMultiplier).roll()

    hit.look = when (displayType) {
        CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
        CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
        CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
    }

    val useProjectile = if (isFirstHit) projectile else settings.projectile
    val useProjectileId = if (isFirstHit) projectileId else settings.projectileId

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
                context = context,
                settings = settings,
                source = target,
                target = next,
                previousTarget = target,
                rootTarget = rootTarget,
                projectile = settings.projectile,
                projectileId = settings.projectileId,
                endGraphicsId = endGraphicsId,
                deathSpreadUsed = deathSpreadUsed,
                bouncesLeft = if (chainMode == ChainMode.SPREAD_ALL) 0 else bouncesLeft - 1,
                deathSpreadAmount = deathSpreadAmount,
                bounceRange = bounceRange,
                chainMode = chainMode,
                isFirstHit = false,
                bounceIndex = bounceIndex + 1
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
            delayOffset = 0,
            hitGraphic = if (settings.projectileEnd != -1) Graphics(settings.projectileEnd) else null
        ) { remainder ->
            continueChain(remainder)
        }

    val impactTicks = result.impactTicks

    context.combat.delayHits(
        PendingHit(
            hit,
            target,
            (impactTicks - 1).coerceAtLeast(0)
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
                        context = context,
                        settings = settings,
                        source = target,
                        target = next,
                        previousTarget = target,
                        rootTarget = rootTarget,
                        projectile = useProjectile,
                        projectileId = useProjectileId,
                        endGraphicsId = settings.projectileEnd,
                        deathSpreadUsed = deathSpreadUsed,
                        bouncesLeft = if (chainMode == ChainMode.SPREAD_ALL) 0 else deathSpreadAmount - 1,
                        deathSpreadAmount = deathSpreadAmount,
                        bounceRange = bounceRange,
                        chainMode = chainMode,
                        isFirstHit = false,
                        bounceIndex = bounceIndex + 1
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
            val player: Entity = source
            for (e in World.getEntitiesAt(tile)) {
                if (e == attacker) continue
                if (excludeRoot && e == rootTarget) continue
                if (e == source) continue
                if (e in pickedThisSpread) continue
                if (e.isDead || e.hasFinished()) continue
                if (!attacker.controlerManager.canHit(e)) continue
                if (!canChainReach(source, e, bounceRange)) continue
                if (!e.isAtMultiArea && !e.isForceMultiArea) continue
                if (e is NPC) {
                    if (e.owner != null && e.owner.familiar == e) continue
                }

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
    val dir = (rawDir / 2048) and 0x7

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

    possibleTargets.add(target)

    target.checkMultiArea()
    if (!target.isAtMultiArea && !target.isForceMultiArea) {
        return possibleTargets
    }

    val regions = target.mapRegionsIds

    regionLoop@ for (regionId in regions) {
        val region = World.getRegion(regionId) ?: continue

        when (target) {

            is Player -> {
                val playerIndexes = region.playerIndexes ?: continue

                for (playerIndex in playerIndexes) {
                    val p2 = World.getPlayers()[playerIndex] ?: continue

                    if (
                        p2 == attacker ||
                        p2 == target ||
                        p2.isDead ||
                        !p2.hasStarted() ||
                        p2.hasFinished() ||
                        !p2.canPvp ||
                        !p2.isAtMultiArea ||
                        !p2.withinDistance(target, maxDistance) ||
                        !attacker.controlerManager.canHit(p2)
                    ) continue

                    possibleTargets.add(p2)

                    if (possibleTargets.size >= maxTargets)
                        break@regionLoop
                }
            }

            is NPC -> {
                val npcIndexes = region.npCsIndexes ?: continue

                for (npcIndex in npcIndexes) {
                    val n = World.getNPCByIndex(npcIndex) ?: continue

                    if (
                        n == target ||
                        n == attacker.familiar ||
                        n.isDead ||
                        n.hasFinished() ||
                        (!n.isAtMultiArea && !n.isForceMultiAttacked) ||
                        !n.withinDistance(target, maxDistance) ||
                        !n.definitions.hasAttackOption() ||
                        !attacker.controlerManager.canHit(n)
                    ) continue

                    possibleTargets.add(n)

                    if (possibleTargets.size >= maxTargets)
                        break@regionLoop
                }
            }

            else -> break@regionLoop
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
        hit: Hit,
        look: Hit.HitLook? = null,
        delay: Int = 0
    ): Hit {
        val resolvedHitLook = look ?: hit.look
        hit.look = resolvedHitLook
        hits += PendingHit(hit, defender, delay)
        return hit
    }

    fun nextHit(
        baseHit: Hit, scale: Double = 1.0, delay: Int = 0
    ): Hit {
        val newHit = Hit(baseHit.source, (baseHit.damage * scale).toInt(), baseHit.look)
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



