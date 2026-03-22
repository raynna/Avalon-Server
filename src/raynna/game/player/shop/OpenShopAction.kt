package raynna.game.player.shop

import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.actions.Action
import raynna.game.player.shop.shops.AccessoriesShop
import raynna.game.player.shop.shops.BarrowsShop
import raynna.game.player.shop.shops.MagicShop
import raynna.game.player.shop.shops.MeleeShop
import raynna.game.player.shop.shops.RangeShop
import raynna.game.player.shop.shops.SuppliesShop

/**
 * Action that opens a shop by its index from StoreDialogue.
 *
 * @author Andreas (refactored)
 */
class OpenShopAction(
    private val shop: GameShop,
    private val ticks: Int
) : Action() {

    enum class ShopDisplay(
        val shop: GameShop,
        val iconItemId: Int
    ) {
        SUPPLY_STORE(SuppliesShop, 6685),         // Super restore potion
        MELEE_STORE(MeleeShop, 4587),          // Dragon scimitar
        RANGE_STORE(RangeShop, 861),           // Magic shortbow
        MAGIC_STORE(MagicShop, 4675),          // Ancient staff
        ACCESSORIES_STORE(AccessoriesShop, 1712),    // Amulet of glory
        BARROWS_STORE(BarrowsShop, 4720);        // Dharok’s platebody

        companion object {
            @JvmStatic
            fun valuesInOrder(): List<ShopDisplay> = entries.toList()

        }
    }


    override fun start(player: Player): Boolean {
        player.shopSystem.openShop(shop)
        stop(player)
        return true
    }

    override fun process(player: Player): Boolean {
        return true // nothing to cancel
    }

    override fun processWithDelay(player: Player): Int {
        return -1 // no repeat
    }

    override fun stop(player: Player) {
        setActionDelay(player, 3)
    }
}
