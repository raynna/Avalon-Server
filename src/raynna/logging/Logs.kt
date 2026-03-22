package raynna.logging

import com.google.gson.GsonBuilder
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.npc.drops.Drop
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * Centralised, non-blocking game logger.
 *
 * All writes happen on a single daemon thread — the game loop is never blocked.
 *
 * ─── File layout ────────────────────────────────────────────────────────────
 *
 *   data/logs/<category>/<username>.jsonl
 *
 *   e.g.
 *     data/logs/trade/andreas.jsonl
 *     data/logs/npcdrops/andreas.jsonl
 *     data/logs/commands/andreas.jsonl
 *
 *   Server-wide / anonymous events (no player):
 *     data/logs/<category>/_server.jsonl
 *
 * ─── Quick-reference ────────────────────────────────────────────────────────
 *
 *  TRADE
 *    Logs.trade(player, target, myItems, theirItems)
 *
 *  GRAND EXCHANGE
 *    Logs.geOffer(player, itemId, itemName, amount, price, isBuy)
 *    Logs.geMatch(buyer, seller, itemId, itemName, amount, price)
 *    Logs.geCancel(player, itemId, itemName, remainingAmount)
 *    Logs.geCollect(player, itemId, itemName, amount)
 *
 *  COMMANDS
 *    Logs.command(player, commandName, args)
 *    Logs.yell(player, message)
 *
 *  GROUND ITEMS
 *    Logs.groundItemDrop(player, item, x, y, plane)
 *    Logs.groundItemPickup(player, item, x, y, plane)
 *
 *  NPC DROPS
 *    Logs.npcDrop(player, npcId, npcName, drops)
 *
 *  GENERIC DSL
 *    Logs.log("category", player, target) { "key" to value }
 *
 *  PLAIN MESSAGE
 *    Logs.log("startup", "Server started")
 *
 *  SHUTDOWN  — call once during server shutdown
 *    Logs.shutdown()
 * ────────────────────────────────────────────────────────────────────────────
 */
object Logs {
    private val BASE_DIR = Path.of("data", "logs")
    private const val TS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val QUEUE_CAP = 65_536

    private val queue = ArrayBlockingQueue<LogEntry>(QUEUE_CAP)

