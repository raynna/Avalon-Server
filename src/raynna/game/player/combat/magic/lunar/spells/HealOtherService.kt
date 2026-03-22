package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Entity
import raynna.game.Graphics
import raynna.game.Hit
import raynna.game.Hit.HitLook
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.util.Utils

object HealOtherService {
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

        if (!other.isAtMultiArea) {
            player.message("You can only cast this spell in a multi-area.")
            return false
        }

        if (other.hitpoints == other.maxHitpoints) {
            player.message("${other.displayName} has already full hitpoints.")
            return false
        }

        player.animate(Animation(4411))
        other.gfx(Graphics(744, 0, 100))

        other.message("You have been healed by player ${player.displayName}.")

        val damage = (player.hitpoints * 0.75).toInt()

        player.applyHit(Hit(player, damage, HitLook.REGULAR_DAMAGE))
        other.heal(damage)

        return true
    }
}
