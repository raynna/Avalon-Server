package com.rs.kotlin.game.world.projectile

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils

object ProjectileManager {

    /*@JvmStatic // for java call
    fun sendWithHitGraphic(
        projectile: Projectile,
        projectileGfxId: Int,
        attacker: Entity,
        defender: Entity,
        hitGraphicId: Graphics
    ) = sendWithHitGraphic(projectile, projectileGfxId, attacker, defender, hitGraphic)*/

    @JvmStatic
    fun sendWithHitGraphic(
        projectile: Projectile,
        projectileGfxId: Int,
        attacker: Entity,
        defender: Entity,
        hitGraphic: Graphics
    ) {
        sendWithHeightAndHitGraphic(
            projectile,
            projectileGfxId,
            0, // Default height difference
            attacker,
            defender,
            hitGraphic
        )
    }

    @JvmStatic
    fun sendWithHeightAndHitGraphic(
        projectile: Projectile,
        projectileGfxId: Int,
        heightDifference: Int,
        attacker: Entity,
        defender: Entity,
        hitGraphic: Graphics = Graphics(-1)
    ) {
        val baseType = ProjectileRegistry.get(projectile) ?: run {
            println("Unknown projectile type: $projectile")
            return
        }

        val adjustedType = baseType.copy(
            startHeight = (baseType.startHeight + heightDifference).coerceIn(0, 255),
            endHeight = (baseType.endHeight + heightDifference).coerceIn(0, 255)
        )

        val player = if (attacker is Player) attacker else defender as? Player
            ?: error("Either attacker or defender must be a Player")
        val startTile = WorldTile(attacker.getCoordFaceX(attacker.size),
            attacker.getCoordFaceY(attacker.size),
            attacker.plane)
        val endTile = WorldTile(
            defender.getCoordFaceX(defender.size),
            defender.getCoordFaceY(defender.size),
            defender.plane)
        val duration = sendProjectile(
            player = player,
            receiver = defender,
            startTile = startTile,
            endTile = endTile,
            gfx = projectileGfxId,
            type = adjustedType,
            creatorSize = attacker.size
        )

        val delayTicks = ((duration + 29) / 30) - 1

        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                val rotation = calculateRotation(startTile, endTile);
                defender.gfx(hitGraphic.id, hitGraphic.height, rotation)
            }
        }, delayTicks.coerceAtLeast(0))
    }

    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        heightDifference: Int,
        attacker: Entity,
        defender: Entity,
        hitGraphic: Graphics = Graphics(-1)
    ) = sendWithHeightAndHitGraphic(
        projectile,
        gfxId,
        heightDifference,
        attacker,
        defender,
        hitGraphic
    )

    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        heightDifference: Int,
        attacker: Entity,
        defender: Entity
    ) {
        val baseType = ProjectileRegistry.get(projectile) ?: run {
            println("Unknown projectile type: $projectile")
            return
        }

        val adjustedType = baseType.copy(
            startHeight = (baseType.startHeight + heightDifference).coerceIn(0, 255),
            endHeight = (baseType.endHeight + heightDifference).coerceIn(0, 255)
        )

        sendProjectile(
            player = if (attacker is Player) attacker else defender as? Player
                ?: error("Either attacker or defender must be a Player"),
            receiver = defender,
            startTile = WorldTile(
                attacker.getCoordFaceX(attacker.size),
                attacker.getCoordFaceY(attacker.size),
                attacker.plane
            ),
            endTile = WorldTile(
                defender.getCoordFaceX(defender.size),
                defender.getCoordFaceY(defender.size),
                defender.plane
            ),
            gfx = gfxId,
            type = adjustedType,
            creatorSize = attacker.size
        )
    }

    @JvmStatic
    fun sendWithSpeed(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        speedDifference: Int
    ) {
        val baseType = ProjectileRegistry.get(projectile) ?: run {
            println("Unknown projectile type: $projectile")
            return
        }

        val newSpeed = baseType.copy(
            speed = (baseType.speed + speedDifference).coerceIn(0, 255)
        )

        sendProjectile(
            player = if (attacker is Player) attacker else defender as? Player
                ?: error("Either attacker or defender must be a Player"),
            receiver = defender,
            startTile = WorldTile(
                attacker.getCoordFaceX(attacker.size),
                attacker.getCoordFaceY(attacker.size),
                attacker.plane
            ),
            endTile = WorldTile(
                defender.getCoordFaceX(defender.size),
                defender.getCoordFaceY(defender.size),
                defender.plane
            ),
            gfx = gfxId,
            type = baseType,
            creatorSize = attacker.size,
            adjustedSpeed = newSpeed.speed
        )
    }

    @JvmStatic
    fun sendWithDelay(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        delayDifference: Int
    ) {
        val baseType = ProjectileRegistry.get(projectile) ?: run {
            println("Unknown projectile type: $projectile")
            return
        }

        val newDelay = baseType.copy(
            delay = (baseType.delay + delayDifference).coerceIn(0, 255)
        )

        sendProjectile(
            player = if (attacker is Player) attacker else defender as? Player
                ?: error("Either attacker or defender must be a Player"),
            receiver = defender,
            startTile = WorldTile(
                attacker.getCoordFaceX(attacker.size),
                attacker.getCoordFaceY(attacker.size),
                attacker.plane
            ),
            endTile = WorldTile(
                defender.getCoordFaceX(defender.size),
                defender.getCoordFaceY(defender.size),
                defender.plane
            ),
            gfx = gfxId,
            type = baseType,
            creatorSize = attacker.size,
            adjustedDelay = newDelay.delay
        )
    }

    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity
    ) = send(projectile, gfxId, 0, attacker, defender)

    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        from: WorldTile,
        to: WorldTile,
        receiver: Entity?,
        creatorSize: Int,
        player: Player
    ) {
        val baseType = ProjectileRegistry.get(projectile) ?: run {
            println("Unknown projectile type: $projectile")
            return
        }
        sendProjectile(player, receiver, from, to, gfxId, baseType, creatorSize)
    }

    private fun sendProjectile(
        player: Player,
        receiver: Entity?,
        startTile: WorldTile,
        endTile: WorldTile,
        gfx: Int,
        type: ProjectileType,
        creatorSize: Int,
        adjustedSpeed: Int = -1,
        adjustedDelay: Int = -1
    ): Int {
        val stream = player.packets.createWorldTileStream(startTile)
        stream.writePacket(player, 20)

        val localX = startTile.getLocalX(player.lastLoadedMapRegionTile, player.mapSize)
        val localY = startTile.getLocalY(player.lastLoadedMapRegionTile, player.mapSize)

        val offsetX = localX and 0x7
        val offsetY = localY and 0x7
        stream.writeByte((offsetX shl 3) or offsetY)
        stream.writeByte(endTile.x - startTile.x)
        stream.writeByte(endTile.y - startTile.y)

        val index = when (receiver) {
            null -> 0
            is Player -> -(receiver.index + 1)
            else -> receiver.index + 1
        }
        val delay = if (adjustedDelay != -1) adjustedDelay else type.delay
        stream.writeShort(index)

        stream.writeShort(gfx)
        stream.writeByte(type.startHeight)
        stream.writeByte(type.endHeight)
        stream.writeShort(delay)

        val distance = Utils.getDistance(startTile.x, startTile.y, endTile.x, endTile.y)
        val speed = if (adjustedSpeed != -1) adjustedSpeed else type.speed
        val travelDuration = if (distance == 0) 10 else (distance * 30) / (speed / 10)
        val totalDuration = delay + travelDuration

        stream.writeShort(totalDuration)
        stream.writeByte(type.arc)

        val finalOffset = (creatorSize shl 6) + (type.displacement shl 6)
        stream.writeShort(finalOffset)

        player.session.write(stream)
        return totalDuration
    }

    fun calculateRotation(startTile: WorldTile, endTile: WorldTile): Int {
        val startX = startTile.x
        val startY = startTile.y
        val endX = endTile.x
        val endY = endTile.y
        val (rotation, directionName) = when {
            endX == startX && endY < startY -> Pair(0, "North")
            endX < startX && endY < startY -> Pair(1, "North-East")
            endX < startX && endY == startY -> Pair(2, "East")
            endX < startX && endY > startY -> Pair(3, "South-East")
            endX == startX && endY > startY -> Pair(4, "South")
            endX > startX && endY > startY -> Pair(5, "South-West")
            endX > startX && endY == startY -> Pair(6, "West")
            endX > startX && endY < startY -> Pair(7, "North-West")
            else -> Pair(8, "South (default)")
        }
        return rotation
    }
}