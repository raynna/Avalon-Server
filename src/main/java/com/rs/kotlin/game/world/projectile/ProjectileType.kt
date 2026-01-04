package com.rs.kotlin.game.world.projectile

data class ProjectileType(
    val startHeight: Int = 40,
    val endHeight: Int = 36,
    val startTime: Int = 51,
    val arc: Int = 16,
    val displacement: Int = 0,
    val multiplier: Int = 5,
) {

    fun endTime(distance: Int): Int =
        startTime + (distance * multiplier)
}

