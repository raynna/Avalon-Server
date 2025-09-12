package com.rs.kotlin.game.world.projectile

import com.rs.java.game.Entity
import com.rs.java.game.WorldTile

data class QueuedProjectile(
    val attacker: Entity?,
    val defender: Entity?,
    val startTile: WorldTile?,
    val endTile: WorldTile?,
    val gfx: Int,
    val type: ProjectileType,
    val creatorSize: Int,
    val sendCycle: Int
)