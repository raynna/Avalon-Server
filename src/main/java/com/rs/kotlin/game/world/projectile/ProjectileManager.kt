package com.rs.kotlin.game.world.projectile

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.core.thread.WorldThread
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.Rscm
import kotlin.math.max

object ProjectileManager {

    @JvmStatic
    fun sendToTile(
        projectile: Projectile,
        gfxId: Int,
        startTile: WorldTile,
        endTile: WorldTile,
        onLanded: Runnable? = null
    ) {
        val type = ProjectileRegistry.get(projectile) ?: return
        val distance = Utils.getDistance(startTile.x, startTile.y, endTile.x, endTile.y)
        val endTime = type.endTime(distance)
        val impactCycles = queueProjectileAndGetImpactCycles(
            attacker = null,
            defender = null,
            startTile = startTile,
            endTile = endTile,
            gfx = gfxId,
            type = type,
            creatorSize = 0,
            endTime = endTime
        )

        onLanded ?: return
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                onLanded.run()
            }
        }, impactCycles)
    }

    @JvmStatic
    fun sendSimple(projectile: Projectile, gfxId: Int, attacker: Entity, defender: Entity): Int {
        return send(projectile, gfxId, attacker, defender)
    }

    @JvmStatic
    fun sendWithGraphic(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        hitGraphic: Graphics
    ): Int {
        return send(
            projectile = projectile,
            gfxId = gfxId,
            attacker = attacker,
            defender = defender,
            hitGraphic = hitGraphic
        )
    }

    @JvmStatic
    fun sendMagic(
        projectile: Projectile,
        projectileGfx: Int,
        attacker: Entity,
        defender: Entity,
        hit: Hit,
        successGraphic: Graphics,
        splashGraphic: Graphics? = Graphics(85)
    ) {
        send(
            projectile = projectile,
            gfxId = projectileGfx,
            attacker = attacker,
            defender = defender,
            onLanded = {
                val gfx = if (hit.damage > 0) successGraphic else splashGraphic
                if (gfx != null) {
                    val startTile = WorldTile(
                        attacker.getCoordFaceX(attacker.size),
                        attacker.getCoordFaceY(attacker.size),
                        attacker.plane
                    )
                    val endTile = WorldTile(
                        defender.getCoordFaceX(defender.size),
                        defender.getCoordFaceY(defender.size),
                        defender.plane
                    )
                    defender.gfx(gfx.id, gfx.height, calculateRotation(startTile, endTile))
                }
            }
        )
    }

    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        onLanded: Runnable? = null
    ): Int {
        return send(
            projectile = projectile,
            gfxId = gfxId,
            attacker = attacker,
            defender = defender,
            onLanded = { onLanded?.run() }
        )
    }

    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        hitGraphic: Graphics,
        attacker: Entity,
        defender: Entity,
        onLanded: Runnable? = null
    ): Int {
        return send(
            projectile = projectile,
            gfxId = gfxId,
            hitGraphic = hitGraphic,
            attacker = attacker,
            defender = defender,
            onLanded = { onLanded?.run() }
        )
    }

    fun send(
        projectile: Projectile,
        gfx: String,
        attacker: Entity,
        defender: Entity,
        heightOffset: Int = 0,
        hitGraphic: Graphics? = null,
        speedAdjustment: Int = 0,
        onLanded: (() -> Unit)? = null
    ): Int {
        return send(
            projectile = projectile,
            gfxId = Rscm.graphic(gfx),
            attacker = attacker,
            defender = defender,
            startHeightOffset = heightOffset,
            hitGraphic = hitGraphic,
            speedAdjustment = speedAdjustment,
            onLanded = onLanded
        )
    }

    data class ProjectileResult(
        val impactTicks: Int,
        val remainderCycles: Int
    )

    fun sendResult(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        angleOffset: Int = 0,
        heightOffset: Int = 0,
        delayOffset: Int = 0,
        hitGraphic: Graphics? = null,
        onLanded: ((Int) -> Unit)? = null
    ): ProjectileResult {

        val baseType = ProjectileRegistry.get(projectile) ?: return ProjectileResult(0, 0)

        val type = baseType.copy(
            startHeight = (baseType.startHeight + heightOffset).coerceIn(0, 255),
            endHeight = baseType.endHeight.coerceIn(0, 255),
            arc = (baseType.arc + angleOffset).coerceIn(0, 255),
            startTime = baseType.startTime + delayOffset
        )

        val distance = Utils.getDistance(attacker, defender)

        val endCycle = type.endTime(distance)
        val impactTicks = max(0, ((endCycle + 29) / 30) - 1)
        val remainderCycles = endCycle % 30

        val startTile = WorldTile(
            attacker.getCoordFaceX(attacker.size),
            attacker.getCoordFaceY(attacker.size),
            attacker.plane
        )
        val endTile = WorldTile(
            defender.getCoordFaceX(defender.size),
            defender.getCoordFaceY(defender.size),
            defender.plane
        )

        queueProjectileAndGetImpactCycles(
            attacker = attacker,
            defender = defender,
            startTile = null,
            endTile = endTile,
            gfx = gfxId,
            type = type,
            creatorSize = attacker.size,
            endTime = endCycle
        )

        val resolveDefender = resolveEntity(defender)

        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                val def = resolveDefender() ?: return
                if (def.hasFinished()) return
                if (def.plane != endTile.plane) return

                hitGraphic?.let {
                    val rotation = calculateRotation(startTile, endTile)
                    def.gfx(it.id, it.height, rotation)
                }

                onLanded?.invoke(remainderCycles)
            }
        }, max(0, impactTicks - 1))

        return ProjectileResult(impactTicks, remainderCycles)
    }


    @JvmStatic
    fun sendMagic(
        projectile: Projectile,
        projectileGfx: Int,
        attacker: Entity,
        defender: Entity,
        hit: Hit,
        successGraphic: Graphics
    ) {
        sendMagic(projectile, projectileGfx, attacker, defender, hit, successGraphic, Graphics(85, 100))
    }

    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        arcOffset: Int = 0,
        startHeightOffset: Int = 0,
        startTimeOffset: Int = 0,
        hitGraphic: Graphics? = null,
        hitSound: Int = -1,
        speedAdjustment: Int = 0,
        onLanded: (() -> Unit)? = null
    ): Int {

        val baseType = ProjectileRegistry.get(projectile) ?: return 1

        val type = baseType.copy(
            startHeight = (baseType.startHeight + startHeightOffset).coerceIn(0, 255),
            endHeight = baseType.endHeight.coerceIn(0, 255),
            arc = (baseType.arc + arcOffset).coerceIn(0, 255),
            startTime = baseType.startTime + startTimeOffset,
            multiplier =  baseType.multiplier + speedAdjustment,
        )

        val distance = Utils.getDistance(attacker, defender)

        val endCycle = type.endTime(distance)

        val impactTicks = endCycle / 30
        val remainderCycles = endCycle % 30


        val startTile = if (gfxId == 368) {
            WorldTile(defender.faceWorldTile.x - 1, defender.faceWorldTile.y - 1, defender.faceWorldTile.plane)
        } else {
            attacker.faceWorldTile
        }
        val endTile = defender.faceWorldTile

        queueProjectileAndGetImpactCycles(
            attacker = attacker,
            defender = defender,
            startTile = startTile,
            endTile = endTile,
            gfx = gfxId,
            type = type,
            creatorSize = attacker.size,
            endTime = endCycle
        )

        if (hitGraphic != null || onLanded != null) {
            val resolveDefender = resolveEntity(defender)
            val startSnap = startTile
            val endSnap = endTile

            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    val def = resolveDefender() ?: return
                    if (def.hasFinished()) return
                    if (def.plane != endSnap.plane) return

                    hitGraphic?.let {
                        val rotation = calculateRotation(startSnap, endSnap)
                        def.gfx(it.id, it.height, rotation)
                    }
                    if (hitSound != -1)
                        def.playSound(hitSound, 1)
                    onLanded?.invoke()
                }
            }, max(0, impactTicks - 1))
        }

        return impactTicks
    }

    private fun queueProjectileAndGetImpactCycles(
        attacker: Entity?,
        defender: Entity?,
        startTile: WorldTile?,
        endTile: WorldTile?,
        gfx: Int,
        type: ProjectileType,
        creatorSize: Int,
        endTime: Int
    ): Int {
        val distance = Utils.getDistance(startTile, endTile)
        val queued = QueuedProjectile(
            attacker = attacker,
            defender = defender,
            startTile = startTile,
            endTile = endTile,
            spotanim = gfx,
            type = type,
            creatorSize = creatorSize,
            sendCycle = WorldThread.getLastCycleTime().toInt(),
            endTime = endTime
        )

        for (player in World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished() || player.isOutOfRegion(attacker)) continue
            player.queueProjectile(queued)
        }

        return queued.type.endTime(distance)
    }

    @JvmStatic
    fun flushProjectile(player: Player, proj: QueuedProjectile) {
        val start = proj.startTile!!
        val end = proj.endTile!!

        val stream = player.packets.createWorldTileStream(start)
        stream.writePacket(player, 20)
        stream.writeByte((start.xInChunk shl 3) or start.yInChunk)
        stream.writeByte(end.x - start.x)
        stream.writeByte(end.y - start.y)
        val targetEntity = when (proj.defender) {
            null -> 0
            is Player -> -(proj.defender.index + 1)
            else -> proj.defender.index + 1
        }
        stream.writeShort(targetEntity)
        stream.writeShort(proj.spotanim)
        stream.writeByte(proj.type.startHeight)
        stream.writeByte(proj.type.endHeight)
        stream.writeShort(proj.type.startTime)
        stream.writeShort(proj.endTime)
        stream.writeByte(proj.type.arc)
        stream.writeShort(proj.type.displacement)
        player.session.write(stream)
    }

    private fun resolveEntity(defender: Entity): () -> Entity? {
        return when (defender) {
            is Player -> {
                val name = defender.username
                { World.getPlayerByDisplayName(name) }
            }
            else -> {
                val idx = defender.index
                val plane = defender.plane
                {
                    World.getNPCs().firstOrNull {
                        it != null && it.index == idx && it.plane == plane && !it.hasFinished()
                    }
                }
            }
        }
    }

    private fun cyclesToTicksFloor(cycles: Int): Int {
        if (cycles <= 0) return 0
        return cycles / 30
    }

    fun calculateRotation(startTile: WorldTile, endTile: WorldTile): Int {
        val dx = endTile.x - startTile.x
        val dy = endTile.y - startTile.y
        return when {
            dx == 0 && dy < 0 -> 0
            dx < 0 && dy < 0 -> 1
            dx < 0 && dy == 0 -> 2
            dx < 0 && dy > 0 -> 3
            dx == 0 && dy > 0 -> 4
            dx > 0 && dy > 0 -> 5
            dx > 0 && dy == 0 -> 6
            dx > 0 && dy < 0 -> 7
            else -> 0
        }
    }
}
