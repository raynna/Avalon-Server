package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.player.Player

object CureMeService {
    fun cast(player: Player): Boolean {
        if (!player.poison.isPoisoned && !player.newPoison.isPoisoned()) {
            player.message("You are not poisoned.")
            return false
        }

        player.poison.reset()
        player.newPoison.reset()

        player.animate(Animation(4411))
        player.gfx(Graphics(746, 0, 100))

        player.message("You cure yourself of poison.")

        return true
    }
}
