package com.rs.kotlin.game.player.skills.fishing

/**
 * Parameters for the skilling success formula.
 *
 * P(level) = (1 + floor( low*(99−level)/98 + high*(level−1)/98 + 0.5 )) / 256
 *
 * [low]  — numerator contribution at level 1  (success ≈ (1+low)/256 at lvl 1)
 * [high] — numerator contribution at level 99 (success ≈ (1+high)/256 at lvl 99)
 */
data class SuccessCurve(
    val low: Int,
    val high: Int,
)
