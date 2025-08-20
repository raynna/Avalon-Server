package com.rs.kotlin.game.npc.combatdata

data class RangedDefence(
    val light: Int = 0,
    val standard: Int,
    val heavy: Int = 0
) {
    fun getStandardBonus() = standard
    fun getLightBonus() = light
    fun getHeavyBonus() = heavy
}
