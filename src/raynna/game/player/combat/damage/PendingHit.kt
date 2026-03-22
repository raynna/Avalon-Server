package raynna.game.player.combat.damage

import raynna.game.Entity
import raynna.game.Hit

data class PendingHit(
    val hit: Hit,
    val target: Entity,
    val delay: Int,
    val onApply: (() -> Unit)? = null
)
