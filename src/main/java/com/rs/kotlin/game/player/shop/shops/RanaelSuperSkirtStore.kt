package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object RanaelSuperSkirtStore : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Ranael's Super Skirt Store"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = false

        generalStoreItem("item.bronze_plateskirt", baseStock = 5)
        generalStoreItem("item.iron_plateskirt", baseStock = 3)
        generalStoreItem("item.steel_plateskirt", baseStock = 2)
        generalStoreItem("item.black_plateskirt", baseStock = 1)
        generalStoreItem("item.mithril_plateskirt", baseStock = 1)
        generalStoreItem("item.adamant_plateskirt", baseStock = 1)
    }
}
