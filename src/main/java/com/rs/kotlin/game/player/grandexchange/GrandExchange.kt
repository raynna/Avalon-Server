package com.rs.kotlin.game.player.grandexchange

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rs.Settings
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.player.Player
import com.rs.java.utils.SerializableFilesManager
import com.rs.java.utils.Utils
import com.rs.kotlin.game.logging.Logs
import java.io.IOException
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Grand Exchange engine.
 *
 * ### Persistence
 * Offers → `data/grandexchange/offers.json` (atomic write with .bak).
 * Prices → managed by [GEPriceManager].
 *
 * ### Thread safety
 * [ReentrantReadWriteLock]: reads are concurrent, writes are exclusive.
 *
 * ### Tick-delay matching
 * [findBuyerSeller] queues a pending match; [processTickMatches] executes it
 * one tick later. Cancel and instant buy/sell also go through a one-tick delay
 * so the client has time to render the in-progress state before completion.
 *
 * ### Self-trade prevention
 * [findBuyerSeller] skips offers from the same username.
 */
object GrandExchange {
    private val OFFERS_FILE = Path.of("data", "grandexchange", "offers.json")
    private val BACKUP_SUFFIX = ".bak"

    private val lock = ReentrantReadWriteLock()
    val offers = ConcurrentHashMap<Long, Offer>()
    private val offersTrack = mutableListOf<OfferHistory>()

    @Volatile private var dirty = false

    private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    fun init() {
        GEPriceManager.init()
        LimitedGEReader.init()
        UnlimitedGEReader.init()
        loadOffers()
        if (offers.isEmpty()) migrateLegacyOffers()
    }

    fun save() {
        GEPriceManager.save()
        if (!dirty) return
        lock.read { saveJson(OFFERS_FILE, gson.toJson(offers)) }
        dirty = false
    }

    fun processTickMatches() {
        lock.write {
            for (offer in offers.values) {
                when {
                    offer.pendingCancel -> {
                        offer.pendingCancel = false
                        offer.cancel()
                        dirty = true
                    }

                    offer.pendingMatch != null -> {
                        offer.processPendingMatch()
                        dirty = true
                    }

                    offer.pendingInstant -> {
                        offer.pendingInstant = false
                        if (offer.isBuying) offer.buyOffer() else offer.sellOffer()
                        dirty = true
                    }
                }
            }

            for (offer in offers.values) {
                if (
                    !offer.isCompleted &&
                    offer.pendingMatch == null &&
                    !offer.pendingInstant &&
                    !offer.pendingCancel
                ) {
                    findBuyerSeller(offer)
                }
            }
        }
    }

    fun linkOffers(player: Player) {
        lock.read {
            var itemsWaiting = false
            for (slot in player.geManager.offerUIds.indices) {
                val offer = getOfferUnsafe(player, slot) ?: continue
                offer.link(slot, player)
                offer.update()
                if (!itemsWaiting && offer.hasItemsWaiting()) {
                    itemsWaiting = true
                    if (player.session != null) {
                        player.packets.sendGameMessage("You have items from the Grand Exchange waiting on you in your collection box.")
                    }
                }
            }
        }
    }

    fun unlinkOffers(player: Player) {
        lock.read {
            for (slot in player.geManager.offerUIds.indices) {
                getOfferUnsafe(player, slot)?.unlink()
            }
        }
    }

    fun getOffer(
        player: Player,
        slot: Int,
    ): Offer? = lock.read { getOfferUnsafe(player, slot) }

    private fun getOfferUnsafe(
        player: Player,
        slot: Int,
    ): Offer? {
        val uid = player.geManager.offerUIds[slot]
        if (uid == 0L) return null
        val offer = offers[uid]
        if (offer == null) player.geManager.offerUIds[slot] = 0L
        return offer
    }

