package raynna.game.npc.worldboss

import raynna.game.WorldTile

class GenericWorldMinion(
    id: Int,
    tile: WorldTile,
    idleTimeoutMs: Long,
    gracePeriodMs: Long,
    handler: RandomWorldBossHandler,
) : WorldMinionNPC(id, tile, idleTimeoutMs, gracePeriodMs, handler) {

}
