package com.rs.kotlin.game.world.projectile

data class ProjectileType(
    val startHeight: Int = 42,
    val endHeight: Int = 28,
    val delay: Double = 1.5,   // fractional ticks
    val speed: Int = 5,        // 1 = slowest, 10 = fastest
    val angle: Int = 12,
    val displacement: Int = 0
) {
    fun toClientValues(distance: Int): Pair<Int, Int> {
        val delayTicks = (delay * 30).toInt().coerceAtLeast(0)
        val duration = delayTicks + (distance * 30 / speed.coerceAtLeast(1))
        return delayTicks to duration
    }
}

