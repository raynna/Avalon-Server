package com.rs.kotlin.game.player

import com.rs.core.packets.decode.WorldPacketsDecoder
import com.rs.java.game.item.Item
import com.rs.java.game.item.ItemsContainer
import com.rs.java.game.player.Player
import com.rs.java.game.player.content.ItemConstants
import com.rs.java.utils.EconomyPrices
import com.rs.java.utils.ItemExamines
import com.rs.kotlin.game.logging.Logs
import java.text.SimpleDateFormat
import java.util.*

class Trade(
    private val player: Player,
) {
    /**
     * Tracks the lifecycle of a trade to prevent duplicate item grants.
     *
     * Transitions (only ever forward):
     *   IDLE → OPEN → CONFIRMING → COMPLETING → CLOSED
     *
     * COMPLETING is the critical guard: once set by the first thread that wins
     * the lock, the second thread skips the item-transfer entirely.
     */
    private enum class TradeState { IDLE, OPEN, CONFIRMING, COMPLETING, CLOSED }

    @Volatile private var state = TradeState.IDLE

    @Volatile var target: Player? = null
        private set

    private val items = ItemsContainer<Item>(28, false)

    private var tradeModified = false
    private var accepted = false

    val isTrading: Boolean get() = target != null && state != TradeState.CLOSED

    /**
     * Handles all button presses for interface 335 (trade screen) and
     * interface 336 (inventory panel during trade).
     *
     * Replace the decoder-side if/else chain with:
     *   player.newTrade.handleButton(interfaceId, componentId, packetId, slotId)
     *
     * Returns true if the button was handled, false if unrecognised.
     */
    fun handleButton(
        interfaceId: Int,
        componentId: Int,
        packetId: Int,
        slotId: Int,
    ): Boolean {
        when (interfaceId) {
            334 -> {
                when (componentId) {
                    21 -> accept(firstStage = false)
                    22 -> closeTrade(CloseTradeStage.CANCEL)
                }
            }

            335 -> {
                when (componentId) {
                    18 -> {
                        accept(firstStage = true)
                    }

                    20 -> {
                        closeTrade(CloseTradeStage.CANCEL)
                    }

                    53 -> {
                        player.temporaryAttribute()["trade_moneypouch_X_Slot"] = slotId
                        player.packets.sendRunScript(108, "Enter Amount:")
                    }

                    32 -> {
                        when (packetId) {
                            ACTION_BUTTON1 -> {
                                removeItem(slotId, 1)
                            }

                            ACTION_BUTTON2 -> {
                                removeItem(slotId, 5)
                            }

                            ACTION_BUTTON3 -> {
                                removeItem(slotId, 10)
                            }

                            ACTION_BUTTON4 -> {
                                removeItem(slotId, Int.MAX_VALUE)
                            }

                            ACTION_BUTTON5 -> {
                                player.temporaryAttribute()["trade_removeitem_X_Slot"] = slotId
                                player.packets.sendRunScript(108, "Enter Amount:")
                            }

                            ACTION_BUTTON9 -> {
                                sendValue(slotId, fromTrader = false)
                            }

                            ACTION_BUTTON8 -> {
                                sendExamine(slotId, fromTrader = false)
                            }

                            else -> {
                                return false
                            }
                        }
                    }

                    35 -> {
                        when (packetId) {
                            ACTION_BUTTON1 -> sendValue(slotId, fromTrader = true)
                            ACTION_BUTTON8 -> sendExamine(slotId, fromTrader = true)
                            else -> return false
                        }
                    }

                    else -> {
                        return false
                    }
                }
            }

            336 -> {
                when (componentId) {
                    0 -> {
                        when (packetId) {
                            ACTION_BUTTON1 -> {
                                addItem(slotId, 1)
                            }

                            ACTION_BUTTON2 -> {
                                addItem(slotId, 5)
                            }

                            ACTION_BUTTON3 -> {
                                addItem(slotId, 10)
                            }

                            ACTION_BUTTON4 -> {
                                addItem(slotId, Int.MAX_VALUE)
                            }

                            ACTION_BUTTON5 -> {
                                player.temporaryAttribute().put("trade_item_X_Slot", slotId)
                                player.packets.sendRunScript(108, arrayOf<Any>("Enter Amount:"))
                            }

                            ACTION_BUTTON9 -> {
                                sendValue(slotId)
                            }

                            ACTION_BUTTON8 -> {
                                player.inventory.sendExamine(slotId)
                            }

                            else -> {
                                return false
                            }
                        }
                    }

                    else -> {
                        return false
                    }
                }
            }

            else -> {
                return false
            }
        }
        return true
    }

    fun openTrade(target: Player) {
        withBothLocks(this, target.trade) {
            this.target = target
            state = TradeState.OPEN

            player.packets.sendTextOnComponent(335, 17, "Trading With: ${target.displayName}")
            player.packets.sendGlobalString(203, target.displayName)
            sendInterItems()
            sendOptions()
            sendTradeModified()
            refreshFreeInventorySlots()
            refreshTradeWealth()
            refreshStageMessage(firstStage = true)
            player.interfaceManager.sendInterface(335)
            player.interfaceManager.sendInventoryInterface(336)
            player.setCloseInterfacesEvent { closeTrade(CloseTradeStage.CANCEL) }
        }
    }

    fun addItem(
        slot: Int,
        amount: Int,
    ) {
        withBothLocks(this, safeOtherTrade() ?: return) {
            if (!isTrading) return@withBothLocks
            val item = player.inventory.getItem(slot) ?: return@withBothLocks

            if (!ItemConstants.isTradeable(item)) {
                player.packets.sendGameMessage("That item isn't tradeable.")
                return@withBothLocks
            }

            val itemsBefore = items.itemsCopy
            val maxAmount = player.inventory.items.getNumberOf(item)
            var itemAmount = amount.coerceAtMost(maxAmount)

            for (tradeItem in items.containerItems) {
                tradeItem ?: continue
                if (tradeItem.amount == Int.MAX_VALUE) {
                    player.packets.sendGameMessage("You can't trade more of that item.")
                    return@withBothLocks
                }
                if (tradeItem.amount + itemAmount < 0) {
                    itemAmount = Int.MAX_VALUE - tradeItem.amount
                }
            }

            if (itemAmount <= 0) return@withBothLocks

            items.add(Item(item.id, itemAmount))
            player.inventory.deleteItem(slot, Item(item.id, itemAmount))
            refreshItems(itemsBefore)
            cancelAccepted()
            setTradeModified(true)
        }
    }

    fun removeItem(
        slot: Int,
        amount: Int,
    ) {
        withBothLocks(this, safeOtherTrade() ?: return) {
            if (!isTrading) return@withBothLocks
            val item = items[slot] ?: return@withBothLocks
            val itemsBefore = items.itemsCopy
            val maxAmount = items.getNumberOf(item)
            val toRemove = Item(item.id, amount.coerceAtMost(maxAmount))
            items.remove(slot, toRemove)

            if (toRemove.id != COINS_ID) {
                player.inventory.addItem(toRemove)
            } else {
                player.moneyPouch.addMoney(toRemove.amount, false)
            }

            refreshItems(itemsBefore)
            cancelAccepted()
            setTradeModified(true)
        }
    }

    fun addPouch(amount: Int) {
        withBothLocks(this, safeOtherTrade() ?: return) {
            if (!isTrading) return@withBothLocks
            val pouchTotal = player.moneyPouch.total
            if (pouchTotal == 0) {
                player.packets.sendGameMessage("You don't have enough money to do that.")
                return@withBothLocks
            }

            val itemsBefore = items.itemsCopy
            var itemAmount = amount

            for (tradeItem in items.containerItems) {
                tradeItem ?: continue
                if (tradeItem.amount == Int.MAX_VALUE) {
                    player.packets.sendGameMessage("You can't trade more of that item.")
                    return@withBothLocks
                }
                if (tradeItem.amount + amount < 0) {
                    itemAmount = Int.MAX_VALUE - tradeItem.amount
                    player.packets.sendGameMessage("You can't trade more of that item.")
                }
            }

            itemAmount = itemAmount.coerceAtMost(pouchTotal)
            if (itemAmount <= 0) return@withBothLocks

            items.add(Item(COINS_ID, itemAmount))
            player.moneyPouch.removeMoneyMisc(itemAmount)
            refreshItems(itemsBefore)
            cancelAccepted()
            setTradeModified(true)
        }
    }

    fun accept(firstStage: Boolean) {
        withBothLocks(this, safeOtherTrade() ?: return) {
            if (!isTrading) return@withBothLocks
            if (firstStage) {
                accepted = true
                val other = safeOtherTrade() ?: return@withBothLocks
                if (other.accepted) {
                    nextStage()
                    other.nextStage()
                } else {
                    refreshBothStageMessage(firstStage = true)
                }
            } else {
                accepted = true
                val other = safeOtherTrade() ?: return@withBothLocks
                if (other.accepted) {
                    player.setCloseInterfacesEvent(null)
                    player.closeInterfaces()
                    closeTrade(CloseTradeStage.DONE)
                } else {
                    refreshBothStageMessage(firstStage = false)
                }
            }
        }
    }

    fun sendValue(
        slot: Int,
        fromTrader: Boolean,
    ) {
        if (!isTrading) return
        val item = (if (fromTrader) target?.trade?.items?.get(slot) else items[slot]) ?: return
        if (!ItemConstants.isTradeable(item)) {
            player.packets.sendGameMessage("That item isn't tradeable.")
            return
        }
        val price = EconomyPrices.getPrice(item.id)
        player.packets.sendGameMessage("${item.definitions.name}: market price is $price coins.")
    }

    fun sendValue(slot: Int) {
        val item = player.inventory.getItem(slot) ?: return
        if (!ItemConstants.isTradeable(item)) {
            player.packets.sendGameMessage("That item isn't tradeable.")
            return
        }
        val price = EconomyPrices.getPrice(item.id)
        player.packets.sendGameMessage("${item.definitions.name}: market price is $price coins.")
    }

    fun sendExamine(
        slot: Int,
        fromTrader: Boolean,
    ) {
        if (!isTrading) return
        val item = (if (fromTrader) target?.trade?.items?.get(slot) else items[slot]) ?: return
        player.packets.sendGameMessage(ItemExamines.getExamine(item))
    }

    /**
     * Close the trade on both sides.
     *
     * Dupe prevention:
     *   1. The COMPLETING guard is checked first — if already set, exit immediately.
     *   2. The log is written AFTER the guard is set, so it only fires once per trade.
     *   3. Item snapshots are taken before clearing containers so the log is accurate.
     */
    fun closeTrade(stage: CloseTradeStage) {
        val oldTarget = target ?: return
        val otherTrade = oldTarget.trade

        withBothLocks(this, otherTrade) {
            if (state == TradeState.COMPLETING || state == TradeState.CLOSED) return@withBothLocks

            state = TradeState.COMPLETING
            otherTrade.state = TradeState.COMPLETING
            val myItemSnapshot = this.items.containerItems.filterNotNull()
            val theirItemSnapshot = otherTrade.items.containerItems.filterNotNull()

            Logs.trade(
                player = player,
                target = oldTarget,
                playerItems = myItemSnapshot,
                targetItems = theirItemSnapshot,
            )

            this.items.clear()
            otherTrade.items.clear()

            this.target = null
            this.tradeModified = false
            this.accepted = false

            if (stage == CloseTradeStage.DONE) {
                grantItems(player, theirItemSnapshot)
                grantItems(oldTarget, myItemSnapshot)
                player.packets.sendGameMessage("Accepted trade.")
                oldTarget.packets.sendGameMessage("Accepted trade.")
            } else {
                grantItems(player, myItemSnapshot)
                grantItems(oldTarget, theirItemSnapshot)
            }

            player.inventory.init()
            oldTarget.inventory.init()

            if (otherTrade.isTrading) {
                otherTrade.target = null
                otherTrade.tradeModified = false
                otherTrade.accepted = false
                oldTarget.setCloseInterfacesEvent(null)
                oldTarget.closeInterfaces()

                when (stage) {
                    CloseTradeStage.CANCEL -> {
                        oldTarget.packets.sendGameMessage("<col=ff0000>Other player declined trade!")
                    }

                    CloseTradeStage.NO_SPACE -> {
                        player.packets.sendGameMessage("You don't have enough space in your inventory for this trade.")
                        oldTarget.packets.sendGameMessage("Other player doesn't have enough space in their inventory for this trade.")
                    }

                    CloseTradeStage.DONE -> { /* messages already sent above */ }
                }
            }

            state = TradeState.CLOSED
            otherTrade.state = TradeState.CLOSED
        }
    }

    fun sendFlash(slot: Int) {
        player.packets.sendInterFlashScript(335, 33, 4, 7, slot)
        target?.packets?.sendInterFlashScript(335, 36, 4, 7, slot)
    }

    fun cancelAccepted() {
        var canceled = false
        if (accepted) {
            accepted = false
            canceled = true
        }
        safeOtherTrade()?.let {
            if (it.accepted) {
                it.accepted = false
                canceled = true
            }
        }
        if (canceled) refreshBothStageMessage(canceled)
    }

    fun refreshItems(itemsBefore: Array<Item?>) {
        val changedSlots = mutableListOf<Int>()
        for (index in itemsBefore.indices) {
            val current = items.containerItems[index]
            if (itemsBefore[index] !== current) {
                val prev = itemsBefore[index]
                if (prev != null && (current == null || current.id != prev.id || current.amount < prev.amount)) {
                    sendFlash(index)
                }
                changedSlots += index
            }
        }
        refresh(*changedSlots.toIntArray())
        refreshFreeInventorySlots()
        refreshTradeWealth()
    }

    fun sendOptions() {
        player.packets.sendInterSetItemsOptionsScript(
            336,
            0,
            93,
            4,
            7,
            "Offer",
            "Offer-5",
            "Offer-10",
            "Offer-All",
            "Offer-X",
            "Value<col=FF9040>",
            "Lend",
        )
        player.packets.sendComponentSettings(336, 0, 0, 27, 1278)
        player.packets.sendInterSetItemsOptionsScript(
            335,
            32,
            90,
            4,
            7,
            "Remove",
            "Remove-5",
            "Remove-10",
            "Remove-All",
            "Remove-X",
            "Value",
        )
        player.packets.sendComponentSettings(335, 32, 0, 27, 1150)
        player.packets.sendInterSetItemsOptionsScript(335, 35, 90, true, 4, 7, "Value")
        player.packets.sendComponentSettings(335, 35, 0, 27, 1026)
    }

    fun setTradeModified(modified: Boolean) {
        if (modified == tradeModified) return
        tradeModified = modified
        sendTradeModified()
    }

    fun sendInterItems() {
        player.packets.sendItems(90, items)
        target?.packets?.sendItems(90, true, items)
    }

    fun refresh(vararg slots: Int) {
        player.packets.sendUpdateItems(90, items, *slots)
        target?.packets?.sendUpdateItems(90, true, items.containerItems, *slots)
    }

    fun nextStage() {
        if (!isTrading) return
        val other = target ?: return

        val iFit = canFitIncoming(player, other.trade.items)
        val theyFit = canFitIncoming(other, this.items)

        if (!iFit || !theyFit) {
            if (!iFit) {
                notifyNoSpace(player, "You don't have enough space in your inventory to continue the trade.")
                notifyNoSpace(other, "${player.displayName} doesn't have enough space in their inventory to continue the trade.")
                outOfSpaceMessage(player, "${other.displayName} doesn't have enough space in their inventory to continue the trade.")
            }
            if (!theyFit) {
                notifyNoSpace(player, "${other.displayName} doesn't have enough space in their inventory to continue the trade.")
                notifyNoSpace(other, "You don't have enough space in your inventory to continue the trade.")
                outOfSpaceMessage(other, "${other.displayName} doesn't have enough space in their inventory to continue the trade.")
            }
            return
        }

        state = TradeState.CONFIRMING
        accepted = false
        player.interfaceManager.sendInterface(334)
        player.interfaceManager.closeInventoryInterface()
        player.packets.sendHideIComponent(334, 55, !(tradeModified || other.trade.tradeModified))
        refreshBothStageMessage(firstStage = false)
    }

    fun refreshBothStageMessage(firstStage: Boolean) {
        refreshStageMessage(firstStage)
        target?.trade?.refreshStageMessage(firstStage)
    }

    fun outOfSpaceMessage(
        p: Player,
        message: String,
    ) {
        p.packets.sendTextOnComponent(335, 39, message)
    }

    fun refreshStageMessage(firstStage: Boolean) {
        player.packets.sendTextOnComponent(
            if (firstStage) 335 else 334,
            if (firstStage) 39 else 34,
            getAcceptMessage(firstStage),
        )
    }

    fun getAcceptMessage(firstStage: Boolean): String =
        when {
            accepted -> "Waiting for other player..."
            safeOtherTrade()?.accepted == true -> "Other player has accepted."
            else -> if (firstStage) "" else "Are you sure you want to make this trade?"
        }

    fun sendTradeModified() {
        player.packets.sendVar(1042, if (tradeModified) 1 else 0)
        target?.packets?.sendVar(1043, if (tradeModified) 1 else 0)
    }

    fun refreshTradeWealth() {
        val wealthLong = getTradeWealth()
        val wealth = if (wealthLong > Int.MAX_VALUE) Int.MAX_VALUE else wealthLong.toInt()
        player.packets.sendGlobalVar(729, wealth)
        target?.packets?.sendGlobalVar(697, wealth)
    }

    fun refreshFreeInventorySlots() {
        val freeSlots = player.inventory.freeSlots
        target?.packets?.sendTextOnComponent(
            335,
            23,
            "has ${if (freeSlots == 0) "no" else freeSlots} free<br>inventory slots",
        )
    }

    fun getTradeWealth(): Long =
        items.containerItems.filterNotNull().sumOf { item ->
            EconomyPrices.getPrice(item.id).toLong() * item.amount.toLong()
        }

    private fun safeOtherTrade(): Trade? = target?.trade

    private fun notifyNoSpace(
        p: Player?,
        msg: String,
    ) {
        p?.packets?.sendGameMessage(msg)
    }

    private fun grantItems(
        recipient: Player,
        snapshot: List<Item>,
    ) {
        for (item in snapshot) {
            if (item.id == COINS_ID) {
                recipient.moneyPouch.addMoney(item.amount, false)
            } else {
                recipient.inventory.addItem(item)
            }
        }
    }

    companion object {
        private const val COINS_ID = 995

        private const val ACTION_BUTTON1 = WorldPacketsDecoder.ACTION_BUTTON1_PACKET
        private const val ACTION_BUTTON2 = WorldPacketsDecoder.ACTION_BUTTON2_PACKET
        private const val ACTION_BUTTON3 = WorldPacketsDecoder.ACTION_BUTTON3_PACKET
        private const val ACTION_BUTTON4 = WorldPacketsDecoder.ACTION_BUTTON4_PACKET
        private const val ACTION_BUTTON5 = WorldPacketsDecoder.ACTION_BUTTON5_PACKET
        private const val ACTION_BUTTON8 = WorldPacketsDecoder.ACTION_BUTTON8_PACKET
        private const val ACTION_BUTTON9 = WorldPacketsDecoder.ACTION_BUTTON9_PACKET

        private fun orderedLocks(
            a: Trade,
            b: Trade,
        ): Pair<Trade, Trade> {
            if (a === b) return a to b
            val an = a.player.username ?: ""
            val bn = b.player.username ?: ""
            val cmp = an.compareTo(bn, ignoreCase = true)
            return when {
                cmp < 0 -> a to b
                cmp > 0 -> b to a
                else -> if (System.identityHashCode(a) <= System.identityHashCode(b)) a to b else b to a
            }
        }

        private inline fun withBothLocks(
            a: Trade,
            b: Trade,
            block: () -> Unit,
        ) {
            val (first, second) = orderedLocks(a, b)
            synchronized(first) { synchronized(second) { block() } }
        }

        private fun canFitIncoming(
            receiver: Player,
            incoming: ItemsContainer<Item>,
        ): Boolean {
            val invUsed = receiver.inventory.items.usedSlots
            val incomingSlots = incoming.usedSlots
            return invUsed + incomingSlots <= 28
        }

        fun currentTime(dateFormat: String): String {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            return SimpleDateFormat(dateFormat).format(cal.time)
        }

        private fun sanitizeUsername(username: String?): String = username?.replace(Regex("[^A-Za-z0-9._-]"), "_") ?: "unknown"
    }

    enum class CloseTradeStage { CANCEL, NO_SPACE, DONE }
}
