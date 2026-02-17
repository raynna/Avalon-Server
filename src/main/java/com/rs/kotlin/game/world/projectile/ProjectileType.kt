package com.rs.kotlin.game.world.projectile

import com.rs.kotlin.game.world.util.RollableInt

data class ProjectileType(
    val startHeight: Int,
    val endHeight: Int,
    val startTime: Int,
    val arc: RollableInt,
    val displacement: Int,
    val multiplier: Int = 5,
    val lengthAdjustment: Int = 0,
) {

    constructor(
        startHeight: Int,
        endHeight: Int,
        startTime: Int,
        arc: Int,
        displacement: Int = 0,
        multiplier: Int = 5,
        lengthAdjustment: Int = 0,
    ) : this(
        startHeight,
        endHeight,
        startTime,
        RollableInt.Fixed(arc),
        displacement,
        multiplier,
        lengthAdjustment
    )

    fun resolve(
        arcOffset: Int = 0,
        startHeightOffset: Int = 0,
        startTimeOffset: Int = 0,
        displacementOffset: Int = 0,
        speedAdjustment: Int = 0
    ): ResolvedProjectileType {

        val rolledArc = arc.roll()

        return ResolvedProjectileType(
            startHeight = (startHeight + startHeightOffset).coerceIn(0, 255),
            endHeight = endHeight.coerceIn(0, 255),
            startTime = startTime + startTimeOffset,
            arc = (rolledArc + arcOffset).coerceIn(0, 255),
            displacement = displacement + displacementOffset,
            multiplier = multiplier + speedAdjustment,
            lengthAdjustment = lengthAdjustment
        )
    }
}
