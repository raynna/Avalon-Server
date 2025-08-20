package com.rs.kotlin.game.npc.combatdata

data class Npc(
    val id: Int,
    val name: String,
    val members: Boolean,
    val releaseDate: String?,
    val aliases: List<String>,
    val size: String?,
    val examine: String?,
    val attributes: List<String>,
    val categories: List<String>,
    val assignedBy: List<String>,
    val combatData: CombatData
)