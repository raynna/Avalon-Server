package com.rs.kotlin.game.world.projectile

data class ProjectileType(
    val startHeight: Int = 36,
    val endHeight: Int = 28,
    val speed: Int,
    val delay: Int = 1,
    val arc: Int = 0,
    val displacement: Int = 64
)
