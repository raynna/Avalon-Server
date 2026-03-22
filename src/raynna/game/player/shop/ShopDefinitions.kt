package raynna.game.player.shop

import raynna.core.cache.defintions.ItemDefinitions
import raynna.util.EconomyPrices

object ShopDefinitions {
    fun shop(init: ShopDsl.() -> Unit): ShopDefinition {
        val dsl = ShopDsl().apply(init)
        return ShopDefinition(
            title = dsl.title,
            currency = dsl.currency,
            items = dsl.items,
            isGlobal = dsl.isGlobal,
            isGeneralStore = dsl.isGeneralStore,
        )
    }
}

class ShopDefinition(
    val title: String,
    val currency: CurrencyType,
    val items: MutableList<ShopDsl.ShopItem>,
    val isGlobal: Boolean = true,
    val isGeneralStore: Boolean = false,
) {
    fun getBuyPrice(
        itemId: Int,
        shopItem: ShopDsl.ShopItem?,
    ): Int =
        when {
            // General stores always use the item's base cache price
            isGeneralStore -> ItemDefinitions.getItemDefinitions(itemId).price

            // Explicit per-item price defined in the DSL takes next priority
            shopItem?.price != null -> shopItem.price

            else -> ShopPriceManager.getPrice(itemId)
        }

    fun getSellPrice(
        itemId: Int,
        shopItem: ShopDsl.ShopItem?,
    ): Int =
        when {
            isGeneralStore -> ItemDefinitions.getItemDefinitions(itemId).lowAlchPrice
            shopItem?.price != null -> (shopItem.price * 0.66).toInt()
            else -> (ShopPriceManager.getPrice(itemId) * 0.66).toInt()
        }

    fun removeIfDepleted(item: ShopDsl.ShopItem): Boolean {
        if (isGeneralStore && item.baseStock == 0 && item.currentStock <= 0) {
            items.remove(item)
            return true
        }
        return false
    }

    fun increaseStock(
        itemId: Int,
        amount: Int,
    ) {
        val item = items.find { it.itemId == itemId } ?: return

        if (item.unlimitedStock == true) return

        item.currentStock =
            when {
                item.maxStock == -1 -> item.currentStock + amount
                else -> minOf(item.maxStock, item.currentStock + amount)
            }
    }

    fun addGeneralStoreItem(
        itemId: Int,
        amount: Int,
    ) {
        val existing = items.find { it.itemId == itemId }
        if (existing != null) {
            increaseStock(itemId, amount)
            return
        }

        items.add(
            ShopDsl.ShopItem(
                itemId = itemId,
                currentStock = amount,
                maxStock = amount * 2, // sensible cap
                restockRate = 1,
                price = null,
                unlimitedStock = false,
                baseStock = 0,
            ),
        )
    }

    fun restock(): Boolean {
        var structureChanged = false

        for (item in items.toList()) {
            when {
                item.currentStock < item.baseStock -> {
                    item.currentStock =
                        minOf(
                            item.baseStock,
                            item.currentStock + item.restockRate,
                        )
                }

                isGeneralStore && item.currentStock > item.baseStock -> {
                    item.currentStock -= item.restockRate
                }
            }

            if (isGeneralStore && item.baseStock == 0 && item.currentStock <= 0) {
                items.remove(item)
                structureChanged = true
            }
        }

        return structureChanged
    }
}
