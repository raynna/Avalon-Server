package com.rs.kotlin.game.player.shop

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rs.kotlin.game.player.grandexchange.GrandExchange
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * Shop price registry.
 *
 * Prices are loaded from [PRICES_FILE] on startup. If an item has no entry
 * in that file the lookup falls back to the Grand Exchange price via
 * [GrandExchange.getPrice].
 *
 * ### File format
 * JSON object where keys are item IDs and values are price strings.
 * Values are parsed by [ShopPriceParser] and support k/m shorthand:
 * ```json
 * {
 *   "4151": "3m",
 *   "6585": "75m",
 *   "4067": "100k",
 *   "1234": "3.5m",
 *   "9999": "1_000_000"
 * }
 * ```
 *
 * ### Thread safety
 * All access to [prices] is guarded by `@Synchronized`.
 */
object ShopPriceManager {
    private val PRICES_FILE = Path.of("data", "shops", "shopprices.json")
    private const val BACKUP_SUFFIX = ".bak"

    private val gson = GsonBuilder().setPrettyPrinting().create()

    /** itemId → resolved integer price */
    private val prices = HashMap<Int, Int>()

    // -------------------------------------------------------------------------
    // Init / save
    // -------------------------------------------------------------------------

    fun init() {
        load()
    }

    /**
     * Saves current prices back to JSON using k/m shorthand for readability.
     * Written atomically: temp file → rename, with a .bak kept of the previous version.
     */
    @Synchronized
    fun save() {
        val asStrings = prices.mapValues { (_, price) -> formatPrice(price) }
        saveJson(PRICES_FILE, gson.toJson(asStrings))
    }

    // -------------------------------------------------------------------------
    // Price lookup
    // -------------------------------------------------------------------------

    /**
     * Returns the shop price for [itemId].
     *
     * Priority:
     * 1. Entry in [PRICES_FILE] (explicit shop override)
     * 2. Grand Exchange price via [GrandExchange.getPrice]
     */
    @Synchronized
    fun getPrice(itemId: Int): Int = prices[itemId] ?: GrandExchange.getPrice(itemId)

    /** Returns `true` if [itemId] has an explicit entry in the shop price file. */
    @Synchronized
    fun hasOverride(itemId: Int): Boolean = prices.containsKey(itemId)

    // -------------------------------------------------------------------------
    // Runtime mutation  (admin tooling / live price edits)
    // -------------------------------------------------------------------------

    /** Sets an explicit price override and persists immediately. */
    @Synchronized
    fun setPrice(
        itemId: Int,
        price: Int,
    ) {
        prices[itemId] = price
        save()
    }

    /** Removes any override for [itemId] so lookups fall back to GE price. */
    @Synchronized
    fun removeOverride(itemId: Int) {
        prices.remove(itemId)
        save()
    }

    /** Returns a read-only snapshot of all current price overrides. */
    @Synchronized
    fun snapshot(): Map<Int, Int> = HashMap(prices)

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private fun load() {
        val file = PRICES_FILE.toFile()
        if (!file.exists()) {
            println("[ShopPrices] No shopprices.json found — creating empty file.")
            saveJson(PRICES_FILE, "{\n\n}")
            return
        }
        try {
            val type = object : TypeToken<HashMap<Int, String>>() {}.type
            val raw: HashMap<Int, String> = gson.fromJson(file.readText(), type) ?: return
            val loaded = HashMap<Int, Int>()
            for ((id, value) in raw) {
                val parsed = ShopPriceParser.parsePrice(value)
                if (parsed == null) {
                    System.err.println("[ShopPrices] Invalid price for item $id: '$value' — skipping")
                    continue
                }
                loaded[id] = parsed
            }
            synchronized(this) { prices.putAll(loaded) }
            println("[ShopPrices] Loaded ${loaded.size} price override(s).")
        } catch (e: Exception) {
            System.err.println("[ShopPrices] Failed to load shopprices.json: ${e.message} — trying backup")
            tryLoadBackup()
        }
    }

    private fun tryLoadBackup() {
        val backup = PRICES_FILE.resolveSibling(PRICES_FILE.fileName.toString() + BACKUP_SUFFIX).toFile()
        if (!backup.exists()) return
        try {
            val type = object : TypeToken<HashMap<Int, String>>() {}.type
            val raw: HashMap<Int, String> = gson.fromJson(backup.readText(), type) ?: return
            val loaded = HashMap<Int, Int>()
            for ((id, value) in raw) {
                val parsed = ShopPriceParser.parsePrice(value) ?: continue
                loaded[id] = parsed
            }
            synchronized(this) { prices.putAll(loaded) }
            println("[ShopPrices] Loaded ${loaded.size} price override(s) from backup.")
        } catch (e: Exception) {
            System.err.println("[ShopPrices] Backup load failed: ${e.message}")
        }
    }

    /**
     * Atomic write: write to temp, copy current to .bak, rename temp over target.
     */
    private fun saveJson(
        target: Path,
        json: String,
    ) {
        try {
            Files.createDirectories(target.parent)
            val tmp = target.resolveSibling(target.fileName.toString() + ".tmp")
            val backup = target.resolveSibling(target.fileName.toString() + BACKUP_SUFFIX)
            tmp.toFile().writeText(json)
            if (target.toFile().exists()) {
                Files.copy(target, backup, StandardCopyOption.REPLACE_EXISTING)
            }
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (e: IOException) {
            System.err.println("[ShopPrices] Save failed for $target: ${e.message}")
        }
    }

    /**
     * Formats an integer price back to a human-readable string for the JSON file.
     * e.g. 3_000_000 → "3m", 3_500_000 → "3.50m", 100_000 → "100k"
     */
    private fun formatPrice(price: Int): String =
        when {
            price >= 1_000_000 && price % 1_000_000 == 0 -> "${price / 1_000_000}m"
            price >= 1_000_000 -> "${"%.2f".format(price / 1_000_000.0)}m"
            price >= 1_000 && price % 1_000 == 0 -> "${price / 1_000}k"
            else -> price.toString()
        }
}
