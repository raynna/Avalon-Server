package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.item.meta.RuneEssencePouchMetaData
import raynna.game.player.Player

object RepairRunePouchService {
    fun cast(player: Player): Boolean {
        var repaired = false

        for (item in player.inventory.items.containerItems) {
            val meta = item?.metadata as? RuneEssencePouchMetaData ?: continue

            if (meta.isDegraded) {
                meta.repair()
                repaired = true
            }
        }

        if (!repaired) {
            player.message("None of your rune pouches need repairing.")
            return false
        }

        player.animate(Animation(6297))
        player.gfx(Graphics(1062))

        return true
    }
}
