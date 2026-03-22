package raynna.game.player.combat.magic.lunar.spells

import raynna.core.tasks.WorldTask
import raynna.core.tasks.WorldTasksManager
import raynna.game.Animation
import raynna.game.ForceTalk
import raynna.game.Graphics
import raynna.game.minigames.duel.DuelArena
import raynna.game.player.Player
import raynna.util.Utils

object DreamService {
    private const val COOLDOWN = 30_000L
    private const val DREAM_ATTR = "Dream"
    private const val DREAMING_ATTR = "Dreaming"

    fun cast(player: Player): Boolean {
        if (player.isInCombat || player.controlerManager.controler is DuelArena) {
            player.message("You can't dream right now.")
            return false
        }

        val attrs = player.temporaryAttribute()

        if (attrs[DREAMING_ATTR] == true) {
            player.message("You are already dreaming!")
            return false
        }

        val lastCast = attrs[DREAM_ATTR] as? Long
        if (lastCast != null && lastCast + COOLDOWN > Utils.currentTimeMillis()) {
            player.message("You can only cast this spell every 30 seconds.")
            return false
        }

        player.stopAll()
        player.lock(6)

        player.animate(Animation(6295))
        player.gfx(Graphics(277))

        attrs[DREAM_ATTR] = Utils.currentTimeMillis()
        attrs[DREAMING_ATTR] = true

        WorldTasksManager.schedule(
            object : WorldTask() {
                override fun run() {
                    if (attrs[DREAMING_ATTR] == true) {
                        player.nextForceTalk = ForceTalk("Zzzzz...")
                        player.animate(Animation(6296))
                        player.gfx(Graphics(277))
                    } else {
                        stop()
                    }
                }
            },
            5,
            15,
        )

        return true
    }
}
