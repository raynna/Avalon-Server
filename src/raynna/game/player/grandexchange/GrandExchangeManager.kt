package raynna.game.player.grandexchange

import raynna.app.Settings
import raynna.core.cache.defintions.ItemDefinitions
import raynna.core.packets.decode.WorldPacketsDecoder
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.content.ItemConstants
import raynna.game.player.content.Shop
import raynna.util.ItemExamines
import raynna.util.Utils
import java.io.Serializable

/**
 * Per-player Grand Exchange controller.
 *
 * Holds the player's six offer slots ([offerUIds]) and personal offer history.
 * All heavy lifting (matching, persistence) is delegated to [GrandExchange].
 *
 * ### Client var state
 * - [VAR_ITEM_ID]      — item being bought/sold (-1 = none selected)
 * - [VAR_AMOUNT]       — quantity
 * - [VAR_PRICE_PER]    — price per item
 * - [VAR_SLOT]         — active offer slot (-1 = none)
 * - [VAR_TYPE]         — 0 = buy, 1 = sell, -1 = idle
 * - [VAR_MARKET_PRICE] — guide price (display only)
 */
class GrandExchangeManager : Serializable {
    val offerUIds = LongArray(MAX_SLOTS)
    private val history = arrayOfNulls<OfferHistory>(HISTORY_SIZE)

    @Transient private lateinit var player: Player

    fun setPlayer(player: Player) {
        this.player = player
    }

    fun init() = GrandExchange.linkOffers(player)

    fun stop() = GrandExchange.unlinkOffers(player)

    fun isSlotFree(slot: Int) = offerUIds[slot] == 0L

    private fun firstFreeSlot() = offerUIds.indexOfFirst { it == 0L }

    fun getCurrentSlot(): Int = player.varsManager.getValue(VAR_SLOT)

    fun getType(): Int = player.varsManager.getValue(VAR_TYPE)

    fun getItemId(): Int = player.varsManager.getValue(VAR_ITEM_ID)

    fun getAmount(): Int = player.varsManager.getValue(VAR_AMOUNT)

    fun getPricePerItem(): Int = player.varsManager.getValue(VAR_PRICE_PER)

    fun setAmount(amount: Int) = player.varsManager.sendVar(VAR_AMOUNT, amount)

    fun setPricePerItem(price: Int) = player.varsManager.sendVar(VAR_PRICE_PER, price)

    private fun setSlot(slot: Int) = player.varsManager.sendVar(VAR_SLOT, slot)

    private fun setType(type: Int) = player.varsManager.sendVar(VAR_TYPE, type)

    private fun setItemId(id: Int) = player.varsManager.sendVar(VAR_ITEM_ID, id)

    private fun setMarketPrice(price: Int) = player.varsManager.sendVar(VAR_MARKET_PRICE, price)

    fun addOfferHistory(entry: OfferHistory) {
        System.arraycopy(history, 0, history, 1, history.size - 1)
        history[0] = entry
    }

    fun openHistory() {
        player.interfaceManager.sendInterface(643)
        for (i in history.indices) {
            val o = history[i]
            player.packets.sendTextOnComponent(643, 25 + i, o?.let { if (it.isBought) "You bought" else "You sold" } ?: "")
            player.packets.sendTextOnComponent(643, 35 + i, o?.let { ItemDefinitions.getItemDefinitions(it.id).name } ?: "")
            player.packets.sendTextOnComponent(
                643,
                30 + i,
                o?.let { Utils.getFormattedNumber(it.quantity.toDouble(), ',').toString() } ?: "",
            )
            player.packets.sendTextOnComponent(643, 40 + i, o?.let { Utils.getFormattedNumber(it.price.toDouble(), ',').toString() } ?: "")
        }
    }

    fun openGrandExchange() {
        player.interfaceManager.sendInterface(105)
        player.packets.sendUnlockOptions(105, 206, -1, 0, 0, 1)
        player.packets.sendUnlockOptions(105, 208, -1, 0, 0, 1)
        cancelOffer()
        player.setCloseInterfacesEvent {
            player.packets.sendRunScript(571)
            player.interfaceManager.removeInterface(752, 7)
        }
    }

