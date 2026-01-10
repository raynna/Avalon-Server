package com.rs.kotlin.game.player.combat.special

import com.rs.kotlin.game.player.combat.CombatType

data class ChainSettings(
    val firstCombatType: CombatType,
    val spreadCombatType: CombatType = firstCombatType,
    val damageMultiplierPerBounce: Double = 1.0,
    val flatDamageMultiplier: Double = 1.0,
    val minDamageMultiplier: Double = 0.25
)
