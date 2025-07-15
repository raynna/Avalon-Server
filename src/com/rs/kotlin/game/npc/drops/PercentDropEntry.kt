package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player

data class PercentDropEntry(
    val itemId: Int,
    val min: Int,
    val max: Int,
    val percent: Double,
    val condition: ((Player?) -> Boolean)? = null
)