package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.WorldObject
import raynna.game.player.Player
import raynna.game.player.actions.skills.farming.FarmingManager

object CurePlantService {
    fun cast(
        player: Player,
        obj: WorldObject,
    ): Boolean {
        val spotInfo = FarmingManager.SpotInfo.getInfo(obj.id)

        if (spotInfo != null) {
            val spot = player.farmingManager.getSpot(spotInfo) ?: return false

            if (!spot.isDiseased) {
                player.message("This patch doesn't need curing.")
                return false
            }
            player.animate(Animation(4432))
            player.gfx(Graphics(748))
            player.farmingManager.startCureAction(spot, null)
            return true
        }

        val config = obj.configByFile
        if (config != -1) {
            player.animate(Animation(4432))
            player.gfx(Graphics(748))
            player.varsManager.forceSendVarBit(config, 4)
            return true
        }
        player.message("You can't cast cure plant on this object.")

        return false
    }
}
