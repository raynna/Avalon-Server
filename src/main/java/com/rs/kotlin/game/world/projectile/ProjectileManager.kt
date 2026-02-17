package com.rs.kotlin.game.world.projectile

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.core.thread.WorldThread
import com.rs.java.game.*
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.combat.CombatUtils
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
        val type = ProjectileRegistry.get(projectile)?.resolve() ?: return
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

        val impactTicks = impactCycles / 30

        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                onLanded.run()
            }
        }, impactTicks)

    }

    @JvmStatic
    fun sendSimple(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity
    ): Int {
        return send(
            projectile = projectile,
            gfxId = gfxId,
            attacker = attacker,
            defender = defender,
            blockAnimation = false
        )
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
    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        hitGraphic: Graphics,
        hitSound: Int,
    ): Int {
        return send(
            projectile = projectile,
            gfxId = gfxId,
            attacker = attacker,
            defender = defender,
            arcOffset = 0,
            startHeightOffset = 0,
            startTimeOffset = 0,
            hitGraphic = hitGraphic,
            hitSound = hitSound,
            speedAdjustment = 0,
            displacement = 0,
            onLanded = null
        )
    }


    @JvmStatic
    fun send(
        projectile: Projectile,
        gfxId: Int,
        attacker: Entity,
        defender: Entity,
        displacementOffset: Int,
        onLanded: Runnable? = null
    ): Int {
        return send(
            projectile = projectile,
            gfxId = gfxId,
            attacker = attacker,
            defender = defender,
            displacement = displacementOffset,
            onLanded = { onLanded?.run() }
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
        blockAnimation: Boolean = true,
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
            blockAnimation = blockAnimation,
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

        val type = baseType.resolve(
            startHeightOffset = heightOffset,
            arcOffset = angleOffset,
            startTimeOffset = delayOffset
        )


        val distance = Utils.getDistance(attacker, defender)

        val endCycle = type.endTime(distance)
        val impactTicks = (endCycle / 30)
        val remainderCycles = endCycle % 30

        val startTile = attacker.faceWorldTile
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

        val resolveDefender = resolveEntity(defender)
        if (hitGraphic != null) {
            val def = resolveDefender()
            hitGraphic.let {
                val rotation = calculateRotation(startTile, endTile)
                if (def == null || def.hasFinished() || def.plane != endTile.plane)
                    return@let
                def.gfx(it.id, endCycle, 100, rotation)
            }
        }
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                onLanded?.invoke(remainderCycles)
            }
        }, max(0, impactTicks))

        return ProjectileResult(impactTicks, remainderCycles)
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
        blockAnimation: Boolean = true,
        speedAdjustment: Int = 0,
        displacement: Int = 0,
        onLanded: (() -> Unit)? = null
    ): Int {

        val baseType = ProjectileRegistry.get(projectile) ?: return 1
        val type = baseType.resolve(
            startHeightOffset = startHeightOffset,
            arcOffset = arcOffset,
            startTimeOffset = startTimeOffset,
            displacementOffset = displacement,
            speedAdjustment = speedAdjustment
        )

        val distance = Utils.getDistance(attacker, defender)

        val endCycle = type.endTime(distance)
        val tickScale = if (attacker is NPC) 0 else 15//unused
        val impactTicks = (endCycle) / 30

        val startTile = if (projectile == Projectile.ICE_BARRAGE) {
            defender.worldTile
        } else {
            attacker.centerTile
        }
        val endTile = defender.worldTile
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
        val resolveDefender = resolveEntity(defender)
        val def = resolveDefender()
        if (blockAnimation) {
            if (def != null && def is Player) {
                val anim = CombatUtils.getBlockAnimation(def)
                def.animateWithDelay(anim, endCycle)
            }
        }

        if (hitGraphic != null) {
            val def = resolveDefender()
            hitGraphic.let {
                val rotation = calculateRotation(startTile, endTile)
                if (def == null || def.hasFinished() || def.plane != endTile.plane)
                    return@let
                def.gfx(it.id, endCycle, it.height, rotation)
            }
            hitSound.let {
                def?.playSound(it, endCycle, 1)
            }
        }
        if (onLanded != null) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    onLanded.invoke()
                }
            }, max(0, impactTicks))
        }
        return impactTicks
    }

    private fun queueProjectileAndGetImpactCycles(
        attacker: Entity?,
        defender: Entity?,
        startTile: WorldTile?,
        endTile: WorldTile?,
        gfx: Int,
        type: ResolvedProjectileType,
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
            if (player == null || !player.hasStarted() || player.hasFinished() || (attacker != null && player.isOutOfRegion(
                    attacker
                ))
            ) continue
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

    fun calculateRotation(
        start: WorldTile,
        end: WorldTile,
        facing: Int
    ): Int {
        val dx = end.x - start.x
        val dy = end.y - start.y

        val angle = Math.atan2(dy.toDouble(), dx.toDouble())
        var dir = Math.round(angle / (Math.PI / 4)).toInt()

        if (dir < 0) dir += 8
        dir %= 8

        // make it relative to facing
        var rot = dir - facing
        if (rot < 0) rot += 8

        return rot
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
