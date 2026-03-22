package raynna.game.player.combat.magic.lunar.spells

import raynna.game.World
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.content.Pots

object BoostPotionShareService {
    fun cast(
        player: Player,
        itemId: Int,
        slotId: Int,
    ): Boolean {
        val item = Item(itemId)
        val name = item.name.lowercase()

        if (
            !name.contains("strength") &&
            !name.contains("attack") &&
            !name.contains("defence") &&
            !name.contains("combat") &&
            !name.contains("ranging") &&
            !name.contains("magic")
        ) {
            player.message(
                "You can only use this spell on strength, attack, defence, ranging, magic and combat potions.",
            )
            return false
        }

        if (player.isPotLocked) {
            return false
        }

        Pots.pot(player, item, slotId)

        for (other in World.getPlayers()) {
            if (other == null || other === player) {
                continue
            }

            if (other.withinDistance(player, 4) && other.isAcceptAid && other.isAtMultiArea) {
                Pots.sharedPot(other, item, slotId)

                val potionName =
                    item.name
                        .replace("(6)", "")
                        .replace("(5)", "")
                        .replace("(4)", "")
                        .replace("(3)", "")
                        .replace("(2)", "")
                        .replace("(1)", "")

                other.message("${player.displayName} shared a $potionName dose with you.")
            }
        }

        return true
    }
}