    private val writer =
        Thread({
            while (!Thread.currentThread().isInterrupted) {
                try {
                    val entry = queue.poll(500, TimeUnit.MILLISECONDS) ?: continue
                    writeEntry(entry)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
            val remaining = mutableListOf<LogEntry>()
            queue.drainTo(remaining)
            remaining.forEach(::writeEntry)
        }, "game-logger").also {
            it.isDaemon = true
            it.start()
        }

    /**
     * Log a completed (or cancelled) trade between two players.
     * Written to both players' trade files.
     */
    fun trade(
        player: Player,
        target: Player,
        playerItems: Iterable<Item?>,
        targetItems: Iterable<Item?>,
    ) {
        val playerSnap = playerItems.filterNotNull().map(::itemSnapshot)
        val targetSnap = targetItems.filterNotNull().map(::itemSnapshot)

        enqueue(
            LogEntry(
                timestamp = utcNow(),
                category = "trade",
                player = playerSnapshot(player),
                target = playerSnapshot(target),
                payload =
                    mapOf(
                        "offeredItems" to playerSnap,
                        "receivedItems" to targetSnap,
                    ),
            ),
        )
        enqueue(
            LogEntry(
                timestamp = utcNow(),
                category = "trade",
                player = playerSnapshot(target),
                target = playerSnapshot(player),
                payload =
                    mapOf(
                        "offeredItems" to targetSnap,
                        "receivedItems" to playerSnap,
                    ),
            ),
        )
    }

    /** Player placed a new buy or sell offer on the Grand Exchange. */
    fun geOffer(
        player: Player,
        itemId: Int,
        itemName: String,
        amount: Int,
        price: Int,
        isBuy: Boolean,
    ) = log(
        category = "grandexchange",
        player = player,
        payload =
            mapOf(
                "action" to if (isBuy) "offer_buy" else "offer_sell",
                "itemId" to itemId,
                "itemName" to itemName,
                "amount" to amount,
                "price" to price,
                "total" to (price.toLong() * amount),
            ),
    )

    /** A buy and sell offer matched each other on the Grand Exchange. */
    fun geMatch(
        buyer: Player,
        seller: Player,
        itemId: Int,
        itemName: String,
        amount: Int,
        price: Int,
    ) = log(
        category = "grandexchange",
        player = buyer,
        target = seller,
        payload =
            mapOf(
                "action" to "match",
                "itemId" to itemId,
                "itemName" to itemName,
                "amount" to amount,
                "price" to price,
                "total" to (price.toLong() * amount),
            ),
    )

    /** A Grand Exchange offer was cancelled. */
    fun geCancel(
        player: Player,
        itemId: Int,
        itemName: String,
        remainingAmount: Int,
    ) = log(
        category = "grandexchange",
        player = player,
        payload =
            mapOf(
                "action" to "cancel",
                "itemId" to itemId,
                "itemName" to itemName,
                "remainingAmount" to remainingAmount,
            ),
    )

    /** Player collected items from the GE collection box. */
    fun geCollect(
        player: Player,
        itemId: Int,
        itemName: String,
        amount: Int,
    ) = log(
        category = "grandexchange",
        player = player,
        payload =
            mapOf(
                "action" to "collect",
                "itemId" to itemId,
                "itemName" to itemName,
                "amount" to amount,
            ),
    )

    /**
     * Log a staff command execution.
     *
     *   Logs.command(player, cmd[0], cmd.drop(1).toTypedArray())
     */
    fun command(
        player: Player,
        commandName: String,
        args: Array<String> = emptyArray(),
    ) = log(
        category = "commands",
        player = player,
        payload =
            mapOf(
                "command" to commandName,
                "args" to args.toList(),
                "full" to ("::$commandName " + args.joinToString(" ")).trim(),
                "rank" to (runCatching { player.rank?.rank }.getOrElse { "UNKNOWN" } ?: "PLAYER"),
            ),
    )

    /** Log a yell / broadcast message. */
    fun yell(
        player: Player,
        message: String,
    ) = log(
        category = "yell",
        player = player,
        payload = mapOf("message" to message),
    )

    /**
     * Log an item landing on the ground.
     * [player] is nullable — server-spawned drops have no owner.
     */
    fun groundItemDrop(
        player: Player?,
        item: Item,
        x: Int,
        y: Int,
        plane: Int,
    ) = log(
        category = "grounditems",
        player = player,
        payload =
            mapOf(
                "action" to "drop",
                "item" to itemSnapshot(item),
                "x" to x,
                "y" to y,
                "plane" to plane,
            ),
    )

    /** Log a player picking up a ground item. */
    fun groundItemPickup(
        player: Player,
        item: Item,
        x: Int,
        y: Int,
        plane: Int,
    ) = log(
        category = "grounditems",
        player = player,
        payload =
            mapOf(
                "action" to "pickup",
                "item" to itemSnapshot(item),
                "x" to x,
                "y" to y,
                "plane" to plane,
            ),
    )

    /**
     * Log all drops rolled when a player kills an NPC.
     *
     *   Logs.npcDrop(killer, getId(), getName(), drops)
     */
    fun npcDrop(
        player: Player,
        npcId: Int,
        npcName: String,
        drops: List<Drop>,
    ) = log(
        category = "npcdrops",
        player = player,
        payload =
            mapOf(
                "npcId" to npcId,
                "npcName" to npcName,
                "drops" to
                    drops.map { drop ->
                        mapOf(
                            "itemId" to drop.itemId,
                            "itemName" to
                                runCatching {
                                    raynna.core.cache.defintions.ItemDefinitions
                                        .getItemDefinitions(drop.itemId)
                                        .name
                                }.getOrElse { "unknown" },
                            "amount" to drop.amount,
                            "always" to drop.isAlways,
                            "source" to drop.context?.dropSource?.name,
                        )
                    },
            ),
    )

    fun log(
        category: String,
        player: Player? = null,
        target: Player? = null,
        payload: Map<String, Any?> = emptyMap(),
    ) = enqueue(
        LogEntry(
            timestamp = utcNow(),
            category = category,
            player = player?.let(::playerSnapshot),
            target = target?.let(::playerSnapshot),
            payload = payload,
        ),
    )

    fun log(
        category: String,
        player: Player? = null,
        target: Player? = null,
        block: LogPayloadBuilder.() -> Unit,
    ) = log(
        category = category,
        player = player,
        target = target,
        payload = LogPayloadBuilder().apply(block).build(),
    )

    fun log(
        category: String,
        message: String,
    ) = log(category = category, payload = mapOf("message" to message))

    /** Flush remaining entries and stop the writer thread gracefully. */
    fun shutdown() {
        writer.interrupt()
        writer.join(10_000)
    }

    private fun enqueue(entry: LogEntry) {
        if (!queue.offer(entry)) {
            System.err.println("[GameLogger] Queue full — dropped entry for '${entry.category}'")
        }
    }

    /**
     * Resolve the target file:  data/logs/<category>/<username>.jsonl
     * Falls back to _server.jsonl when there is no player.
     */
    private fun writeEntry(entry: LogEntry) {
        val cat = sanitize(entry.category)
        val username =
            entry.player
                ?.get("username")
                ?.toString()
                ?.let(::sanitize) ?: "_server"

        val dir = BASE_DIR.resolve(cat)
        val file = dir.resolve("$username.json")

        try {
            Files.createDirectories(dir)

            val gson = GsonBuilder().setPrettyPrinting().create()

            val existing =
                if (file.toFile().exists()) {
                    gson.fromJson(file.toFile().readText(), MutableList::class.java)
                        as? MutableList<Any?> ?: mutableListOf()
                } else {
                    mutableListOf()
                }

            existing.add(entryToMap(entry))

            file.toFile().writeText(gson.toJson(existing))
        } catch (e: Exception) {
            System.err.println("[GameLogger] Write failed: ${e.message}")
        }
    }

    private fun entryToMap(entry: LogEntry): Map<String, Any?> {
        val root = LinkedHashMap<String, Any?>()
        root["timestamp"] = entry.timestamp
        root["category"] = entry.category
        if (entry.player != null) root["player"] = entry.player
        if (entry.target != null) root["target"] = entry.target
        root.putAll(entry.payload)
        return root
    }

    private fun utcNow(): String =
        SimpleDateFormat(TS_FORMAT)
            .apply { timeZone = TimeZone.getTimeZone("GMT") }
            .format(Date())

    /** Strip characters that are unsafe in filenames / JSON keys. */
    private fun sanitize(s: String): String = s.replace(Regex("[^A-Za-z0-9._-]"), "_").lowercase()

    internal fun playerSnapshot(p: Player): Map<String, Any?> =
        mapOf(
            "username" to p.username,
            "displayName" to p.displayName,
        )

    internal fun itemSnapshot(item: Item): Map<String, Any?> =
        mapOf(
            "itemId" to item.id,
            "itemName" to item.definitions.name,
            "amount" to item.amount,
        )

    internal data class LogEntry(
        val timestamp: String,
        val category: String,
        val player: Map<String, Any?>?,
        val target: Map<String, Any?>?,
        val payload: Map<String, Any?>,
    ) {
        /**
         * Produces a compact single-line JSON object.
         *
         * Top-level key order:
         *   timestamp → category → player → target → <payload fields>
         */
        fun toJson(): String {
            val root = LinkedHashMap<String, Any?>()
            root["timestamp"] = timestamp
            root["category"] = category
            if (player != null) root["player"] = player
            if (target != null) root["target"] = target
            root.putAll(payload)
            return serializeMap(root)
        }

        private fun serialize(v: Any?): String =
            when (v) {
                null -> {
                    "null"
                }

                is Boolean -> {
                    v.toString()
                }

                is Number -> {
                    v.toString()
                }

                is String -> {
                    "\"${
                        v.replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n")
                            .replace("\r", "\\r")
                    }\""
                }

                is Map<*, *> -> {
                    serializeMap(v)
                }

                is Iterable<*> -> {
                    "[${v.joinToString(",") { serialize(it) }}]"
                }

                else -> {
                    "\"${v.toString().replace("\\", "\\\\").replace("\"", "\\\"")}\""
                }
            }

        private fun serializeMap(m: Map<*, *>): String = "{${m.entries.joinToString(",") { (k, v) -> "\"$k\":${serialize(v)}" }}}"
    }
}

class LogPayloadBuilder {
    private val map = LinkedHashMap<String, Any?>()

    infix fun String.to(value: Any?) {
        map[this] = value
    }

    internal fun build(): Map<String, Any?> = map
}
