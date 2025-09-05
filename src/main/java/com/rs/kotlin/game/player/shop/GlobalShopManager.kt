package com.rs.kotlin.game.player.shop

import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.player.tasks.Task

object GlobalShopManager {
    private val globalShops = LinkedHashMap<Int, ShopDefinition>() // preserves insertion order
    private val restockTasks = mutableMapOf<Int, Task>()

    fun registerShop(shop: ShopDefinition) {
        globalShops[shop.id] = shop
    }

    fun getShop(shopId: Int): ShopDefinition? {
        return globalShops[shopId]
    }

    @JvmStatic
    fun getShopsInOrder(): List<ShopDefinition> = globalShops.values.toList()
    
    /*private fun startRestockTask(shopId: Int) {
        restockTasks[shopId] = WorldTasksManager.schedule(0, 100) { // Restock every 100 ticks (60 seconds)
            globalShops[shopId]?.let { shop ->
                shop.items.forEach { item ->
                    if (item.maxStock != -1 && item.currentStock < item.maxStock) {
                        item.currentStock = minOf(item.maxStock, item.currentStock + item.restockRate)
                    }
                }
            }
        }
    }*/
}