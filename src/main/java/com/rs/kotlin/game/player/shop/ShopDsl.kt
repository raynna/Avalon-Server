package com.rs.kotlin.game.player.shop

import com.rs.kotlin.Rscm

class ShopDsl {
    data class ShopItem(
        val itemId: Int,
        var currentStock: Int,
        val maxStock: Int,
        val restockRate: Int = 1,
        val price: Int? = null,
        val unlimitedStock: Boolean? = true
    )

    var id: Int = 0
    var title: String = ""
    var currency: CurrencyType = CurrencyType.COINS
    var items: MutableList<ShopItem> = mutableListOf()
    var isGlobal: Boolean = true

    fun shop(builder: ShopDsl.() -> Unit): ShopDefinition {
        val dsl = ShopDsl()
        dsl.builder()
        return ShopDefinition(
            id = dsl.id,
            title = dsl.title,
            currency = dsl.currency,
            items = dsl.items,
            isGlobal = dsl.isGlobal
        )
    }

    /** Utility: resolve either an itemId directly or an item.name */
    private fun resolveItemId(input: Any): Int {
        return when (input) {
            is Int -> input
            is String -> Rscm.lookup(input) // e.g. "item.abyssal_whip"
            else -> throw IllegalArgumentException("Unsupported item reference: $input")
        }
    }

    fun item(itemRef: Any, currentStock: Int = 1, price: Int? = null) {
        val itemId = resolveItemId(itemRef)
        items.add(
            ShopItem(
                itemId = itemId,
                currentStock = currentStock,
                maxStock = currentStock,
                price = price
            )
        )
    }

    fun item(itemRef: Any, currentStock: Int = 1, maxStock: Int = -1, restockRate: Int = 1, price: Int? = null, unlimitedStock: Boolean? = true) {
        val itemId = resolveItemId(itemRef)
        items.add(
            ShopItem(
                itemId = itemId,
                currentStock = currentStock,
                maxStock = maxStock,
                restockRate = restockRate,
                price = price,
                unlimitedStock = unlimitedStock
            )
        )
    }

    fun item(itemRef: Any, currentStock: Int = 1, maxStock: Int = -1, restockRate: Int = 1, price: Int? = null) {
        val itemId = resolveItemId(itemRef)
        items.add(
            ShopItem(
                itemId = itemId,
                currentStock = currentStock,
                maxStock = maxStock,
                restockRate = restockRate,
                price = price
            )
        )
    }

    fun items(vararg itemRefs: Any) {
        itemRefs.forEach { ref ->
            val itemId = resolveItemId(ref)
            items.add(
                ShopItem(
                    itemId = itemId,
                    currentStock = 1,
                    maxStock = -1,
                    restockRate = 1,
                    price = null
                )
            )
        }
    }
}
