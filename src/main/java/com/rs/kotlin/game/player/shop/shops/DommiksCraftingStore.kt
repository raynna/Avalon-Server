package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object DommiksCraftingStore : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Dommik's Crafting Store"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = false

        generalStoreItem("item.chisel", baseStock = 1)
        generalStoreItem("item.ring_mould", baseStock = 4)
        generalStoreItem("item.necklace_mould", baseStock = 2)
        generalStoreItem("item.amulet_mould", baseStock = 2)
        generalStoreItem("item.needle", baseStock = 3)
        generalStoreItem("item.thread", baseStock = 100)
        generalStoreItem("item.holy_mould", baseStock = 3)
        generalStoreItem("item.sickle_mould", baseStock = 6)
        generalStoreItem("item.tiara_mould", baseStock = 10)
        generalStoreItem("item.bolt_mould", baseStock = 10)
        generalStoreItem("item.bracelet_mould", baseStock = 5)
    }
}
