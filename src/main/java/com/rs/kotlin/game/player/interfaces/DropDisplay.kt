package com.rs.kotlin.game.player.interfaces

import com.rs.kotlin.game.npc.drops.DropTable
import com.rs.kotlin.game.npc.drops.DropType
import com.rs.kotlin.game.npc.drops.weighted.WeightedTable

data class DropDisplay(
    val itemId: Int,
    val amount: IntRange,
    val rarityText: String,
    val type: DropType,
    val baseDenominator: Int,
    val weight: Int? = null,
    val totalWeight: Int? = null,
    val nothingWeight: Int? = null,
    val percentage: Double? = null,
    val tableReference: WeightedTable? = null,
    val tableName: String? = null,
    val parentChance: Double? = null,
    val expandNested: Boolean = false,
    val subTable: DropTable? = null,
)

fun IntRange.toDisplayString(): String =
    if (first == last) {
        first.toString()
    } else {
        "$first-$last"
    }
