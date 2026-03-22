package raynna.game.player.shop.shops

import raynna.game.player.shop.*
import raynna.game.player.shop.ShopDefinitions.shop

object AuburyRuneShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Aubury's Rune Shop"
        currency = CurrencyType.COINS
        isGlobal = true
        isGeneralStore = false

        generalStoreItem("item.air_rune", baseStock = 5000)
        generalStoreItem("item.fire_rune", baseStock = 5000)
        generalStoreItem("item.water_rune", baseStock = 5000)
        generalStoreItem("item.earth_rune", baseStock = 5000)
        generalStoreItem("item.mind_rune", baseStock = 5000)
        generalStoreItem("item.body_rune", baseStock = 5000)
        generalStoreItem("item.chaos_rune", baseStock = 250)
        generalStoreItem("item.death_rune", baseStock = 250)
    }
}
