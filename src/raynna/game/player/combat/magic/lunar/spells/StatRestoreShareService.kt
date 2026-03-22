package raynna.game.player.combat.magic.lunar.spells

import raynna.game.World
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.content.Pots

object StatRestoreShareService {
    fun cast(
        player: Player,
        itemId: Int,
        slotId: Int,
    ): Boolean {
        val item = Item(itemId)
        val name = item.name.lowercase()

        if (!name.contains("restore") && !name.contains("sanfew")) {
            player.message("You can only use this spell on restore potions.")
            return false
        }

        if (player.isPotLocked) {
            return false
        }

        Pots.pot(player, item, slotId)

        val cleanName =
            item.name
                .replace("(6)", "")
                .replace("(5)", "")
                .replace("(4)", "")
                .replace("(3)", "")
                .replace("(2)", "")
                .replace("(1)", "")

        for (other in World.getPlayers()) {
            if (other == null || other === player) {
                continue
            }

            if (!other.withinDistance(player, 4)) {
                continue
            }

            if (!other.isAcceptAid) {
                continue
            }

            if (!other.isAtMultiArea) {
                continue
            }

            Pots.sharedPot(other, item, slotId)

            other.message("${player.displayName} shared a $cleanName dose with you.")
        }

        return true
    }
}
