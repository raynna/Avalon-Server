package com.rs.kotlin.game.npc.drops.config

import com.rs.kotlin.game.npc.drops.seed.SeedTableType

data class SeedTableConfig(
    val table: SeedTableType,
    val numerator: Int,
    val denominator: Int,
    val amount: IntRange = 1..1,
)
