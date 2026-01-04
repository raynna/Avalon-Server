package com.rs.kotlin.game.world.projectile

data class ProjectileType(
    val startHeight: Int = 42,
    val endHeight: Int = 28,
    val startTime: Int = 51,
    val arc: Int = 12,
    val displacement: Int = 0,
    val multiplier: Int = 5,
) {

    fun endTime(distance: Int): Int =
        startTime + (distance * multiplier)
}