    fun sendOffer(
        player: Player,
        slot: Int,
        itemId: Int,
        amount: Int,
        price: Int,
        buy: Boolean,
    ) {
        lock.write {
            val offer = Offer(itemId, amount, price, buy)
            offer.username = player.username
            player.geManager.offerUIds[slot] = createOffer(offer)
            offer.link(slot, player)
            Logs.geOffer(player, itemId, ItemDefinitions.getItemDefinitions(itemId).name, amount, price, buy)

            when {
                getPrice(offer.id) == 0 -> {
                    if (offer.isBuying) offer.queueInstantBuy() else offer.queueInstantSell()
                }

                Settings.ECONOMY_MODE <= 1 -> {
                    when (resolveAutoMatchPath(offer)) {
                        AutoMatchPath.INSTANT_SELL -> offer.queueInstantSell()
                        AutoMatchPath.INSTANT_BUY -> offer.queueInstantBuy()
                        AutoMatchPath.ORDER_BOOK -> findBuyerSeller(offer)
                    }
                }

                else -> {
                    findBuyerSeller(offer)
                }
            }
            dirty = true
        }
    }

    private enum class AutoMatchPath { INSTANT_SELL, INSTANT_BUY, ORDER_BOOK }

    private fun resolveAutoMatchPath(offer: Offer): AutoMatchPath {
        val guidePrice = getPrice(offer.id)
        val isLimited = LimitedGEReader.itemIsLimited(offer.id)
        val isUnlimited = UnlimitedGEReader.itemIsUnlimited(offer.id)
        val isLowValue = guidePrice <= Settings.LOWPRICE_LIMIT

        if (isLimited || (!isUnlimited && !isLowValue)) return AutoMatchPath.ORDER_BOOK

        return if (!offer.isBuying) {
            if (isUnlimited || offer.price <= guidePrice) AutoMatchPath.INSTANT_SELL else AutoMatchPath.ORDER_BOOK
        } else {
            if (offer.price >= guidePrice) AutoMatchPath.INSTANT_BUY else AutoMatchPath.ORDER_BOOK
        }
    }

    fun abortOffer(
        player: Player,
        slot: Int,
    ) {
        lock.write {
            val offer = getOfferUnsafe(player, slot) ?: return
            if (offer.isCompleted || offer.pendingCancel) return
            offer.queueCancel()
            dirty = true
        }
    }

    fun collectItems(
        player: Player,
        slot: Int,
        invSlot: Int,
        option: Int,
    ) {
        lock.write {
            val offer = getOfferUnsafe(player, slot) ?: return
            dirty = true
            if (offer.collectItems(invSlot, option) && offer.forceRemove()) {
                deleteOffer(player, slot)
                if (offer.totalAmountSoFar > 0) {
                    player.geManager.addOfferHistory(
                        OfferHistory(offer.id, offer.totalAmountSoFar, offer.totalPriceSoFar, offer.isBuying),
                    )
                }
            }
        }
    }

    private fun deleteOffer(
        player: Player,
        slot: Int,
    ) {
        player.geManager.cancelOffer()
        offers.remove(player.geManager.offerUIds[slot])
        player.geManager.offerUIds[slot] = 0L
        dirty = true
    }

    fun priceCheckItem(
        player: Player,
        itemId: Int,
    ) {
        player.dialogueManager.startDialogue("PriceChecker", itemId, getCheapestSellPrice(itemId))
    }

    fun removeOffers(player: Player) {
        lock.write {
            player.geManager.offerUIds.forEach { uid ->
                if (uid != 0L) {
                    offers.remove(uid)
                    dirty = true
                }
            }
            offers.entries.removeIf { (_, o) ->
                (o.username == player.username).also { if (it) dirty = true }
            }
        }
    }

    fun removeAllOffers() {
        lock.write {
            offers.values.forEach {
                it.forceRemove()
                it.cancel()
            }
            dirty = true
        }
    }

