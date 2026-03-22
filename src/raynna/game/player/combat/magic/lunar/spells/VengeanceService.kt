package raynna.game.player.combat.magic.lunar.spells

import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.TickManager

object VengeanceService {
    fun cast(player: Player): Boolean {
        if (player.tickManager.isActive(TickManager.TickKeys.VENGEANCE_COOLDOWN)) {
            player.message("You can only cast vengeance every 30 seconds.")
            return false
        }

        player.gfx(726, 100, 0)
        player.animate(4410)

        player.setVengeance(true)

        player.tickManager.addSeconds(
            TickManager.TickKeys.VENGEANCE_COOLDOWN,
            30,
        ) {
            player.message("You can now cast vengeance again.")
        }

        player.message("You cast a vengeance.")

        return true
    }
}
