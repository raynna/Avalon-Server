package com.rs.kotlin.game.player.combat.special

data class SpecialAttack(
    val energyCost: Int,
    val accuracyMultiplier: Double = 1.0,
    val damageMultiplier: Double = 1.0,
    val execute: (context: SpecialContext) -> Unit
)