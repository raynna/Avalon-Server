package com.rs.kotlin.game.npc.worldboss

import com.rs.java.game.WorldTile

class GenericWorldMinion(
    id: Int,
    tile: WorldTile,
    idleTimeoutMs: Long,
    gracePeriodMs: Long,
    handler: RandomWorldBossHandler,
) : WorldMinionNPC(id, tile, idleTimeoutMs, gracePeriodMs, handler) {

}
