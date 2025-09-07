package com.rs.kotlin.game.data.npc

data class NpcSpawnEntry(
    val comment: String? = null,
    val npcId: Int,
    val tile: Tile,
    val mapAreaName: String? = null,
    val canBeAttackedFromOutside: Boolean? = null
) {
    data class Tile(val x: Int, val y: Int, val plane: Int)
}
