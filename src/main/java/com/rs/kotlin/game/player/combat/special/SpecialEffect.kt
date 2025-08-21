package com.rs.kotlin.game.player.combat.special

data class SpecialEffect(
    val chance: Int = 0,
    val interruptAttack: Boolean = false,
    val execute: (context: CombatContext) -> Boolean
)