package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object LouiesArmouredLegsBazaar : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Louie's Armoured Legs Bazaar"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = false

        generalStoreItem("item.bronze_platelegs", baseStock = 5)
        generalStoreItem("item.iron_platelegs", baseStock = 3)
        generalStoreItem("item.steel_platelegs", baseStock = 2)
        generalStoreItem("item.black_platelegs", baseStock = 1)
        generalStoreItem("item.mithril_platelegs", baseStock = 1)
        generalStoreItem("item.adamant_platelegs", baseStock = 1)
    }
}
