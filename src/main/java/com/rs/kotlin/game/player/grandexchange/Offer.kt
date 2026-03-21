package com.rs.kotlin.game.player.grandexchange

import com.google.gson.annotations.SerializedName
import com.rs.core.cache.defintions.ClientScriptMap
import com.rs.java.game.item.Item
import com.rs.java.game.item.ItemsContainer
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import java.io.Serializable
import java.time.Instant

/**
 * A single Grand Exchange offer (buy or sell).
 *
 * ### Item-safety guarantees
 * - [receivedItems] is only mutated inside methods on this class, always
 *   called from within [GrandExchange]'s write lock.
 * - [cancel] is idempotent — safe to call multiple times.
 * - [collectItems] checks all inventory space before touching any state.
 * - [totalAmountSoFar] / [totalPriceSoFar] have private setters.
 * - All coin arithmetic uses `Long` internally and is clamped to [Int.MAX_VALUE].
 *
 * ### Tick-delay mechanic
 * [GrandExchange.findBuyerSeller] calls [queueMatch] when a counterpart is
 * found. [GrandExchange.processTickMatches] calls [processPendingMatch] after
 * the countdown, executing the transfer and notifying both clients.
 */

class Offer(
    id: Int,
    amount: Int,
    val price: Int,
    val isBuying: Boolean,
) : Item(id, amount),
    Serializable {
    @Transient var owner: Player? = null
        private set

    @Transient var slot: Int = 0
        private set

    @Transient var pendingMatch: Offer? = null
        private set

    @Transient var pendingInstant: Boolean = false
        internal set

    @Transient var pendingCancel: Boolean = false
        internal set

    @field:SerializedName("username")
    var username: String = ""

    @field:SerializedName("totalAmount")
    var totalAmountSoFar: Int = 0
        private set

    @field:SerializedName("totalPrice")
    var totalPriceSoFar: Int = 0
        private set

    @field:SerializedName("canceled")
    private var canceled: Boolean = false

    @field:SerializedName("created")
    val createdAt: Long = Instant.now().epochSecond

    @field:SerializedName("received")
    private val receivedItems = ItemsContainer<Item>(2, true)

    @Suppress("unused")
    @field:SerializedName("data")
    private var data: Long = 0L

    fun link(
        slot: Int,
        owner: Player,
    ) {
        this.slot = slot
        this.owner = owner
        this.data = Utils.currentTimeMillis()
    }

    fun unlink() {
        owner = null
    }

    val isCompleted: Boolean
        get() = canceled || totalAmountSoFar >= amount

    val remainingAmount: Int
        get() = (amount - totalAmountSoFar).coerceAtLeast(0)

    fun forceRemove(): Boolean = isCompleted && !hasItemsWaiting()

    fun hasItemsWaiting(): Boolean = receivedItems.freeSlots != RECEIVED_SLOTS

    /**
     * GE progress-bar stage sent to the client:
     * - 0  = complete, nothing waiting
     * - 2  = buying, in progress
     * - 5  = buying, complete (items in box)
     * - 10 = selling, in progress
     * - 13 = selling, complete (coins in box)
     */
    fun getStage(): Int =
        when {
            forceRemove() -> STAGE_EMPTY
            isCompleted -> if (isBuying) STAGE_BUY_COMPLETE else STAGE_SELL_COMPLETE
            else -> if (isBuying) STAGE_BUY_PROGRESS else STAGE_SELL_PROGRESS
        }

    fun getPercentage(): Int =
        if (amount == 0) {
            0
        } else {
            (totalAmountSoFar * 100L / amount).toInt().coerceIn(0, 100)
        }

    fun update() {
        val o = owner ?: return
        if (o.session == null) return
        o.packets.sendGrandExchangeOffer(this)
        sendItems()
    }

    fun sendItems() {
        val o = owner ?: return
        o.packets.sendItems(ClientScriptMap.getMap(1079).getIntValue(slot.toLong()), receivedItems)
    }

    fun sendUpdateWarning(offer: Offer) {
        val o = owner ?: return
        val msg =
            if (!isCompleted) {
                val verb = if (offer.isBuying) "Bought" else "Sold"
                "<col=00a52c>Grand Exchange: $verb " +
                    "${Utils.getFormattedNumber(offer.totalAmountSoFar.toDouble(), ',')}/" +
                    "${Utils.getFormattedNumber(offer.amount.toDouble(), ',')} x ${offer.definitions.name}"
            } else {
                "<col=00a52c>One or more of your Grand Exchange offers have been updated."
            }
        o.packets.sendGameMessage(msg)
        o.packets.sendMusicEffect(SOUND_GE_UPDATE)
        update()
    }

    /**
     * Cancels the offer and returns untraded items/coins to the collection box.
     * Idempotent — returns false if already completed.
     */
    fun cancel(): Boolean {
        if (isCompleted) return false
        canceled = true
        val remaining = remainingAmount
        if (isBuying) {
            val refund = (remaining.toLong() * price.toLong()).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            receivedItems.add(Item(COINS_ID, refund))
        } else {
            receivedItems.add(Item(id, remaining))
        }
        update()
        return true
    }

    fun queueCancel() {
        pendingCancel = true
        update()
    }

    fun queueInstantSell() {
        pendingInstant = true
        update()
    }

    fun queueInstantBuy() {
        pendingInstant = true
        update()
    }

    internal fun queueMatch(counterpart: Offer) {
        pendingMatch = counterpart
        update()
    }

    fun processPendingMatch() {
        val counterpart = pendingMatch ?: return
        pendingMatch = null
        if (isCompleted || counterpart.isCompleted) return
        executeExchange(counterpart)
    }

    fun isOfferTooHigh(fromOffer: Offer): Boolean {
        val exchangeAmt = minOf(remainingAmount, fromOffer.remainingAmount)
        val totalPrice = exchangeAmt.toLong() * fromOffer.price.toLong()
        val amtCoins = receivedItems.getNumberOf(COINS_ID).toLong()
        return if (isBuying) {
            fromOffer.receivedItems.getNumberOf(COINS_ID).toLong() + totalPrice > Int.MAX_VALUE ||
                (
                    exchangeAmt.toLong() * price - totalPrice > 0 &&
                        amtCoins + (exchangeAmt.toLong() * price - totalPrice) > Int.MAX_VALUE
                )
        } else {
            amtCoins + totalPrice > Int.MAX_VALUE
        }
    }

    /**
     * Auto-sell: server buys the item at 95% of guide price.
     */
    fun sellOffer() {
        val exchangeAmt = remainingAmount
        val payout =
            (GrandExchange.getPrice(id) * 0.95 * exchangeAmt)
                .toLong()
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()
        receivedItems.add(Item(COINS_ID, payout))
        totalAmountSoFar += exchangeAmt
        totalPriceSoFar =
            (totalPriceSoFar.toLong() + payout)
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()
        GEPriceManager.recordTrade(id, GrandExchange.getPrice(id), exchangeAmt)
        sendUpdateWarning(this)
    }

    /**
     * Auto-buy: server sells the item at guide price.
     */
    fun buyOffer() {
        val exchangeAmt = remainingAmount
        val unitPrice = GrandExchange.getPrice(id)
        val totalPrice =
            (exchangeAmt.toLong() * unitPrice)
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()
        val leftCoins =
            (exchangeAmt.toLong() * price - totalPrice)
                .coerceIn(0, Int.MAX_VALUE.toLong())
                .toInt()
        if (leftCoins > 0) receivedItems.add(Item(COINS_ID, leftCoins))
        receivedItems.add(Item(id, exchangeAmt))
        totalAmountSoFar += exchangeAmt
        totalPriceSoFar =
            (totalPriceSoFar.toLong() + totalPrice)
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()
        GEPriceManager.recordTrade(id, unitPrice, exchangeAmt)
        sendUpdateWarning(this)
    }

    /**
     * Core player-vs-player exchange at [fromOffer]'s listed price.
     * Called from [processPendingMatch].
     */
    internal fun executeExchange(fromOffer: Offer) {
        val exchangeAmt = minOf(remainingAmount, fromOffer.remainingAmount)
        if (exchangeAmt <= 0) return

        val totalPrice = exchangeAmt.toLong() * fromOffer.price.toLong()

        if (isBuying) {
            val leftCoins =
                (exchangeAmt.toLong() * price - totalPrice)
                    .coerceIn(0, Int.MAX_VALUE.toLong())
                    .toInt()
            if (leftCoins > 0) receivedItems.add(Item(COINS_ID, leftCoins))
            receivedItems.add(Item(id, exchangeAmt))
            fromOffer.receivedItems.add(Item(COINS_ID, totalPrice.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()))
        } else {
            fromOffer.receivedItems.add(Item(id, exchangeAmt))
            receivedItems.add(Item(COINS_ID, totalPrice.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()))
        }

        totalAmountSoFar += exchangeAmt
        fromOffer.totalAmountSoFar += exchangeAmt
        totalPriceSoFar = (totalPriceSoFar.toLong() + totalPrice).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        fromOffer.totalPriceSoFar = (fromOffer.totalPriceSoFar.toLong() + totalPrice).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

        GEPriceManager.recordTrade(id, fromOffer.price, exchangeAmt)

        sendUpdateWarning(this)
        fromOffer.sendUpdateWarning(fromOffer)
    }

    /**
     * Instant exchange with no update warnings.
     */
    fun instaOffer(fromOffer: Offer) {
        val exchangeAmt = minOf(remainingAmount, fromOffer.remainingAmount)
        if (exchangeAmt <= 0) return
        val totalPrice = exchangeAmt.toLong() * fromOffer.price.toLong()
        if (isBuying) {
            val leftCoins = (exchangeAmt.toLong() * price - totalPrice).coerceIn(0, Int.MAX_VALUE.toLong()).toInt()
            if (leftCoins > 0) receivedItems.add(Item(COINS_ID, leftCoins))
            receivedItems.add(Item(id, exchangeAmt))
        } else {
            receivedItems.add(Item(COINS_ID, totalPrice.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()))
        }
        totalAmountSoFar += exchangeAmt
        totalPriceSoFar = (totalPriceSoFar.toLong() + totalPrice).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }

    fun collectItems(
        slot: Int,
        option: Int,
    ): Boolean {
        val o = owner ?: return false
        val freeSlots = o.inventory.freeSlots
        val item = receivedItems[slot] ?: return false
        val defs = item.definitions

        val hasSpace =
            if (defs.isStackable) {
                o.inventory.hasFreeSlots() || o.inventory.containsOneItem(item.id)
            } else {
                o.inventory.hasFreeSlots()
            }

        if (!hasSpace) {
            o.packets.sendGameMessage("Not enough space in your inventory.")
            return false
        }

        when {
            defs.id == COINS_ID -> {
                o.moneyPouch.addMoney(item.amount, false)
                receivedItems.remove(item)
            }

            defs.isStackable && o.inventory.getNumberOf(item.id).toLong() + item.amount > Int.MAX_VALUE -> {
                val canAdd = Int.MAX_VALUE - o.inventory.getNumberOf(item.id)
                if (canAdd > 0) {
                    receivedItems.remove(Item(item.id, canAdd))
                    o.inventory.addItem(Item(item.id, canAdd))
                }
                o.packets.sendGameMessage("Not enough space in your inventory.")
            }

            !defs.isStackable && option == (if (item.amount == 1) 0 else 1) -> {
                val toCollect = minOf(item.amount, freeSlots)
                o.inventory.addItem(Item(item.id, toCollect))
                receivedItems.remove(Item(item.id, toCollect))
            }

            else -> {
                val targetId = if (defs.certId != -1) defs.certId else item.id
                o.inventory.addItem(Item(targetId, item.amount))
                receivedItems.remove(item)
            }
        }

        update()
        return true
    }

    companion object {
        private const val serialVersionUID = -4065899425889989474L

        const val COINS_ID = 995

        private const val RECEIVED_SLOTS = 2
        private const val STAGE_EMPTY = 0
        private const val STAGE_BUY_PROGRESS = 2
        private const val STAGE_BUY_COMPLETE = 5
        private const val STAGE_SELL_PROGRESS = 10
        private const val STAGE_SELL_COMPLETE = 13
        private const val SOUND_GE_UPDATE = 284
    }
}
