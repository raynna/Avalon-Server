package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Entity
import raynna.game.Graphics
import raynna.game.player.Player
import raynna.game.player.Skills

object CureOtherService {
    fun cast(
        player: Player,
        target: Entity,
    ): Boolean {
        val other = target as? Player ?: return false

        player.faceEntity(other)

        if (!other.isAcceptAid) {
            player.message("${other.displayName} doesn't have aid on.")
            return false
        }

        if (!other.poison.isPoisoned && !other.newPoison.isPoisoned()) {
            player.message("${other.displayName} is not poisoned.")
            return false
        }

        if (!other.isAtMultiArea) {
            player.message("You can only cast this spell in a multi-area.")
            return false
        }

        player.animate(Animation(4411))

        other.gfx(Graphics(744, 0, 100))

        other.message("You have been cured by player ${player.displayName}.")
        other.newPoison.reset()
        other.poison.reset()

        return true
    }
}
