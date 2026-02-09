package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object GemTrader : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Gem Trader"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = false

        generalStoreItem("item.uncut_sapphire", baseStock = 1)
        generalStoreItem("item.uncut_emerald", baseStock = 1)
        generalStoreItem("item.uncut_ruby", baseStock = 0)
        generalStoreItem("item.uncut_diamond", baseStock = 0)
        generalStoreItem("item.sapphire", baseStock = 1)
        generalStoreItem("item.emerald", baseStock = 1)
        generalStoreItem("item.ruby", baseStock = 0)
        generalStoreItem("item.diamond", baseStock = 0)
    }
}
