package com.rs.kotlin.game.player.combat.damage

import com.rs.java.game.Entity
import com.rs.java.game.Hit

data class PendingHit(
    val hit: Hit,
    val target: Entity,
    val delay: Int,
    val onApply: (() -> Unit)? = null
)
