package com.rs.kotlin.game.player.combat.special

data class SpecialEffect(
    val chance: Int,
    val execute: (context: CombatContext) -> Unit
)