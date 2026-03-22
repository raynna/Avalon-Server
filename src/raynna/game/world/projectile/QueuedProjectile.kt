package raynna.game.world.projectile

import raynna.game.Entity
import raynna.game.WorldTile

data class QueuedProjectile(
    val attacker: Entity?,
    val defender: Entity?,
    val startTile: WorldTile?,
    val endTile: WorldTile?,
    val spotanim: Int,
    val type: ResolvedProjectileType,
    val creatorSize: Int,
    val sendCycle: Int,
    val endTime: Int,
)