package com.rs.kotlin.game.player.interfaces

import com.rs.kotlin.game.npc.drops.DropType

data class DropDisplay(
    val itemId: Int,
    val amount: IntRange,
    val rarity: String,
    val type: DropType

)

fun IntRange.toDisplayString(): String {
    return if (first == last) {
        first.toString()
    } else {
        "$first-$last"
    }
}
