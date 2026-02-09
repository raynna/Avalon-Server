package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object FancyClothesStore : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Fancy Clothes Store"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = false

        generalStoreItem("item.chef_s_hat", baseStock = 0)
        generalStoreItem("item.wizard_hat", baseStock = 3)
        generalStoreItem("item.yellow_cape", baseStock = 1)
        generalStoreItem("item.grey_wolf_fur", baseStock = 3)
        generalStoreItem("item.bear_fur", baseStock = 3)
        generalStoreItem("item.needle", baseStock = 3)
        generalStoreItem("item.thread", baseStock = 100)
        generalStoreItem("item.leather_gloves", baseStock = 10)
        generalStoreItem("item.leather_boots", baseStock = 10)
        generalStoreItem("item.priest_gown_2", baseStock = 3)
        generalStoreItem("item.priest_gown", baseStock = 3)
        generalStoreItem("item.brown_apron", baseStock = 1)
        generalStoreItem("item.pink_skirt", baseStock = 5)
        generalStoreItem("item.black_robe_skirt", baseStock = 3)
        generalStoreItem("item.wizard_robe_skirt", baseStock = 2)
        generalStoreItem("item.red_cape", baseStock = 3)
    }
}
