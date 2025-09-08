package com.rs.kotlin.game.player.shop

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.core.packets.decode.WorldPacketsDecoder
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.utils.EconomyPrices
import com.rs.java.utils.HexColours
import com.rs.java.utils.ItemExamines
import com.rs.kotlin.game.world.util.Msg
import java.util.*

class ShopSystem(private val player: Player) {
    companion object {
        const val INTERFACE_ID = 3010
        private val viewingPlayers = WeakHashMap<Player, Int>()

        // Component IDs for the shop interface
        private val ITEM_CONTAINERS = intArrayOf(
            25,
            31,
            37,
            43,
            49,
            55,
            61,
            67,
            73,
            79,
            85,
            91,
            97,
            103,
            109,
            115,
            121,
            127,
            133,
            139,
            145,
            151,
            157,
            163,
            169,
            175,
            181,
            187,
            193,
            199,
            205,
            211,
            217,
            223,
            229,
            235,
            241,
            247,
            253,
            259,
            265,
            271,
            277,
            283,
            289,
            295,
            301,
            307,
            313,
            319,
            325,
            331,
            337,
            343,
            349,
            355,
            361,
            367,
            373,
            379,
            385,
            391,
            397,
            403,
            409,
            415,
            421,
            427,
            433,
            439,
            445,
            451,
            457,
            463,
            469,
            475,
            481,
            487
        )
        private const val TITLE_COMPONENT = 16
        private const val CURRENCY_COMPONENT = 17
    }

    fun openShop(shopId: Int) {
        val shop = GlobalShopManager.getShop(shopId) ?: return

        player.interfaceManager.closeScreenInterface()
        viewingPlayers[player] = shopId

        // Hide all item components initially
        ITEM_CONTAINERS.forEach { component ->
            player.packets.sendHideIComponent(INTERFACE_ID, component, true)
        }

        sendShop(shop)
        player.interfaceManager.sendInterface(INTERFACE_ID)
        player.interfaceManager.sendInventoryInterface(1266)

        player.setCloseInterfacesEvent {
            viewingPlayers.remove(player)
        }
    }

    private fun getCurrentShop(): ShopDefinition? {
        val shopId = viewingPlayers[player] ?: return null
        return GlobalShopManager.getShop(shopId)
    }

