package raynna.game.player.combat.magic.modern.spells

import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.content.ItemConstants

object AlchemyService {
    fun cast(
        player: Player,
        itemId: Int,
        slotId: Int,
        fireStaff: Boolean,
        lowAlch: Boolean,
    ): Boolean {
        if (player.isLocked || player.hasSpellDelay()) return false

        if (!player.inventory.containsItem(itemId, 1)) return false

        val item = player.inventory.getItem(slotId) ?: return false

        if (!ItemConstants.isTradeable(item)) {
            player.message("You cannot cast an alchemy spell on untradeables.")
            return false
        }

        if (itemId == 995) {
            player.message("You cannot cast an alchemy spell on coins.")
            return false
        }

        var defs = ItemDefinitions.getItemDefinitions(itemId)

        if (defs.isNoted) {
            defs = ItemDefinitions.getItemDefinitions(defs.certId)
        }

        player.animate(if (fireStaff) Animation(9633) else Animation(713))
        player.gfx(if (fireStaff) Graphics(1693) else Graphics(113))

        player.castSpellDelay(3)

        player.inventory.deleteItem(slotId, Item(itemId))

        val coins = if (lowAlch) defs.lowAlchPrice else defs.highAlchPrice

        player.moneyPouch.addMoney(coins, false)

        player.packets.sendGlobalVar(168, 7)

        player.lock(1)

        return true
    }
}
