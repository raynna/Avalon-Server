package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object BarrowsShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Barrows Store"
        currency = CurrencyType.COINS
        isGlobal = true

        for (i in 4708..4738 step 2) {
            item(i, 1)
        }
        for (i in 4745..4759 step 2) {
            item(i, 1)
        }
        for (i in 21736..21760 step 8) {
            item(i, 1)
        }
    }
}
