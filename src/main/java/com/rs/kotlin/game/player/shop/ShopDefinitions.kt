package com.rs.kotlin.game.player.shop

object ShopDefinitions {
    fun shop(init: ShopDsl.() -> Unit): ShopDefinition {
        val dsl = ShopDsl().apply(init)
        return ShopDefinition(
            id = dsl.id,
            title = dsl.title,
            currency = dsl.currency,
            items = dsl.items,
            isGlobal = dsl.isGlobal
        )
    }
}

class ShopDefinition(
    val id: Int,
    val title: String,
    val currency: CurrencyType,
    val items: MutableList<ShopDsl.ShopItem>,
    val isGlobal: Boolean = true // Whether shop is global or personal
)