package com.rs.kotlin.game.npc.drops.config

data class HerbTableConfig(
    val numerator: Int = 1,
    val denominator: Int = 64,
    val amount: IntRange = 1..1,
)