    private fun findBuyerSeller(offer: Offer) {
        while (!offer.isCompleted) {
            val best =
                offers.values
                    .filter { o ->
                        o.isBuying != offer.isBuying &&
                            o.id == offer.id &&
                            !o.isCompleted &&
                            o.username != offer.username &&
                            o.pendingMatch == null &&
                            !o.pendingCancel &&
                            !(offer.isBuying && o.price > offer.price) &&
                            !(!offer.isBuying && o.price < offer.price) &&
                            !offer.isOfferTooHigh(o)
                    }.minByOrNull { o -> if (offer.isBuying) o.price else -o.price }
                    ?: break

            offer.queueMatch(best)
            best.queueMatch(offer)
            break
        }
        offer.update()
    }

    fun getPrice(itemId: Int): Int = GEPriceManager.getPrice(itemId)

    fun getCheapestSellPrice(itemId: Int): Int =
        lock.read {
            offers.values
                .filter { !it.isBuying && it.id == itemId && !it.isCompleted }
                .minOfOrNull { it.price } ?: 0
        }

    fun getBestBuyPrice(itemId: Int): Int =
        lock.read {
            offers.values
                .filter { it.isBuying && it.id == itemId && !it.isCompleted }
                .maxOfOrNull { it.price } ?: 0
        }

    fun getTotalSellQuantity(itemId: Int): Int =
        lock.read {
            offers.values
                .filter { !it.isBuying && it.id == itemId && !it.isCompleted }
                .sumOf { it.remainingAmount }
        }

    fun getTotalBuyQuantity(itemId: Int): Int =
        lock.read {
            offers.values
                .filter { it.isBuying && it.id == itemId && !it.isCompleted }
                .sumOf { it.remainingAmount }
        }

    fun getSellQuantity(itemId: Int): Int =
        lock.read {
            val cheapest = getCheapestSellPrice(itemId)
            offers.values
                .filter { !it.isBuying && it.id == itemId && !it.isCompleted && it.price == cheapest }
                .sumOf { it.remainingAmount }
        }

    fun getBuyQuantity(itemId: Int): Int =
        lock.read {
            val best = getBestBuyPrice(itemId)
            offers.values
                .filter { it.isBuying && it.id == itemId && !it.isCompleted && it.price == best }
                .sumOf { it.remainingAmount }
        }

    fun getTotalOfferValues(player: Player): Long =
        lock.read {
            offers.values
                .filter { it.username == player.username && !it.isCompleted }
                .sumOf { ItemDefinitions.getItemDefinitions(it.id).tipitPrice.toLong() * it.amount }
        }

    fun getTotalCollectionValue(player: Player): Long =
        lock.read {
            offers.values
                .filter { it.username == player.username && it.isCompleted }
                .sumOf { it.price.toLong() * it.totalAmountSoFar }
        }

    fun reset(
        track: Boolean,
        price: Boolean,
    ) {
        if (track) synchronized(offersTrack) { offersTrack.clear() }
        if (price) GEPriceManager.resetAllPrices()
    }

