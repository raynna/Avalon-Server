package raynna.game.npc.worldboss

import raynna.game.WorldTile

class GenericWorldBossNPC(
    id: Int,
    tile: WorldTile,
    idleTimeoutMs: Long,
    gracePeriodMs: Long,
    handler: RandomWorldBossHandler,
) : WorldBossNPC(id, tile, idleTimeoutMs, gracePeriodMs, handler) {

}
