package com.rs.kotlin.game.npc.combatdata

data class MeleeDefence(
    val stab: Int,
    val slash: Int,
    val crush: Int
) {
    fun getStabBonus() = stab
    fun getSlashBonus() = slash
    fun getCrushBonus() = crush
}