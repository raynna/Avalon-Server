package com.rs.kotlin.game.player.skills.mining

data class RockVariant(
    val oreId: Int,
    val lowChance: Int,
    val highChance: Int,
    val xp: Double,
)
