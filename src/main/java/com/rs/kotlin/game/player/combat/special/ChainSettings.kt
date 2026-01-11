package com.rs.kotlin.game.player.combat.special

import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.world.projectile.Projectile

data class ChainSettings(
    val projectile: Projectile = Projectile.ARROW,
    val projectileId: Int = -1,
    val projectileEnd: Int = -1,
    val chainMode: ChainMode = ChainMode.NEAREST,
    val firstCombatType: CombatType,
    val spreadCombatType: CombatType,
    val damageMultiplier: Double = 1.0,
    val damageScaleMode: DamageScaleMode = DamageScaleMode.ABSOLUTE,
    val deathSpread: Boolean = false,
    val deathSpreadAmount: Int = 2
)