    private fun sendShop(shop: ShopDefinition) {
        // Clear all previous items
        for (i in 0 until ITEM_CONTAINERS.size) {
            player.packets.sendItems(i, emptyArray()) // Clear container
        }

        shop.items.forEachIndexed { index, shopItem ->
            if (index >= ITEM_CONTAINERS.size) return@forEachIndexed

            val containerId = index
            val componentId = ITEM_CONTAINERS[index]

            player.packets.sendHideIComponent(INTERFACE_ID, componentId, false)

            // Check if item is in stock
            val inStock = shopItem.maxStock == -1 || shopItem.currentStock > 0
            val displayAmount = if (shopItem.maxStock == -1) shopItem.currentStock
            else minOf(shopItem.currentStock, shopItem.maxStock)

            val price = shopItem.price ?: EconomyPrices.getPrice(shopItem.itemId)
            val canAfford = when (shop.currency) {
                CurrencyType.COINS -> player.totalCoins >= price
                CurrencyType.PVP_TOKENS -> player.pvpTokens >= price
                CurrencyType.AVALON_POINTS -> player.avalonPoints >= price
            }

            // Send the item to the correct container
            val item = Item(shopItem.itemId, if (inStock) displayAmount else 0)
            player.packets.sendItems(containerId, arrayOf(item))

            // Display price (red if out of stock or can't afford)
            val rawPriceText = when {
                !inStock -> "Out of<br>stock<br><br>" + price.shortFormat() + "<br><br><br><br>"
                price == 0 -> "Free"
                else -> price.shortFormat()
            }

            val priceColour = when {
                !inStock -> HexColours.Colour.RED
                !canAfford -> HexColours.Colour.RED
                price == 0 -> HexColours.Colour.GREEN
                else -> HexColours.Colour.YELLOW
            }

            // Apply colour after each <br>
            val priceText = rawPriceText.split("<br>").joinToString("<br>") { segment ->
                if (segment.isNotEmpty()) HexColours.getMessage(priceColour, segment) else ""
            }

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                componentId + 4,
                priceText
            )

            // Display currency sprite
            player.packets.sendSpriteOnIComponent(
                INTERFACE_ID,
                componentId + 5,
                shop.currency.spriteId
            )

            player.packets.sendHideIComponent(INTERFACE_ID, componentId + 3, true)

            player.packets.sendInterSetItemsOptionsScript(
                INTERFACE_ID,
                componentId + 2,
                containerId,
                6, 10,
                "Value", "Buy 1", "Buy 10", "Buy 50", "Buy X", "Examine"
            )
            player.packets.sendUnlockOptions(
                INTERFACE_ID,
                componentId + 2,
                0, 27, 0, 1, 2, 3, 4, 5
            )
            player.packets.sendItems(93, player.getInventory().getItems())
            player.packets.sendUnlockOptions(1266, 0, 0, 27, 0, 1, 2, 3, 4, 5)
            player.packets.sendInterSetItemsOptionsScript(
                1266, 0, 93, 4, 7, "Value", "Sell 1", "Sell 5", "Sell 10",
                "Sell 50", "Sell All"
            )
            player.packets.sendTextOnComponent(INTERFACE_ID, TITLE_COMPONENT, shop.title)
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                CURRENCY_COMPONENT,
                "${shop.currency.displayName}: ${
                    HexColours.getMessage(
                        HexColours.Colour.WHITE,
                        getPlayerCurrencyAmount(shop.currency).shortFormat()
                    )
                }"
            )
        }
    }

    private fun refresh(shop: ShopDefinition) {

        shop.items.forEachIndexed { index, shopItem ->
            if (index >= ITEM_CONTAINERS.size) return@forEachIndexed

            val containerId = index
            val componentId = ITEM_CONTAINERS[index]

            player.packets.sendHideIComponent(INTERFACE_ID, componentId, false)

            // Check if item is in stock
            val inStock = shopItem.maxStock == -1 || shopItem.currentStock > 0
            val displayAmount = if (shopItem.maxStock == -1) shopItem.currentStock
            else minOf(shopItem.currentStock, shopItem.maxStock)

            val price = shopItem.price ?: EconomyPrices.getPrice(shopItem.itemId)
            val canAfford = when (shop.currency) {
                CurrencyType.COINS -> player.totalCoins >= price
                CurrencyType.PVP_TOKENS -> player.pvpTokens >= price
                CurrencyType.AVALON_POINTS -> player.avalonPoints >= price
            }

            // Send the item to the correct container
            val item = Item(shopItem.itemId, if (inStock) displayAmount else 0)
            player.packets.sendUpdateItems(containerId, arrayOf(item))

            // Display price (red if out of stock or can't afford)
            val rawPriceText = when {
                !inStock -> "Out of<br>stock<br><br>" + price.shortFormat() + "<br><br><br><br>"
                price == 0 -> "Free"
                else -> price.shortFormat()
            }

            val priceColour = when {
                !inStock -> HexColours.Colour.RED
                !canAfford -> HexColours.Colour.RED
                price == 0 -> HexColours.Colour.GREEN
                else -> HexColours.Colour.YELLOW
            }

            // Apply colour after each <br>
            val priceText = rawPriceText.split("<br>").joinToString("<br>") { segment ->
                if (segment.isNotEmpty()) HexColours.getMessage(priceColour, segment) else ""
            }

            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                componentId + 4,
                priceText
            )

            // Display currency sprite
            player.packets.sendSpriteOnIComponent(
                INTERFACE_ID,
                componentId + 5,
                shop.currency.spriteId
            )

            player.packets.sendHideIComponent(INTERFACE_ID, componentId + 3, true)

            player.packets.sendUpdateItems(93, player.getInventory().getItems())
            player.packets.sendTextOnComponent(INTERFACE_ID, TITLE_COMPONENT, shop.title)
            player.packets.sendTextOnComponent(
                INTERFACE_ID,
                CURRENCY_COMPONENT,
                "${shop.currency.displayName}: ${
                    HexColours.getMessage(
                        HexColours.Colour.WHITE,
                        getPlayerCurrencyAmount(shop.currency).shortFormat()
                    )
                }"
            )
        }
    }

    fun handleItemOption(itemId: Int, option: Int) {
        val shop = getCurrentShop() ?: return
        when (option) {
            WorldPacketsDecoder.ACTION_BUTTON1_PACKET -> showItemInfo(itemId, shop.currency)
            WorldPacketsDecoder.ACTION_BUTTON2_PACKET -> buyItem(itemId, 1)
            WorldPacketsDecoder.ACTION_BUTTON3_PACKET -> buyItem(itemId, 10)
            WorldPacketsDecoder.ACTION_BUTTON4_PACKET -> buyItem(itemId, 50)
            WorldPacketsDecoder.ACTION_BUTTON5_PACKET -> handleBuyXOption(itemId)
            WorldPacketsDecoder.ACTION_BUTTON6_PACKET -> player.message(ItemExamines.getExamine(Item(itemId)))
        }
    }

    fun handleSellOption(item: Item, option: Int) {
        val shop = getCurrentShop() ?: return
        when (option) {
            WorldPacketsDecoder.ACTION_BUTTON1_PACKET -> showSellInfo(item.id, shop.currency)
            WorldPacketsDecoder.ACTION_BUTTON2_PACKET -> sellItem(item.id, 1)
            WorldPacketsDecoder.ACTION_BUTTON3_PACKET -> sellItem(item.id, 5)
            WorldPacketsDecoder.ACTION_BUTTON4_PACKET -> sellItem(item.id, 10)
            WorldPacketsDecoder.ACTION_BUTTON5_PACKET -> sellItem(item.id, 50)
            WorldPacketsDecoder.ACTION_BUTTON6_PACKET -> sellItem(item.id, item.amount)
        }
    }

    private fun handleBuyXOption(itemId: Int) {
        player.temporaryAttribute()["SHOP_BUY_X_ITEM"] = itemId

        player.packets.sendRunScript(108, "How many would you like to buy?")
    }

    fun handleBuyXInput(value: Int) {
        val itemId = player.temporaryAttribute().remove("SHOP_BUY_X_ITEM") as? Int ?: return

        if (value <= 0) {
            Msg.warn(player, "You must enter a positive amount.")
            return
        }

        buyItem(itemId, value)
    }

    private fun showItemInfo(itemId: Int, currency: CurrencyType) {
        val shop = getCurrentShop() ?: return

        // Find the shop item that matches the itemId
        val shopItem = shop.items.find { it.itemId == itemId }
        if (shopItem == null) {
            Msg.warn(player, "This item is not available in this shop.")
            return
        }

        // Get the price (use custom price if set, otherwise economy price)
        val price = shopItem.price ?: EconomyPrices.getPrice(shopItem.itemId)
        val itemDef = ItemDefinitions.getItemDefinitions(itemId)

        player.message("${itemDef.name} costs ${price.fullFormat()} ${currency.displayName}.")
    }

    private fun showSellInfo(itemId: Int, currency: CurrencyType) {
        val shop = getCurrentShop() ?: return

        val shopItem = shop.items.find { it.itemId == itemId }
        if (shopItem == null) {
            Msg.warn(player, "This item is not available in this shop.")
            return
        }

        val basePrice = shopItem.price ?: EconomyPrices.getPrice(shopItem.itemId)
        if (basePrice == 0) {
            Msg.warn(player, "You cannot sell free items to the shop.")
            return
        }
        val sellPrice = (basePrice * 0.66).toInt() // 66% of base price

        val itemDef = ItemDefinitions.getItemDefinitions(itemId)

        Msg.info(player, "${itemDef.name} will sell back for ${sellPrice.fullFormat()} ${currency.displayName}.")
    }


    fun buyItem(itemId: Int, amount: Int) {
        val shop = getCurrentShop() ?: return
        val shopItem = shop.items.find { it.itemId == itemId } ?: return

        var buyAmount = amount
        var buyItem = shopItem.itemId;
        // --- Cap to stock ---
        if (shopItem.maxStock != 1 && shopItem.unlimitedStock == false && buyAmount > shopItem.currentStock) {
            buyAmount = shopItem.currentStock
        }
        var def = ItemDefinitions.getItemDefinitions(itemId)
        if (buyAmount > 1 && !def.isStackable && !def.isNoted) {
            if (def.getCertId() != -1) {
                buyItem = def.getCertId();
            }
        }
        val price = shopItem.price ?: EconomyPrices.getPrice(buyItem)
        val currency = shop.currency

        // --- Cap to what player can afford ---
        val effectivePrice = price ?: 0;
        val playerCurrency = getPlayerCurrencyAmount(currency)
        val maxAffordable = if (effectivePrice <= 0) {
            buyAmount
        } else {
            playerCurrency / price
        }
        if (buyAmount > maxAffordable) {
            buyAmount = maxAffordable
        }
        def = ItemDefinitions.getItemDefinitions(buyItem)
        val freeSlots = player.inventory.freeSlots
        val stackableOrNoted = def.isStackable || def.isNoted

        // --- Cap to inventory space (only for non-stackable items) ---
        if (!stackableOrNoted) {
            if (buyAmount > freeSlots) {
                buyAmount = freeSlots
            }
        }

        // --- Final validation ---
        if (buyAmount <= 0) {
            when {
                shopItem.maxStock == 0 ->  Msg.warn(player,"The shop is out of stock.")
                maxAffordable == 0 -> Msg.warn(player,"You don't have enough ${currency.displayName}.")
                freeSlots == 0 ->  Msg.warn(player,"You don't have enough inventory space.")
            }
            return
        }

        val totalPrice = buyAmount * price

        // --- Process purchase ---
        removeCurrency(currency, totalPrice)
        player.inventory.addItem(buyItem, buyAmount)

        // Reduce stock if not infinite
        if (shopItem.maxStock != 1 && shopItem.unlimitedStock == false && price > 0) {
            shopItem.currentStock -= buyAmount
        }

        Msg.success(player, "You bought $buyAmount x ${def.name} for ${totalPrice.fullFormat()} ${currency.displayName}.")
        refresh(shop)
    }

    fun sellItem(itemId: Int, amount: Int) {
        val shop = getCurrentShop()?: return
        val currency = shop.currency
        var sellAmount = amount
        if (sellAmount > player.inventory.getNumberOf(itemId))
            sellAmount = player.inventory.getAmountOf(itemId)

        val price = (EconomyPrices.getPrice(itemId) * 0.66).toInt()
        if (price <= 0) {
            Msg.warn(player, "You can't sell this item.")
            return
        }
        val sellPrice = price * sellAmount


        when (currency) {
            CurrencyType.COINS -> player.moneyPouch.addMoney(sellPrice, false)
            CurrencyType.PVP_TOKENS -> {
                val tokenId = Item.getId("item.pvp_token")
                if (!player.inventory.canHold(tokenId, 1)) {
                    Msg.warn(player, "You don't have any inventory space.")
                    return
                }
                player.addItem(tokenId, sellPrice)
            }

            CurrencyType.AVALON_POINTS -> player.avalonPoints += sellPrice
        }
        player.inventory.deleteItem(itemId, sellAmount)

        val itemDef = ItemDefinitions.getItemDefinitions(itemId)
        Msg.success(player, "Sold $sellAmount x ${itemDef.name} for ${sellPrice.fullFormat()} ${currency.displayName}.")
        refresh(shop)
    }

    private fun getPlayerCurrencyAmount(currency: CurrencyType): Int {
        return when (currency) {
            CurrencyType.COINS -> player.totalCoins
            CurrencyType.PVP_TOKENS -> player.pvpTokens
            CurrencyType.AVALON_POINTS -> player.avalonPoints
        }
    }

    private fun removeCurrency(currency: CurrencyType, amount: Int) {
        when (currency) {
            CurrencyType.COINS -> player.canBuy(amount)
            CurrencyType.PVP_TOKENS -> player.inventory.deleteItem("item.pvp_token", amount);
            CurrencyType.AVALON_POINTS -> player.avalonPoints -= amount
        }
    }

    private fun Int.shortFormat(): String {
        return when {
            this >= 10_000_000 -> "${this / 1_000_000}m"
            this >= 10_000 -> "${this / 1_000}k"
            else -> toString()
        }
    }

    private fun Int.fullFormat(): String {
        return "%,d".format(this) // adds commas: 1,479 → "1,479"
    }
}