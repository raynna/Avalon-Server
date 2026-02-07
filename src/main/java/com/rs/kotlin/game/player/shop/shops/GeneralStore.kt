package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object GeneralStore : GameShop {
    override val definition: ShopDefinition = shop {
        title = "General Store"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = true

        generalStoreItem("item.chisel", baseStock = 10)
        generalStoreItem("item.spade", baseStock = 10)
        generalStoreItem("item.shears", baseStock = 10)
    }
}