    fun openCollectionBox() {
        player.interfaceManager.sendInterface(109)
        intArrayOf(19, 23, 27, 32, 37, 42).forEach {
            player.packets.sendUnlockOptions(109, it, 0, 2, 0, 1)
        }
    }

    fun handleButtons(
        interfaceId: Int,
        componentId: Int,
        slotId: Int,
        packetId: Int,
    ) {
        when (interfaceId) {
            INTERFACE_GE -> handleGEInterface(componentId, slotId, packetId)
            INTERFACE_INV_SELL -> if (componentId == 18) handleInventoryOffer(slotId)
            INTERFACE_ITEM_INFO -> if (componentId == 1) player.interfaceManager.closeInventoryInterface()
            INTERFACE_COLLECTION -> handleCollectionBox(componentId, slotId, packetId)
        }
    }

    private fun handleGEInterface(
        componentId: Int,
        slotId: Int,
        packetId: Int,
    ) {
        val btn1 = packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET
        val isBuy = getType() == TYPE_BUY
        when (componentId) {
            19 -> if (btn1) viewOffer(0) else abortOffer(0)
            35 -> if (btn1) viewOffer(1) else abortOffer(1)
            51 -> if (btn1) viewOffer(2) else abortOffer(2)
            70 -> if (btn1) viewOffer(3) else abortOffer(3)
            89 -> if (btn1) viewOffer(4) else abortOffer(4)
            108 -> if (btn1) viewOffer(5) else abortOffer(5)
            31 -> makeOffer(0, sell = false)
            32 -> makeOffer(0, sell = true)
            47 -> makeOffer(1, sell = false)
            48 -> makeOffer(1, sell = true)
            63 -> makeOffer(2, sell = false)
            64 -> makeOffer(2, sell = true)
            82 -> makeOffer(3, sell = false)
            83 -> makeOffer(3, sell = true)
            101 -> makeOffer(4, sell = false)
            102 -> makeOffer(4, sell = true)
            120 -> makeOffer(5, sell = false)
            121 -> makeOffer(5, sell = true)
            220 -> player.interfaceManager.closeScreenInterface()
            200 -> abortCurrentOffer()
            128 -> cancelOffer()
            186 -> confirmOffer()
            190 -> chooseItem()
            155 -> requireItem { modifyAmount(getAmount() - 1) }
            157 -> requireItem { modifyAmount(getAmount() + 1) }
            160 -> requireItem { modifyAmount(if (isBuy) getAmount() + 1 else 1) }
            162 -> requireItem { modifyAmount(if (isBuy) getAmount() + 10 else 10) }
            164 -> requireItem { modifyAmount(if (isBuy) getAmount() + 100 else 100) }
            166 -> modifyAmount(if (isBuy) getAmount() + 1000 else getItemAmount(Item(getItemId())))
            168 -> editAmount()
            169 -> modifyPricePerItem(getPricePerItem() - 1)
            171 -> modifyPricePerItem(getPricePerItem() + 1)
            175 -> modifyPricePerItem(GrandExchange.getPrice(getItemId()))
            177 -> editPrice()
            179 -> modifyPricePerItem((getPricePerItem() * 1.05).toInt())
            181 -> modifyPricePerItem((getPricePerItem() * 0.95).toInt())
            206 -> collectItems(getCurrentSlot(), 0, if (btn1) 0 else 1)
            208 -> collectItems(getCurrentSlot(), 1, if (btn1) 0 else 1)
        }
    }

    private fun handleInventoryOffer(slotId: Int) {
        if (getCurrentSlot() == -1) {
            val free = firstFreeSlot()
            if (free == -1) {
                player.packets.sendGameMessage("All your Grand Exchange slots are full.")
                return
            }
            setType(TYPE_SELL)
            setSlot(free)
            player.packets.sendHideIComponent(105, 196, true)
            player.packets.sendHideIComponent(105, 220, true)
        }
        offer(slotId)
    }

