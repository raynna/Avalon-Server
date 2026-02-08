package com.rs.kotlin.game.player.shop

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.utils.EconomyPrices

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
    val isGeneralStore: Boolean = false

) {

    fun getBuyPrice(itemId: Int, shopItem: ShopDsl.ShopItem?): Int {
        return when {
            isGeneralStore ->
                ItemDefinitions.getItemDefinitions(itemId).price

            shopItem?.price != null ->
                shopItem.price

            else ->
                EconomyPrices.getPrice(itemId)
        }
    }

    fun getSellPrice(itemId: Int, shopItem: ShopDsl.ShopItem?): Int {
        return when {
            isGeneralStore ->
                ItemDefinitions.getItemDefinitions(itemId).price

            shopItem?.price != null -> (shopItem.price * 0.66).toInt()

            else ->
                (EconomyPrices.getPrice(itemId) * 0.66).toInt()
        }
    }

    fun removeIfDepleted(item: ShopDsl.ShopItem): Boolean {
        if (isGeneralStore && item.baseStock == 0 && item.currentStock <= 0) {
            items.remove(item)
            return true
        }
        return false
    }

    fun increaseStock(itemId: Int, amount: Int) {
        val item = items.find { it.itemId == itemId } ?: return

        if (item.unlimitedStock == true) return

        item.currentStock = when {
            item.maxStock == -1 -> item.currentStock + amount
            else -> minOf(item.maxStock, item.currentStock + amount)
        }
    }

    fun addGeneralStoreItem(itemId: Int, amount: Int) {
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
                baseStock = 0
            )
        )
    }

    fun restock(): Boolean {
        var structureChanged = false

        for (item in items.toList()) {
            when {
                item.currentStock < item.baseStock -> {
                    item.currentStock = minOf(
                        item.baseStock,
                        item.currentStock + item.restockRate
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