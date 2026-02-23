package com.rs.kotlin.game.npc.drops

data class SeedTableConfig(
    val table: SeedTableType,
    val amount: IntRange = 1..1,
    val numerator: Int = 1,
    val denominator: Int = 64
)
