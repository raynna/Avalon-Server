package com.rs.kotlin.game.world.projectile

data class ResolvedProjectileType(
    val startHeight: Int,
    val endHeight: Int,
    val startTime: Int,
    val arc: Int,
    val displacement: Int,
    val multiplier: Int,
    val lengthAdjustment: Int,
) {
    fun endTime(distance: Int): Int =
        startTime + lengthAdjustment + (distance * multiplier)
}
