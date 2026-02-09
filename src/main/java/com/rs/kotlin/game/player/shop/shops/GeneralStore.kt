package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object GeneralStore : GameShop {
    override val definition: ShopDefinition = shop {
        title = "General Store"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = true

        generalStoreItem("item.empty_pot", baseStock = 5)
        generalStoreItem("item.empty_jug", baseStock = 2)
        generalStoreItem("item.shears", baseStock = 2)
        generalStoreItem("item.empty_bucket", baseStock = 3)
        generalStoreItem("item.bowl", baseStock = 2)
        generalStoreItem("item.cake_tin", baseStock = 2)
        generalStoreItem("item.tinderbox", baseStock = 2)
        generalStoreItem("item.chisel", baseStock = 2)
        generalStoreItem("item.hammer", baseStock = 5)
        generalStoreItem("item.knife", baseStock = 1)
        generalStoreItem("item.newcomer_map", baseStock = 5)
        generalStoreItem("item.security_book", baseStock = 5)
    }
}
