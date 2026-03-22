package raynna.game.player.shop

import raynna.core.tasks.WorldTask
import raynna.core.tasks.WorldTasksManager
import raynna.game.player.tasks.Task

object GlobalShopManager {
    private val globalShops = LinkedHashSet<ShopDefinition>() // preserves insertion order

    private var restockTaskStarted = false

    fun registerShop(shop: ShopDefinition) {
        globalShops += shop
        startRestockTaskIfNeeded()
    }

    private fun startRestockTaskIfNeeded() {
        if (restockTaskStarted) return
        restockTaskStarted = true

        WorldTasksManager.schedule(0, 10) {
            globalShops
                .filter { it.isGlobal }
                .forEach { shop ->

                    val beforeStocks = shop.items.map { it.currentStock }

                    val structureChanged = shop.restock()

                    val afterStocks = shop.items.map { it.currentStock }

                    when {
                        structureChanged -> rebuildViewers(shop)
                        beforeStocks != afterStocks -> refreshViewers(shop)
                    }
                }
        }
    }

    private fun refreshViewers(shop: ShopDefinition) {
        ShopSystem.getViewingPlayers(shop).forEach { player ->
            player.shopSystem.refresh(shop)
        }
    }

    private fun rebuildViewers(shop: ShopDefinition) {
        ShopSystem.getViewingPlayers(shop).forEach { player ->
            player.shopSystem.rebuild(shop)
        }
    }
}