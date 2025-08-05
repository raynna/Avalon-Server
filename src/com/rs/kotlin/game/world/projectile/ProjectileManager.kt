package com.rs.kotlin.game.world.projectile

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils

object ProjectileManager {


    @JvmStatic
    fun sendWithHitGraphic(
        projectile: Projectile,
        projectileGfxId: Int,
        attacker: Entity,
        defender: Entity,
        hitGraphicId: Int
    ) {
        val baseType = ProjectileRegistry.get(projectile)
        if (baseType == null) {
            println("Unknown projectile type: $projectile")
            return
        }

        val player = if (attacker is Player) attacker else defender as? Player
            ?: error("Either attacker or defender must be a Player")

        val duration = sendProjectile(
            player = player,
            receiver = defender,
            startTile =  WorldTile(attacker.getCoordFaceX(attacker.size), attacker.getCoordFaceY(attacker.size), attacker.plane),
            endTile = WorldTile(defender.getCoordFaceX(defender.size), defender.getCoordFaceY(defender.size), defender.plane),
            gfx = projectileGfxId,
            type = baseType,
            creatorSize = attacker.size
        )

        val delayTicks = ((duration + 29) / 30) - 1

        WorldTasksManager.schedule(object: WorldTask(){
            override fun run() {
                defender.gfx(hitGraphicId)
            }
        }, delayTicks.coerceAtLeast(0))
    }

    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity
    ) {
        val baseType = ProjectileRegistry.get(projectile)
        if (baseType == null) {
            println("Unknown projectile type: $projectile")
            return
        }
        sendProjectile(
            player = if (attacker is Player) attacker else defender as? Player
                ?: error("Either attacker or defender must be a Player"),
            receiver = defender,
            startTile = attacker.tile,
            endTile = defender.tile,
            gfx = gfxId,
            type = baseType,
            creatorSize = attacker.size
        )
    }

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
        val baseType = ProjectileRegistry.get(projectile)
        if (baseType == null) {
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
        creatorSize: Int
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
        stream.writeShort(index)

        stream.writeShort(gfx)
        stream.writeByte(type.startHeight)
        stream.writeByte(type.endHeight)
        stream.writeShort(type.delay)

        val distance = Utils.getDistance(startTile.x, startTile.y, endTile.x, endTile.y)
        val speed = type.duration
        val travelDuration = if (distance == 0) 10 else (distance * 30) / (speed / 10)
        val totalDuration = type.delay + travelDuration

        stream.writeShort(totalDuration)
        stream.writeByte(type.arc)

        val finalOffset = (creatorSize shl 6) + (type.displacement shl 6)
        stream.writeShort(finalOffset)

        player.session.write(stream)
        return totalDuration
    }

}