package com.rs.kotlin.game.player.shop

import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.Action
import com.rs.kotlin.game.player.shop.shops.AccessoriesShop
import com.rs.kotlin.game.player.shop.shops.BarrowsShop
import com.rs.kotlin.game.player.shop.shops.MagicShop
import com.rs.kotlin.game.player.shop.shops.MeleeShop
import com.rs.kotlin.game.player.shop.shops.RangeShop
import com.rs.kotlin.game.player.shop.shops.SuppliesShop

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
