package com.rs.kotlin.game.npc.worldboss

import com.rs.java.game.WorldTile

class GenericWorldBossNPC(
    id: Int,
    tile: WorldTile,
    idleTimeoutMs: Long,
    gracePeriodMs: Long,
    handler: RandomWorldBossHandler,
    isMinion: Boolean
) : WorldBossNPC(id, tile, idleTimeoutMs, gracePeriodMs, handler, isMinion) {

}
