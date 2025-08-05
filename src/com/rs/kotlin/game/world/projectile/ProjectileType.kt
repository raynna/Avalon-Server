package com.rs.kotlin.game.world.projectile

data class ProjectileType(
    val startHeight: Int,
    val endHeight: Int,
    val delay: Int,
    val duration: Int,
    val arc: Int = 0,
    val displacement: Int = 64
) {
    fun getSpeed(distance: Int): Int {
        return if (duration == 0) 1 else (distance * 30) / duration
    }
}
