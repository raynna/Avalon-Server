package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Entity
import raynna.game.Graphics
import raynna.game.player.Player
import raynna.game.player.TickManager

object VengeanceOtherService {
    fun cast(
        player: Player,
        target: Entity,
    ): Boolean {
        val other = target as? Player ?: return false

        if (player.tickManager.isActive(TickManager.TickKeys.VENGEANCE_COOLDOWN)) {
            player.message("You can only cast vengeance every 30 seconds.")
            return false
        }

        if (other.tickManager.isActive(TickManager.TickKeys.VENGEANCE_COOLDOWN)) {
            player.message("${other.displayName} can only cast vengeance every 30 seconds.")
            return false
        }

        if (!other.isAcceptAid) {
            player.message("${other.displayName} doesn't have aid on.")
            return false
        }

        if (!other.isAtMultiArea) {
            player.message("You can only cast this spell in a multi-area.")
            return false
        }

        player.animate(Animation(4411))
        other.gfx(Graphics(725, 0, 100))

        other.message("${player.displayName} cast a vengeance spell on you.")

        other.setVengeance(true)

        other.tickManager.addSeconds(TickManager.TickKeys.VENGEANCE_COOLDOWN, 30)
        player.tickManager.addSeconds(
            TickManager.TickKeys.VENGEANCE_COOLDOWN,
            30,
        ) {
            player.message("You can now cast vengeance again.")
        }

        return true
    }
}
