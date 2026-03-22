package raynna.game.world.projectile

import raynna.game.Entity
import raynna.game.Graphics

data class ProjectileRequest(
    val projectile: Projectile,
    val gfxId: Int,
    val attacker: Entity,
    val defender: Entity,
    val projectileType: ProjectileType? = null,
    val arcOffset: Int = 0,
    val startHeightOffset: Int = 0,
    val startTimeOffset: Int = 0,
    val displacement: Int = 0,
    val multiplierOffset: Int = 0,
    val hitGraphic: Graphics? = null,
    val hitSound: Int = -1,
    val blockAnimation: Boolean = true,
    val onLanded: Runnable? = null,
)
