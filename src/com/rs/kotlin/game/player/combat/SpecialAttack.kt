package com.rs.kotlin.game.player.combat

import com.rs.kotlin.game.player.combat.range.SpecialEffect

data class SpecialAttack(
    val name: String,
    val energyCost: Int,
    val damageMultiplier: Double,
    val accuracyMultiplier: Double,
    val specialProjectileId: Int? = null,
    val specialEffect: SpecialEffect? = null
)