    private fun handleCollectionBox(
        componentId: Int,
        slotId: Int,
        packetId: Int,
    ) {
        val invSlot = if (slotId == 0) 0 else 1
        val option = if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) 0 else 1
        when (componentId) {
            19 -> collectItems(0, invSlot, option)
            23 -> collectItems(1, invSlot, option)
            27 -> collectItems(2, invSlot, option)
            32 -> collectItems(3, invSlot, option)
            37 -> collectItems(4, invSlot, option)
            42 -> collectItems(5, invSlot, option)
        }
    }

    /**
     * Resets the pending-offer UI. If the GE main screen is still open,
     * restores the inventory panel so the player can immediately make another offer.
     */
    fun cancelOffer() {
        setItemId(-1)
        setAmount(0)
        setPricePerItem(1)
        setMarketPrice(0)
        setSlot(-1)
        if (getType() == TYPE_BUY) player.packets.sendRunScript(571)
        player.interfaceManager.closeInventoryInterface()
        player.interfaceManager.removeInterface(752, 7)
        setType(-1)
        if (player.interfaceManager.containsInterface(105)) {
            sendInventoryOfferInterface()
        }
    }

    fun makeOffer(
        slot: Int,
        sell: Boolean,
    ) {
        if (!isSlotFree(slot) || getCurrentSlot() != -1) return
        setType(if (sell) TYPE_SELL else TYPE_BUY)
        setSlot(slot)
        player.packets.sendHideIComponent(105, 196, true)
        player.packets.sendHideIComponent(105, 220, true)
        if (sell) sendInventoryOfferInterface() else openItemSearch()
    }

    fun confirmOffer() {
        val type = getType()
        if (type == -1) return
        val slot = getCurrentSlot()
        if (slot == -1 || !isSlotFree(slot)) return

        val isBuy = type == TYPE_BUY
        val itemId = getItemId()
        if (itemId == -1) {
            player.packets.sendGameMessage("You must choose an item to ${if (isBuy) "buy" else "sell"}!")
            return
        }
        val amount = getAmount()
        if (amount <= 0) {
            player.packets.sendGameMessage("You must choose the quantity you wish to ${if (isBuy) "buy" else "sell"}!")
            return
        }
        val pricePerItem = getPricePerItem()
        if (pricePerItem > 0 && amount > Int.MAX_VALUE / pricePerItem) {
            player.packets.sendGameMessage("You do not have enough coins to cover the offer.")
            return
        }

        if (isBuy) {
            val total = pricePerItem * amount
            if (!player.hasMoney(total)) {
                player.packets.sendGameMessage("You do not have enough coins to cover the offer.")
                return
            }
            player.takeMoney(total)
        } else {
            if (getItemAmount(Item(itemId)) < amount) {
                player.packets.sendGameMessage("You do not have enough of this item in your inventory to cover the offer.")
                return
            }
            if (GrandExchange.getPrice(itemId) == 0) {
                player.packets.sendGameMessage("You can't sell free items to the grand exchange.")
                return
            }
            val notedId = ItemDefinitions.getItemDefinitions(itemId).certId
            val notedAmount = player.inventory.getAmountOf(notedId)
            if (notedAmount < amount) {
                player.inventory.deleteItem(notedId, notedAmount)
                player.inventory.deleteItem(itemId, amount - notedAmount)
            } else {
                player.inventory.deleteItem(notedId, amount)
            }
        }

        GrandExchange.sendOffer(player, slot, itemId, amount, pricePerItem, isBuy)
        cancelOffer()
    }

    fun viewOffer(slot: Int) {
        if (isSlotFree(slot) || getCurrentSlot() != -1) return
        val offer = GrandExchange.getOffer(player, slot) ?: return
        setType(if (offer.isBuying) TYPE_BUY else TYPE_SELL)
        setSlot(slot)
        setExtraDetails(offer.id)
    }

    fun abortCurrentOffer() {
        val slot = getCurrentSlot()
        if (slot != -1) abortOffer(slot)
    }

    fun abortOffer(slot: Int) {
        if (isSlotFree(slot)) return
        GrandExchange.abortOffer(player, slot)
        player.packets.sendGameMessage("Abort request acknowledged. Please be aware that your offer may have already been completed.")
    }

    fun collectItems(
        slot: Int,
        invSlot: Int,
        option: Int,
    ) {
        if (slot == -1 || isSlotFree(slot)) return
        GrandExchange.collectItems(player, slot, invSlot, option)
    }

    fun chooseItem(id: Int) {
        if (!player.interfaceManager.containsInterface(105)) return
        setItem(Item(id), sell = false)
    }

    fun chooseItem() {
        if (getType() != TYPE_BUY) return
        setItemId(-1)
        setAmount(0)
        setPricePerItem(1)
        setMarketPrice(0)
        player.interfaceManager.closeInventoryInterface()
        player.packets.sendVar(VAR_ITEM_ID, -1)
        player.packets.sendVar(VAR_SLOT, 0)
        player.packets.sendVar(VAR_TYPE, 0)
        openItemSearch()
    }

    fun offer(invSlot: Int) {
        val item = player.inventory.getItem(invSlot) ?: return
        setItem(item, sell = true)
    }

    fun setItem(
        item: Item,
        sell: Boolean,
    ) {
        if (item.id == Shop.COINS || !ItemConstants.isTradeable(item)) {
            player.packets.sendGameMessage("This item cannot be sold on the Grand Exchange.")
            return
        }
        val real =
            if (item.definitions.isNoted && item.definitions.getCertId() != -1) {
                Item(item.definitions.getCertId(), item.amount)
            } else {
                item
            }
        setPricePerItem(GrandExchange.getPrice(real.id))
        setAmount(if (sell) real.amount else 0)
        setExtraDetails(real.id)
    }

    fun setExtraDetails(id: Int) {
        setItemId(id)
        setMarketPrice(GrandExchange.getPrice(id))
        val gePrice = GrandExchange.getPrice(id)
        val isLow = gePrice < Settings.LOWPRICE_LIMIT && !LimitedGEReader.itemIsLimited(id)
        val isUnlimited = UnlimitedGEReader.itemIsUnlimited(id)
        val examine = ItemExamines.getExamine(Item(id))
        val text =
            when (getType()) {
                TYPE_SELL, -1 -> buildSellInfo(id, gePrice, isLow, isUnlimited, examine)
                TYPE_BUY -> buildBuyInfo(id, gePrice, isLow, isUnlimited, examine)
                else -> ""
            }
        if (text.isNotEmpty()) player.packets.sendTextOnComponent(105, 143, text)
    }

    private fun buildSellInfo(
        id: Int,
        gePrice: Int,
        isLow: Boolean,
        isUnlimited: Boolean,
        examine: String,
    ): String =
        when {
            gePrice == 0 -> {
                "$examine<br> <br>You can't sell free items to the grand exchange."
            }

            isLow || isUnlimited -> {
                "$examine<br> <br>The Grand Exchange will automatically buy this<br>item for 5% less than its guide price."
            }

            GrandExchange.getTotalBuyQuantity(id) > 0 -> {
                val qty = GrandExchange.getTotalBuyQuantity(id)
                val best = GrandExchange.getBestBuyPrice(id)
                player.packets.sendFilteredGameMessage(
                    true,
                    "There are %s %s currently being bought on the Grand Exchange.",
                    "${Utils.getFormattedNumber(qty.toDouble(), ',')} x",
                    ItemDefinitions.getItemDefinitions(id).name,
                )
                "$examine<br><br>Best offer: ${Utils.getFormattedNumber(best.toDouble(), ',')}" +
                    "<br>Quantity: ${Utils.getFormattedNumber(qty.toDouble(), ',')}"
            }

            else -> {
                "$examine<br> <br>There are currently no buy offers for this item."
            }
        }

    private fun buildBuyInfo(
        id: Int,
        gePrice: Int,
        isLow: Boolean,
        isUnlimited: Boolean,
        examine: String,
    ): String =
        when {
            gePrice == 0 -> {
                "$examine<br><br>Quantity: Unlimited!<br>Price: Free!"
            }

            isLow || isUnlimited -> {
                "$examine<br><br>Quantity: Unlimited!<br>Price: $gePrice."
            }

            GrandExchange.getTotalSellQuantity(id) > 0 -> {
                val qty = GrandExchange.getTotalSellQuantity(id)
                val cheapest = GrandExchange.getCheapestSellPrice(id)
                player.packets.sendFilteredGameMessage(
                    true,
                    "There are %s %s currently being sold on the Grand Exchange.",
                    Utils.getFormattedNumber(qty.toDouble(), ','),
                    ItemDefinitions.getItemDefinitions(id).name,
                )
                "$examine<br><br>Lowest offer: ${Utils.getFormattedNumber(cheapest.toDouble(), ',')}" +
                    "<br>Quantity: ${Utils.getFormattedNumber(qty.toDouble(), ',')}"
            }

            else -> {
                "$examine<br><br>There are currently no sell offers for this item."
            }
        }

    fun modifyAmount(value: Int) {
        if (getType() == -1) return
        setAmount(value.coerceAtLeast(0))
    }

    fun modifyPricePerItem(value: Int) {
        if (getType() == -1) return
        requireItem { setPricePerItem(value.coerceAtLeast(1)) }
    }

    fun editAmount() {
        if (getType() == -1) return
        requireItem {
            player.temporaryAttribute().put("GEQUANTITYSET", true)
            player.packets.sendRunScript(
                108,
                "Enter the quantity you wish to ${if (getType() == TYPE_BUY) "purchase" else "sell"}:",
            )
        }
    }

    fun editPrice() {
        if (getType() == -1) return
        requireItem {
            player.temporaryAttribute().put("GEPRICESET", true)
            player.packets.sendRunScript(
                108,
                "Enter the price you wish to ${if (getType() == TYPE_BUY) "buy" else "sell"} for:",
            )
        }
    }

    fun getItemAmount(item: Item): Int {
        val notedId = item.definitions.certId
        return player.inventory.getAmountOf(item.id) + player.inventory.getAmountOf(notedId)
    }

    private inline fun requireItem(block: () -> Unit) {
        if (getItemId() == -1) {
            player.packets.sendGameMessage("You must choose an item first.")
        } else {
            block()
        }
    }

    private fun sendInventoryOfferInterface() {
        player.interfaceManager.sendInventoryInterface(INTERFACE_INV_SELL)
        player.packets.sendUnlockOptions(INTERFACE_INV_SELL, 18, 0, 27, 0)
        player.packets.sendInterSetItemsOptionsScript(INTERFACE_INV_SELL, 18, 93, 4, 7, "Offer")
    }

    private fun openItemSearch() {
        player.packets.sendVar(1241, 16750848)
        player.packets.sendVar(1242, 15439903)
        player.packets.sendVar(741, -1)
        player.packets.sendVar(743, -1)
        player.packets.sendVar(744, 0)
        player.packets.sendInterface(true, 752, 7, 389)
        player.packets.sendRunScript(570, "Grand Exchange Item Search")
    }

    companion object {
        private const val serialVersionUID = -866326987352331696L

        const val MAX_SLOTS = 6
        const val HISTORY_SIZE = 5

        const val TYPE_BUY = 0
        const val TYPE_SELL = 1

        private const val INTERFACE_GE = 105
        private const val INTERFACE_INV_SELL = 107
        private const val INTERFACE_ITEM_INFO = 449
        private const val INTERFACE_COLLECTION = 109

        private const val VAR_ITEM_ID = 1109
        private const val VAR_AMOUNT = 1110
        private const val VAR_PRICE_PER = 1111
        private const val VAR_SLOT = 1112
        private const val VAR_TYPE = 1113
        private const val VAR_MARKET_PRICE = 1114
    }
}
