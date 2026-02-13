package com.rs.kotlin.game.player.combat.damage

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.kotlin.game.player.combat.CombatType

data class HitRequest(
    val type: CombatType,
    val target: Entity,
    val hits: Int = 1,
    val delay: Int = 0,
    val accuracyMultiplier: Double = 1.0,
    val damageMultiplier: Double = 1.0,
    val hitLook: Hit.HitLook? = null,
    val spellId: Int = -1
)
