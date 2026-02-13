package com.rs.kotlin.game.player.combat.damage

data class MaxHit(
    val base: Int,
    val max: Int,
    val min: Int = 0
)