    fun recalcPrices() {
        val snapshot = synchronized(offersTrack) { ArrayList(offersTrack) }
        val sumPrice = HashMap<Int, BigInteger>()
        val sumQuantity = HashMap<Int, BigInteger>()
        for (o in snapshot) {
            sumPrice[o.id] = (sumPrice[o.id] ?: BigInteger.ZERO) + BigInteger.valueOf(o.price.toLong())
            sumQuantity[o.id] = (sumQuantity[o.id] ?: BigInteger.ZERO) + BigInteger.valueOf(o.quantity.toLong())
        }
        for ((id, sp) in sumPrice) {
            val sq = sumQuantity[id] ?: continue
            val avg = (sp / sq).toLong().coerceIn(1L, Int.MAX_VALUE.toLong()).toInt()
            val qty = (sumQuantity[id]?.toLong() ?: 1L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            GEPriceManager.recordTrade(id, avg, qty)
        }
        GEPriceManager.save()
    }

    fun sendOfferTracker(player: Player) {
        player.interfaceManager.sendInterface(275)
        repeat(100) { player.packets.sendTextOnComponent(275, it, "") }

        val visible =
            lock.read {
                offers.values
                    .filter { o ->
                        !o.isCompleted &&
                            !(getPrice(o.id) < Settings.LOWPRICE_LIMIT && !LimitedGEReader.itemIsLimited(o.id))
                    }.take(100)
            }

        player.packets.sendTextOnComponent(275, 11, "Amount of Offers: ${visible.size}<br>")
        player.packets.sendTextOnComponent(275, 1, "Grand Exchange Offers")
        visible.forEachIndexed { i, offer ->
            val defs = ItemDefinitions.getItemDefinitions(offer.id)
            val remaining = offer.remainingAmount
            val qtyStr = if (remaining > 1) " x ${Utils.getFormattedNumber(remaining.toDouble(), ',')}" else ""
            val eachStr = if (remaining > 1) " each" else ""
            player.packets.sendTextOnComponent(
                275,
                13 + i,
                "${Utils.formatPlayerNameForDisplay(offer.username)} " +
                    "[${if (offer.isBuying) "Buying" else "Selling"}] " +
                    "${defs.name}$qtyStr : Price ${Utils.getFormattedNumber(offer.price.toDouble(), ',')}$eachStr",
            )
        }
    }

    fun format(price: Int): String =
        if (price >= 10_000_000) {
            "${price / 1_000_000}m"
        } else {
            Utils.getFormattedNumber(price.toDouble(), ',').toString()
        }

    fun getHistory(): List<OfferHistory> = synchronized(offersTrack) { offersTrack.toList() }

    private fun loadOffers() {
        val file = OFFERS_FILE.toFile()
        if (!file.exists()) return
        try {
            val type = object : TypeToken<HashMap<Long, Offer>>() {}.type
            val loaded: HashMap<Long, Offer> = gson.fromJson(file.readText(), type) ?: return
            offers.putAll(loaded)
            println("[GE] Loaded ${offers.size} offers")
        } catch (e: Exception) {
            System.err.println("[GE] Failed to load offers.json: ${e.message} — trying backup")
            tryLoadBackup(OFFERS_FILE) { text ->
                val type = object : TypeToken<HashMap<Long, Offer>>() {}.type
                val loaded: HashMap<Long, Offer> = gson.fromJson(text, type) ?: return@tryLoadBackup
                offers.putAll(loaded)
                println("[GE] Recovered ${offers.size} offers from backup")
            }
        }
    }

    private fun saveJson(
        target: Path,
        json: String,
    ) {
        try {
            Files.createDirectories(target.parent)
            val tmp = target.resolveSibling("${target.fileName}.tmp")
            val backup = target.resolveSibling("${target.fileName}$BACKUP_SUFFIX")
            tmp.toFile().writeText(json)
            if (target.toFile().exists()) {
                Files.copy(target, backup, StandardCopyOption.REPLACE_EXISTING)
            }
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (e: IOException) {
            System.err.println("[GE] Save failed for $target: ${e.message}")
        }
    }

    private inline fun tryLoadBackup(
        original: Path,
        block: (String) -> Unit,
    ) {
        val backup = original.resolveSibling("${original.fileName}$BACKUP_SUFFIX").toFile()
        if (!backup.exists()) return
        try {
            block(backup.readText())
        } catch (e: Exception) {
            System.err.println("[GE] Backup load failed: ${e.message}")
        }
    }

    private fun migrateLegacyOffers() {
        try {
            @Suppress("UNCHECKED_CAST")
            val legacy = SerializableFilesManager.loadGEOffers() as? HashMap<Long, *> ?: return
            if (legacy.isEmpty()) return
            println("[GE] Migrating ${legacy.size} legacy offers to JSON…")
            dirty = true
        } catch (e: Exception) {
            System.err.println("[GE] Legacy migration skipped: ${e.message}")
        }
    }

    private fun createOffer(offer: Offer): Long {
        val uid = generateUid()
        offers[uid] = offer
        dirty = true
        return uid
    }

    private fun generateUid(): Long {
        repeat(1000) {
            val uid = Utils.RANDOM.nextLong()
            if (!offers.containsKey(uid)) return uid
        }
        throw IllegalStateException("[GE] Could not generate unique UID after 1000 attempts")
    }
}
