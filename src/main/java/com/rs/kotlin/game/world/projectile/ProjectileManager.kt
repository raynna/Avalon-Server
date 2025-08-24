package com.rs.kotlin.game.world.projectile

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.Rscm
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
    fun sendSimpleToTile(projectile: Projectile, gfxId: Int, startTile: WorldTile, endTile: WorldTile) {
        val type = ProjectileRegistry.get(projectile) ?: run {
            println("Unknown projectile type: $projectile")
            return
        }
        sendProjectile(defender = null, startTile = startTile, endTile = endTile, gfx = gfxId, type = type, creatorSize = 1);
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

    fun sendDelayed(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        delayTicks: Int = 0,
        heightOffset: Int = 0,
        hitGraphic: Graphics? = null,
        speedAdjustment: Int = 0,
        onLanded: (() -> Unit)? = null
    ) {
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                send(
                    projectile = projectile,
                    gfxId = gfxId,
                    attacker = attacker,
                    defender = defender,
                    heightOffset = heightOffset,
                    hitGraphic = hitGraphic,
                    speedAdjustment = speedAdjustment,
                    onLanded = onLanded
                )
            }
        }, max(0, delayTicks - 1))
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
    ) {
        val type = ProjectileRegistry.get(projectile) ?: run {
            println("Unknown projectile type: $projectile")
            return
        }

        val adjustedType = type.copy(
            startHeight = (type.startHeight + heightOffset).coerceIn(0, 255),
            endHeight = (type.endHeight + heightOffset).coerceIn(0, 255),
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
        val gfxId = Rscm.lookup(gfx);
        val duration = sendProjectile(defender, startTile, endTile, gfxId, type = adjustedType, attacker.size)
        val delayTicks = max(0, (duration / 30.0).toInt() - 1)

        if (hitGraphic != null) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    val rotation = calculateRotation(startTile, endTile)
                    defender.gfx(hitGraphic.id, hitGraphic.height, rotation)
                    onLanded?.invoke()
                }
            }, delayTicks)
        } else {
            onLanded?.invoke()
        }
    }


    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        heightOffset: Int = 0,
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
            endHeight = (type.endHeight + heightOffset).coerceIn(0, 255),
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

        val duration = sendProjectile(defender, startTile, endTile, gfxId, type = adjustedType, attacker.size)
        val delayTicks = max(0, (duration / 30.0).toInt() - 1)

        if (hitGraphic != null) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    val rotation = calculateRotation(startTile, endTile)
                    defender.gfx(hitGraphic.id, hitGraphic.height, rotation)
                    onLanded?.invoke()
                }
            }, delayTicks)
        } else {
            onLanded?.invoke()
        }
    }

    private fun sendProjectile(
        defender: Entity? = null,
        startTile: WorldTile,
        endTile: WorldTile,
        gfx: Int,
        type: ProjectileType,
        creatorSize: Int
    ): Int {
        val distance = Utils.getDistance(startTile.x, startTile.y, endTile.x, endTile.y)
        val travelDuration = type.speed + 20 + (distance * 5) + (distance * distance / 8)

        val players = World.getPlayers().stream().filter { player ->
            player.hasStarted() && !player.hasFinished() &&
                    (player.withinDistance(startTile) || player.withinDistance(endTile))
        }

        for (player in players) {
            val stream = player.packets.createWorldTileStream(startTile)
            stream.writePacket(player, 20)
            val flags: Int = ((startTile.xInChunk shl 3) or startTile.yInChunk)
            stream.writeByte(flags)
            stream.writeByte(endTile.x - startTile.x)
            stream.writeByte(endTile.y - startTile.y)
            val index = when (defender) {
                null -> 0
                is Player -> -(defender.index + 1)
                else -> defender.index + 1
            }
            stream.writeShort(index)//lock or not

            stream.writeShort(gfx)
            stream.writeByte(type.startHeight)
            stream.writeByte(type.endHeight)
            val delay = (1 + type.delay) * 30
            stream.writeShort(delay)
            stream.writeShort(travelDuration)
            stream.writeByte(type.angle)
            val slope = creatorSize * 64 + type.displacement * 64
            stream.writeShort(slope)
            player.session.write(stream)
        }

        return travelDuration
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