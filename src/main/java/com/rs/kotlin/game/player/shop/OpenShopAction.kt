package com.rs.kotlin.game.player.shop

import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.Action

/**
 * Action that opens a shop by its index from StoreDialogue.
 *
 * @author Andreas (refactored)
 */
class OpenShopAction(
    private val shopIndex: Int,
    private val ticks: Int
) : Action() {

    enum class ShopDisplay(
        val shopId: Int,
        val iconItemId: Int
    ) {
        SUPPLY_STORE(1, 6685),         // Super restore potion
        MELEE_STORE(2, 4587),          // Dragon scimitar
        RANGE_STORE(3, 861),           // Magic shortbow
        MAGIC_STORE(4, 4675),          // Ancient staff
        ACCESSORIES_STORE(5, 1712),    // Amulet of glory
        BARROWS_STORE(6, 4720);        // Dharok’s platebody

        companion object {
            @JvmStatic
            fun valuesInOrder(): List<ShopDisplay> = entries.toList()

        }
    }


    override fun start(player: Player): Boolean {
        val shop = GlobalShopManager.getShop(shopIndex) ?: return false
        player.shopSystem.openShop(shop.id)
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
