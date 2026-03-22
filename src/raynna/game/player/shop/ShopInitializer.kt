package raynna.game.player.shop

import raynna.game.player.shop.shops.*

object ShopInitializer {

    private val SHOPS: List<GameShop> = listOf(
        SuppliesShop,
        MeleeShop,
        RangeShop,
        MagicShop,
        AccessoriesShop,
        BarrowsShop,
        PvpShop,
        GeneralStore
    )

    @JvmStatic
    fun initializeShops() {
        SHOPS.forEach { shop ->
            GlobalShopManager.registerShop(shop.definition)
        }
    }
}
