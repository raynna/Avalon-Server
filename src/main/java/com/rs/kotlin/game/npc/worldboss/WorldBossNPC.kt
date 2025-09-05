package com.rs.kotlin.game.npc.worldboss

import com.rs.Settings
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import kotlin.math.max

open class WorldBossNPC : NPC {

    private val idleTimeoutMs: Long
    private val gracePeriodMs: Long
    private val handler: RandomWorldBossHandler
    private val isMinion: Boolean

    companion object {
        private const val SCALE_RADIUS = 32
        private const val RECHECK_INTERVAL = 10
    }

    @Volatile private var lastInteractionMs = Utils.currentTimeMillis()
    @Volatile private var spawnTimeMs = lastInteractionMs
    @Volatile private var externallyDespawning: String? = null
    private var ticksUntilRecheck = RECHECK_INTERVAL
    private val baseHp: Int
    private var firstProcess = true   // 🔑 new flag

    constructor(
        id: Int,
        tile: WorldTile,
        idleTimeoutMs: Long,
        gracePeriodMs: Long,
        handler: RandomWorldBossHandler,
        isMinion: Boolean = false
    ) : super(id, tile, -1, true, true) {
        this.idleTimeoutMs = idleTimeoutMs
        this.gracePeriodMs = gracePeriodMs
        this.handler = handler
        this.isMinion = isMinion
        this.baseHp = maxHitpoints
    }

    override fun processEntity() {
        super.processEntity()

        if (firstProcess) {
            setBonuses()
            recalcHpScaling(force = true)
            firstProcess = false
        }

        val now = Utils.currentTimeMillis()
        if (now - spawnTimeMs >= gracePeriodMs && now - lastInteractionMs >= idleTimeoutMs) {
            handler.onBossIdleDespawn(this)
            finish()
            return
        }

        ticksUntilRecheck--
        if (ticksUntilRecheck <= 0) {
            recalcHpScaling()
            ticksUntilRecheck = RECHECK_INTERVAL
        }
    }

    private fun recalcHpScaling(force: Boolean = false) {
        val nearbyPlayers = World.getPlayers().count {
            it != null && !it.hasFinished() && it.withinDistance(this, SCALE_RADIUS)
        }

        val scale = if (isMinion) 1.0 + (nearbyPlayers * 0.05) else 1.0 + (nearbyPlayers * 0.2)
        val newMaxHp = max(1, (baseHp * scale).toInt())

        if (newMaxHp != maxHitpoints || force) {
            val oldMaxHp = maxHitpoints.takeIf { it > 0 } ?: newMaxHp
            val hpRatio = hitpoints.toDouble() / oldMaxHp

            increasedMaxHitpoints = newMaxHp
            hitpoints = max(1, max(hitpoints, (newMaxHp * hpRatio).toInt()))

            if (force) {
                World.sendWorldMessage(
                    "<col=ff8c00>[WorldBoss] $name adapts to $nearbyPlayers warriors nearby! (${newMaxHp} HP)",
                    false
                )
            }
        }
    }

    override fun handleIncommingHit(hit: Hit) {
        super.handleIncommingHit(hit)
        if (hit.source is Player) {
            lastInteractionMs = Utils.currentTimeMillis()
        }
    }

    override fun sendDeath(source: Entity?) {
        try {
            World.sendWorldMessage(
                "<img=7><col=ff0000>News: $name has been defeated! ${Settings.SERVER_NAME} trembles...",
                false
            )
        } finally {
            super.sendDeath(source)
        }
    }

    override fun finish() {
        val wasFinished = hasFinished()
        super.finish()
        if (!wasFinished) {
            handler.onBossFinished(externallyDespawning ?: "Finished")
        }
    }

    fun markExternallyDespawning(reason: String) {
        externallyDespawning = reason
    }
}
