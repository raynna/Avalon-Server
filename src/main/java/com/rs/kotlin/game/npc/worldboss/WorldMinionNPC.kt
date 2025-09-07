package com.rs.kotlin.game.npc.worldboss

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import kotlin.math.max

open class WorldMinionNPC : NPC {

    private val idleTimeoutMs: Long
    private val gracePeriodMs: Long
    private val handler: RandomWorldBossHandler

    companion object {
        private const val SCALE_RADIUS = 32
        private const val RECHECK_INTERVAL = 10
    }

    @Volatile private var lastInteractionMs = Utils.currentTimeMillis()
    @Volatile private var spawnTimeMs = lastInteractionMs
    @Volatile private var externallyDespawning: String? = null
    private var ticksUntilRecheck = RECHECK_INTERVAL
    private val baseHp: Int
    private var firstProcess = true

    constructor(
        id: Int,
        tile: WorldTile,
        idleTimeoutMs: Long,
        gracePeriodMs: Long,
        handler: RandomWorldBossHandler,
    ) : super(id, tile, -1, true, true) {
        this.idleTimeoutMs = idleTimeoutMs
        this.gracePeriodMs = gracePeriodMs
        this.handler = handler
        this.baseHp = maxHitpoints * 3
        this.forceTargetDistance = 64
        this.forceAgressiveDistance = 16
        this.isForceMultiAttacked = true
        this.isForceMultiArea = true
        this.setBonuses()
    }

    override fun processEntity() {
        super.processEntity()

        if (firstProcess) {
            setBonuses()
            recalcHpScaling(force = true)
            firstProcess = false
        }

        ticksUntilRecheck--
        if (ticksUntilRecheck <= 0) {
            recalcHpScaling()
            ticksUntilRecheck = RECHECK_INTERVAL
        }
        if (!hasFinished() && combat != null && combat.attackDelay == 0) {
            selectNewTarget()
        }
    }

    private fun recalcHpScaling(force: Boolean = false) {
        val nearbyPlayers = World.getPlayers().count {
            it != null && !it.hasFinished() && it.withinDistance(this, SCALE_RADIUS)
        }

        val scale = 1.0 + (nearbyPlayers * 0.05)
        val newMaxHp = max(1, (baseHp * scale).toInt())

        if (newMaxHp != maxHitpoints || force) {
            val oldMaxHp = maxHitpoints.takeIf { it > 0 } ?: newMaxHp
            val hpRatio = hitpoints.toDouble() / oldMaxHp

            increasedMaxHitpoints = newMaxHp
            hitpoints = max(1, max(hitpoints, (newMaxHp * hpRatio).toInt()))
        }
    }

    override fun handleIncommingHit(hit: Hit) {
        super.handleIncommingHit(hit)
    }

    override fun getProtectionPrayerEffectiveness(): Double {
        return 0.1
    }

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        handler.onMinionDeath(this)
    }

    override fun finish() {
        super.finish()
    }

    private fun selectNewTarget() {
        val possibleTargets = possibleTargets
            .filterIsInstance<Player>()
            .filter { !it.hasFinished() && !it.isDead && withinDistance(it, forceTargetDistance) }

        if (possibleTargets.isNotEmpty()) {
            val newTarget = possibleTargets.random()
            if (newTarget != combat.target) {
                combat.setTarget(newTarget)
            }
        }
    }
}
