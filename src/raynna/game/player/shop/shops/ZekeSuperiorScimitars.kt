package raynna.game.player.shop.shops

import raynna.game.player.shop.*
import raynna.game.player.shop.ShopDefinitions.shop

object ZekeSuperiorScimitars : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Zeke's Superior Scimitars"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = false

        generalStoreItem("item.bronze_scimitar", baseStock = 5)
        generalStoreItem("item.iron_scimitar", baseStock = 3)
        generalStoreItem("item.steel_scimitar", baseStock = 2)
        generalStoreItem("item.mithril_scimitar", baseStock = 1)
    }
}
