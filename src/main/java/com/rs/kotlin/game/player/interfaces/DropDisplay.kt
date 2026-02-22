package com.rs.kotlin.game.player.interfaces

import com.rs.kotlin.game.npc.drops.DropType

data class DropDisplay(
    val itemId: Int,
    val amount: IntRange,
    val rarityText: String,
    val type: DropType,
    val baseDenominator: Int,
    val weight: Int? = 0,
    val percentage: Double? = null,
)

fun IntRange.toDisplayString(): String {
    return if (first == last) {
        first.toString()
    } else {
        "$first-$last"
    }
}
