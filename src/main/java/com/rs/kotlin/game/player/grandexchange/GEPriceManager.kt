package com.rs.kotlin.game.player.grandexchange

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rs.core.cache.defintions.ItemDefinitions
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.ConcurrentHashMap

/**
 * Dynamic Grand Exchange price engine.
 *
 * ### Price resolution
 * 1. `prices.json` entry for this item → use it
 * 2. Fall back to [ItemDefinitions.value] and seed it in the map
 *
 * ### Price drift (RS-style EMA)
 * Each call to [recordTrade] nudges the price toward the traded price:
 *
 *   newPrice = oldPrice × (1 − α) + tradedPrice × α
 *
 * where α = [EMA_ALPHA] (default 0.08). Movement per trade is further
 * capped at [MAX_DRIFT_PERCENT] of the current price so no single trade
 * spikes the price.
 *
 * ### Seeding
 * Call [seedFromTextFile] once at startup to import your custom price list
 * in the format `itemId - price` (one per line).
 */
object GEPriceManager {
    // -------------------------------------------------------------------------
    // Config
    // -------------------------------------------------------------------------

    private const val EMA_ALPHA = 0.08
    private const val MAX_DRIFT_PERCENT = 0.05
    private const val MIN_PRICE = 1
    private const val MAX_PRICE = Int.MAX_VALUE

    private val PRICES_FILE = Path.of("data", "grandexchange", "prices.json")
    private val BACKUP_SUFFIX = ".bak"

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private val prices = ConcurrentHashMap<Int, Int>()

    @Volatile private var dirty = false
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun init() {
        loadPrices()
    }

    /**
     * Returns the current GE guide price for [itemId].
     * Falls back to [ItemDefinitions.value] on first access and seeds the map.
     */
    fun getPrice(itemId: Int): Int =
        prices.getOrPut(itemId) {
            itemDefinitionValue(itemId).coerceAtLeast(MIN_PRICE).also { dirty = true }
        }

    /**
     * Directly sets the guide price. Use for admin overrides or seeding.
     */
    fun setPrice(
        itemId: Int,
        price: Int,
    ) {
        prices[itemId] = price.coerceIn(MIN_PRICE, MAX_PRICE)
        dirty = true
    }

    /**
     * Clears all stored prices so they fall back to item definition values.
     * Called by [GrandExchange.reset] when price reset is requested.
     */
    fun resetAllPrices() {
        prices.clear()
        dirty = true
        save()
    }

    // -------------------------------------------------------------------------
    // Trade recording
    // -------------------------------------------------------------------------

    /**
     * Nudges the guide price toward [tradedPrice] using a volume-weighted EMA.
     * Called after every completed player-vs-player exchange.
     *
     * @param itemId      traded item
     * @param tradedPrice per-item price agreed between the two players
     * @param quantity    items exchanged (higher volume = faster drift)
     */
    fun recordTrade(
        itemId: Int,
        tradedPrice: Int,
        quantity: Int,
    ) {
        if (tradedPrice <= 0 || quantity <= 0) return

        val current = getPrice(itemId).toDouble()
        val volumeWeight = (quantity.toDouble() / 100.0).coerceAtMost(1.0)
        val effectiveAlpha = (EMA_ALPHA * volumeWeight).coerceIn(0.01, MAX_DRIFT_PERCENT)

        val ema = current * (1.0 - effectiveAlpha) + tradedPrice.toDouble() * effectiveAlpha
        val maxMove = current * MAX_DRIFT_PERCENT
        val clamped = ema.coerceIn(current - maxMove, current + maxMove)
        val newPrice = clamped.toLong().coerceIn(MIN_PRICE.toLong(), MAX_PRICE.toLong()).toInt()

        prices[itemId] = newPrice
        dirty = true
    }

    // -------------------------------------------------------------------------
    // Seeding from text file
    // -------------------------------------------------------------------------

    /**
     * Imports prices from a plain-text file:
     * ```
     * 8848 - 5000
     * 6570 - 2000000
     * ```
     * Lines not matching `<int> - <int>` are silently skipped.
     *
     * @param path      path to the text file
     * @param overwrite if true, existing entries are replaced; otherwise kept
     * @return number of prices imported
     */
    fun seedFromTextFile(
        path: Path,
        overwrite: Boolean = false,
    ): Int {
        val file = path.toFile()
        if (!file.exists()) {
            System.err.println("[GEPrice] Seed file not found: $path")
            return 0
        }
        val regex = Regex("""^\s*(\d+)\s*-\s*(\d+)\s*$""")
        var count = 0
        file.forEachLine { line ->
            val match = regex.matchEntire(line) ?: return@forEachLine
            val id = match.groupValues[1].toIntOrNull() ?: return@forEachLine
            val price = match.groupValues[2].toIntOrNull() ?: return@forEachLine
            if (overwrite || !prices.containsKey(id)) {
                prices[id] = price.coerceIn(MIN_PRICE, MAX_PRICE)
                count++
                dirty = true
            }
        }
        println("[GEPrice] Seeded $count prices from ${path.fileName}")
        if (dirty) save()
        return count
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    fun save() {
        if (!dirty) return
        saveJson(PRICES_FILE, gson.toJson(prices))
        dirty = false
    }

    private fun loadPrices() {
        val file = PRICES_FILE.toFile()
        if (!file.exists()) {
            println("[GEPrice] No prices.json — will fall back to item definition values.")
            return
        }
        try {
            val type = object : TypeToken<HashMap<Int, Int>>() {}.type
            val loaded = gson.fromJson<HashMap<Int, Int>>(file.readText(), type) ?: return
            prices.putAll(loaded)
            println("[GEPrice] Loaded ${prices.size} prices")
        } catch (e: Exception) {
            System.err.println("[GEPrice] Failed to load prices.json: ${e.message} — trying backup")
            tryLoadBackup()
        }
    }

    private fun tryLoadBackup() {
        val backup = PRICES_FILE.resolveSibling("${PRICES_FILE.fileName}$BACKUP_SUFFIX").toFile()
        if (!backup.exists()) return
        try {
            val type = object : TypeToken<HashMap<Int, Int>>() {}.type
            val loaded = gson.fromJson<HashMap<Int, Int>>(backup.readText(), type) ?: return
            prices.putAll(loaded)
            println("[GEPrice] Recovered ${prices.size} prices from backup")
        } catch (e: Exception) {
            System.err.println("[GEPrice] Backup load also failed: ${e.message}")
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
            System.err.println("[GEPrice] Save failed: ${e.message}")
        }
    }

    private fun itemDefinitionValue(itemId: Int): Int =
        try {
            ItemDefinitions.getItemDefinitions(itemId).price.coerceAtLeast(1)
        } catch (_: Exception) {
            1
        }
}
