package com.rs.kotlin.game.world.projectile

data class ProjectileType(
    val startHeight: Int,
    val endHeight: Int,
    val startTime: Int,
    val arc: Int,
    val displacement: Int = 0,
    val multiplier: Int = 5,
) {

    fun endTime(distance: Int): Int =
        startTime + (distance * multiplier)
}

