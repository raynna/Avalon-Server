package com.rs.kotlin.game.player.combat

import com.rs.java.game.Hit

data class PendingHit(
    val hit: Hit,
    val delay: Int
)
