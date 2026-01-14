package com.rs.kotlin.game.npc.combatdata

data class MaxHit(
    val melee: Int = 0,
    val magic: Int = 0,
    val ranged: Int = 0,
    val default: Int = 0
) {
    fun forStyle(style: String): Int = when (style.lowercase()) {
        "melee", "crush", "stab", "slash" -> melee
        "magic" -> magic
        "ranged" -> ranged
        else -> default
    }

    fun highest(): Int = listOf(melee, magic, ranged, default).maxOrNull() ?: 0
}
