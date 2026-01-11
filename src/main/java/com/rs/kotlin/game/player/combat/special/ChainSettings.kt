package com.rs.kotlin.game.player.combat.special

import com.rs.kotlin.game.player.combat.CombatType

data class ChainSettings(
    val firstCombatType: CombatType,
    val spreadCombatType: CombatType,
    val damageMultiplier: Double = 1.0,
    val damageScaleMode: DamageScaleMode = DamageScaleMode.ABSOLUTE,
    val deathSpread: Boolean = false
)

