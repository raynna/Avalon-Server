package com.rs.kotlin.game.player.combat.special

data class SpecialEffect(
    val chance: Int = 0,
    val execute: (context: CombatContext) -> Unit
)