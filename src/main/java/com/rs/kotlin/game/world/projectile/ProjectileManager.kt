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
import kotlin.math.floor
import kotlin.math.max

object ProjectileManager {

    @JvmStatic
    fun sendSimple(projectile: Projectile, gfxId: Int, attacker: Entity, defender: Entity) {
        send(
            projectile = projectile,
            gfxId = gfxId,
            attacker = attacker,
            defender = defender,
            heightOffset = 0,
            hitGraphic = null,
            speedAdjustment = 0,
            onLanded = null
        )
    }

    @JvmStatic
    fun sendToTile(
        projectile: Projectile,
        gfxId: Int,
        startTile: WorldTile,
        endTile: WorldTile,
        onLanded: Runnable? = null
    ) {
        val type = ProjectileRegistry.get(projectile) ?: return

        val duration = sendProjectile(
            attacker = null,
            defender = null,
            startTile = startTile,
            endTile = endTile,
            gfx = gfxId,
            type = type,
            creatorSize = 0
        )

        onLanded ?: return

        val delayTicks = max(0, (duration / 30.0).toInt())
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                onLanded.run()
            }
        }, delayTicks)
    }



    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        onLanded: Runnable? = null
    ) {
        send(
            projectile = projectile,
            gfxId = gfxId,
            attacker = attacker,
            defender = defender,
            angleOffset = 0,
            heightOffset = 0,
            delayOffset = 0,
            hitGraphic = null,
            speedAdjustment = 0,
            onLanded = { onLanded?.run() }
        )
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
        sendMagic(
            projectile,
            projectileGfx,
            attacker,
            defender,
            hit,
            successGraphic,
            Graphics(85, 100)
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
                val gfx =
                    if (hit.damage > 0) successGraphic
                    else splashGraphic

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
    fun sendWithGraphic(projectile: Projectile, gfxId: Int, attacker: Entity, defender: Entity, hitGraphic: Graphics) {
        send(
            projectile = projectile,
            gfxId = gfxId,
            attacker = attacker,
            defender = defender,
            heightOffset = 0,
            hitGraphic = hitGraphic,
            speedAdjustment = 0,
            onLanded = null
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
        onLanded: (() -> Unit)? = null) {
        this.send(projectile, gfxId = Rscm.graphic(gfx), attacker = attacker, defender = defender, heightOffset = heightOffset, hitGraphic = hitGraphic, speedAdjustment = speedAdjustment);
    }

    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        angleOffset: Int = 0,
        heightOffset: Int = 0,
        delayOffset: Int = 0,
        hitGraphic: Graphics? = null,
        speedAdjustment: Int = 0,
        onLanded: (() -> Unit)? = null
    ) {
        val type = ProjectileRegistry.get(projectile) ?: run {
            println("Unknown projectile type: $projectile")
            return
        }
        val adjustedType = type.copy(
            startHeight = (type.startHeight + heightOffset).coerceIn(0, 255),
            endHeight = (type.endHeight).coerceIn(0, 255),
            angle = (type.angle + angleOffset).coerceIn(0, 255),
            delay = (type.delay + delayOffset / 30.0).coerceIn(0.0, 5.0),
            speed = type.speed + speedAdjustment,
        )

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

        val duration = sendProjectile(attacker, defender, null, endTile, gfxId, type = adjustedType, attacker.size)
        val delayTicks = max(0, (duration / 30))

        if (hitGraphic != null || onLanded != null) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    if (hitGraphic != null) {
                        val rotation = calculateRotation(startTile, endTile)
                        defender.gfx(hitGraphic.id, hitGraphic.height, rotation)
                    }
                    onLanded?.invoke()
                }
            }, delayTicks)
        }

    }

    private fun sendProjectile(
        attacker: Entity? = null,
        defender: Entity? = null,
        startTile: WorldTile? = null,
        endTile: WorldTile? = null,
        gfx: Int,
        type: ProjectileType,
        creatorSize: Int,
        delayTicks: Int = 0
    ): Int {
        val queued = QueuedProjectile(
            attacker = attacker,
            defender = defender,
            startTile = startTile,
            endTile = endTile,
            gfx = gfx,
            type = type,
            creatorSize = creatorSize,
            sendCycle = WorldThread.getLastCycleTime().toInt() + delayTicks
        )

        for (player in World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue
            player.queueProjectile(queued)
        }

        val distance = Utils.getDistance(startTile?.x ?: attacker!!.x, startTile?.y ?: attacker!!.y,
            endTile?.x ?: defender!!.x, endTile?.y ?: defender!!.y)
        val (_, duration) = type.toClientValues(distance)
        return duration
    }

    @JvmStatic
    fun flushProjectile(player: Player, proj: QueuedProjectile) {
        val start: WorldTile = if (proj.attacker != null) {
            WorldTile(
                proj.attacker.getCoordFaceX(proj.attacker.size),
                proj.attacker.getCoordFaceY(proj.attacker.size),
                proj.attacker.plane
            )
        } else {
            proj.startTile!!
        }

        val end: WorldTile = if (proj.defender != null) {
            WorldTile(
                proj.defender.getCoordFaceX(proj.defender.size),
                proj.defender.getCoordFaceY(proj.defender.size),
                proj.defender.plane
            )
        } else {
            proj.endTile!!
        }

        val stream = player.packets.createWorldTileStream(start)
        stream.writePacket(player, 20)

        val flags: Int = ((start.xInChunk shl 3) or start.yInChunk)
        stream.writeByte(flags)
        stream.writeByte(end.x - start.x)
        stream.writeByte(end.y - start.y)
        //println("startX: ${start.x} startY: ${start.y}, endX: ${end.x}, endY: ${end.y}, (finalX: ${end.x - start.x}, finalY: ${end.y - start.y})")

        val index = when (proj.defender) {
            null -> 0
            is Player -> -(proj.defender.index + 1)
            else -> proj.defender.index + 1
        }
        stream.writeShort(index)

        stream.writeShort(proj.gfx)
        stream.writeByte(proj.type.startHeight)
        stream.writeByte(proj.type.endHeight)

        val distance = Utils.getDistance(start.x, start.y, end.x, end.y)
        val (delay, duration) = proj.type.toClientValues(distance)

        stream.writeShort(delay)
        stream.writeShort(duration)
        stream.writeByte(proj.type.angle)
        val displacement = if (distance == 1 && proj.type.displacement == 0) 64 else proj.type.displacement
        val effectiveSize =
            if (proj.attacker != null) proj.creatorSize else 0

        var startDistanceOffset = effectiveSize * 64 + displacement * 64
        //println("displament: " + displacement)
        //println("startDistanceOffset: " + startDistanceOffset)
        stream.writeShort(startDistanceOffset)

        player.session.write(stream)
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
