package com.rs.kotlin.api

import com.rs.java.game.World
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.AccountCreation
import com.rs.kotlin.game.player.grandexchange.GrandExchange
import com.rs.kotlin.game.player.tasksystem.Task
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.BufferedReader
import java.io.FileReader
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executors

/**
 * Lightweight HTTP server that exposes game logs as a JSON API.
 *
 * Reads directly from the .jsonl files written by [Logs].
 * No external dependencies — uses the JDK's built-in com.sun.net.httpserver.
 *
 * ─── File layout (matches Logs.kt) ──────────────────────────────────────────
 *
 *   data/logs/<category>/<username>.jsonl
 *
 *   e.g.
 *     data/logs/trade/andreas.jsonl
 *     data/logs/npcdrops/andreas.jsonl
 *     data/logs/commands/andreas.jsonl
 *     data/logs/grandexchange/_server.jsonl   ← server-wide / no owner
 *
 * ─── Start / stop ───────────────────────────────────────────────────────────
 *
 *   LogApiServer.start(port = 8765, apiKey = "changeme")
 *   LogApiServer.stop()
 *
 *   // Recommended in server bootstrap:
 *   Runtime.getRuntime().addShutdownHook(Thread { LogApiServer.stop() })
 *
 * ─── Authentication ──────────────────────────────────────────────────────────
 *
 *   Every request must include the header:
 *     X-Api-Key: <your-key>
 *
 * ─── Endpoints ──────────────────────────────────────────────────────────────
 *
 *   GET /api/logs/categories
 *       List all available log categories (folder names under data/logs/).
 *
 *   GET /api/logs/players/{category}
 *       List all player files (usernames) that have entries in a category.
 *
 *   GET /api/logs/{category}/{username}
 *       Return all log entries for this player in this category.
 *       Query params:
 *         ?date=YYYY-MM-DD   — filter to entries on a specific date
 *         ?limit=N           — return last N entries (default 500, max 5000)
 *         ?search=text       — filter entries containing this text anywhere
 *
 *   GET /api/logs/player/{username}
 *       Return entries across ALL categories for this player.
 *       Query params: ?date=YYYY-MM-DD, ?limit=N, ?search=text
 *
 *   GET /api/logs/drops/{username}
 *       Shortcut: NPC drops for a specific player.
 *       Query params: ?date=YYYY-MM-DD, ?limit=N
 *
 *   GET /api/logs/trades/{username}
 *       Shortcut: Trades involving a specific player.
 *       Query params: ?date=YYYY-MM-DD, ?limit=N
 *
 * ─── Example response ────────────────────────────────────────────────────────
 *
 *   GET /api/logs/npcdrops/andreas?limit=3
 *   {
 *     "category": "npcdrops",
 *     "player":   "andreas",
 *     "count":    3,
 *     "entries": [
 *       {"timestamp":"2026-03-21T14:01:00Z","category":"npcdrops",
 *        "player":{"username":"andreas","displayName":"Andreas"},
 *        "npcId":1615,"npcName":"Abyssal demon",
 *        "drops":[{"itemId":4151,"itemName":"Abyssal whip","amount":1,...}]},
 *       ...
 *     ]
 *   }
 * ────────────────────────────────────────────────────────────────────────────
 */
object LogApiServer {
    private val BASE_DIR = Path.of("data", "logs")

    @Volatile private var server: HttpServer? = null

    @Volatile private var apiKey: String = ""

    fun start(
        port: Int = 8765,
        apiKey: String = "changeme",
    ) {
        if (server != null) return
        this.apiKey = apiKey

        val httpServer = HttpServer.create(InetSocketAddress(port), 0)
        httpServer.executor = Executors.newFixedThreadPool(4)

        httpServer.createContext("/api/logs/categories") { exchange ->
            handle(exchange) { categoriesEndpoint() }
        }

        httpServer.createContext("/api/logs/players/") { exchange ->
            handle(exchange) {
                val category =
                    exchange.requestURI.path
                        .removePrefix("/api/logs/players/")
                        .trim('/')
                playersInCategoryEndpoint(category)
            }
        }

        httpServer.createContext("/api/logs/player/") { exchange ->
            handle(exchange) {
                val username =
                    exchange.requestURI.path
                        .removePrefix("/api/logs/player/")
                        .trim('/')
                val params = parseQuery(exchange.requestURI.query)
                allCategoriesForPlayerEndpoint(username, params)
            }
        }

        httpServer.createContext("/api/logs/drops/") { exchange ->
            handle(exchange) {
                val username =
                    exchange.requestURI.path
                        .removePrefix("/api/logs/drops/")
                        .trim('/')
                val params = parseQuery(exchange.requestURI.query)
                playerFileEndpoint("npcdrops", username, params)
            }
        }

        httpServer.createContext("/api/logs/trades/") { exchange ->
            handle(exchange) {
                val username =
                    exchange.requestURI.path
                        .removePrefix("/api/logs/trades/")
                        .trim('/')
                val params = parseQuery(exchange.requestURI.query)
                playerFileEndpoint("trade", username, params)
            }
        }

        httpServer.createContext("/api/logs/") { exchange ->
            handle(exchange) {
                val parts =
                    exchange.requestURI.path
                        .removePrefix("/api/logs/")
                        .trim('/')
                        .split("/")
                val params = parseQuery(exchange.requestURI.query)
                when {
                    parts.size >= 2 -> {
                        playerFileEndpoint(parts[0], parts[1], params)
                    }

                    parts.size == 1 && parts[0].isNotEmpty() -> {
                        respond404("Use /api/logs/{category}/{username}")
                    }

                    else -> {
                        respond404("Use /api/logs/{category}/{username}")
                    }
                }
            }
        }

        httpServer.createContext("/api/player/skills/") { exchange ->
            handle(exchange) {
                val username =
                    exchange.requestURI.path
                        .removePrefix("/api/player/skills/")
                        .trim('/')

                playerSkillsEndpoint(username)
            }
        }

        httpServer.createContext("/api/player/achievements/") { exchange ->
            handle(exchange) {
                val username =
                    exchange.requestURI.path
                        .removePrefix("/api/player/achievements/")
                        .trim('/')

                playerAchievementsEndpoint(username)
            }
        }

        httpServer.createContext("/api/player/overview/") { exchange ->
            handle(exchange) {
                val username =
                    exchange.requestURI.path
                        .removePrefix("/api/player/overview/")
                        .trim('/')

                playerOverviewEndpoint(username)
            }
        }
        httpServer.createContext("/api/ge/offers") { exchange ->
            handle(exchange) {
                grandExchangeOffersEndpoint()
            }
        }

        httpServer.createContext("/api/ge/overview") { exchange ->
            handle(exchange) {
                grandExchangeOverviewEndpoint()
            }
        }

        httpServer.start()
        server = httpServer
        println("[LogApiServer] Started on port $port")
    }

    private fun grandExchangeOffersEndpoint(): String {
        val offers =
            GrandExchange.offers.values.map { offer ->
                buildJson {
                    "itemId" to offer.id
                    "itemName" to offer.definitions.name
                    "username" to offer.username

                    "amount" to offer.amount
                    "remaining" to offer.remainingAmount
                    "completed" to offer.totalAmountSoFar

                    "price" to offer.price
                    "type" to if (offer.isBuying) "buy" else "sell"

                    "state" to
                        when {
                            offer.isCompleted -> "completed"
                            offer.pendingCancel -> "canceling"
                            offer.pendingInstant -> "instant"
                            offer.pendingMatch != null -> "matching"
                            else -> "active"
                        }

                    "createdAt" to offer.createdAt
                }
            }

        return buildJson {
            "count" to offers.size
            "offers" to RawJsonArray(offers)
        }
    }

    private fun grandExchangeOverviewEndpoint(): String {
        val grouped =
            GrandExchange.offers.values
                .groupBy { it.id }
                .map { (itemId, offers) ->
                    val name = offers.first().definitions.name

                    val buyOffers = offers.filter { it.isBuying && !it.isCompleted }
                    val sellOffers = offers.filter { !it.isBuying && !it.isCompleted }

                    buildJson {
                        "itemId" to itemId
                        "itemName" to name

                        "buyOffers" to buyOffers.size
                        "sellOffers" to sellOffers.size

                        "bestBuyPrice" to (buyOffers.maxOfOrNull { it.price } ?: 0)
                        "bestSellPrice" to (sellOffers.minOfOrNull { it.price } ?: 0)

                        "buyQuantity" to buyOffers.sumOf { it.remainingAmount }
                        "sellQuantity" to sellOffers.sumOf { it.remainingAmount }
                    }
                }

        return buildJson {
            "items" to RawJsonArray(grouped)
        }
    }

    private fun getPlayerOnlineOrOffline(username: String): com.rs.java.game.player.Player? {
        val online =
            com.rs.java.game.World
                .getPlayers()
                .firstOrNull { it != null && it.username.equals(username, ignoreCase = true) }

        if (online != null) return online

        val displayName = Utils.formatPlayerNameForDisplay(username)
        return AccountCreation.loadPlayer(displayName)
    }

    private fun playerSkillsEndpoint(username: String): String {
        val player =
            getPlayerOnlineOrOffline(username)
                ?: return """{"error":"Player not online"}"""

        val skills = player.skills

        val skillList =
            (0..24).map { id ->
                val name = skills.getSkillName(id)
                val level = skills.getRealLevel(id)
                val xp = skills.getXp(id)

                buildJson {
                    "id" to id
                    "name" to name
                    "level" to level
                    "xp" to xp.toLong()
                }
            }

        return buildJson {
            "player" to username
            "totalLevel" to skills.getTotalLevel(player)
            "combatLevel" to skills.getCombatLevelWithSummoning()
            "skills" to RawJsonArray(skillList)
        }
    }

    private fun playerAchievementsEndpoint(username: String): String {
        val player =
            getPlayerOnlineOrOffline(username)
                ?: return """{"error":"Player not online"}"""

        val taskManager = player.taskManager

        val tasks =
            Task.entries.map { task ->
                val completed = taskManager.isCompleted(task)
                val stage = taskManager.stage(task)

                buildJson {
                    "name" to task.name
                    "difficulty" to task.difficulty.name
                    "completed" to completed
                    "progress" to stage
                    "required" to task.amount
                }
            }

        return buildJson {
            "player" to username
            "completedTasks" to tasks.count { it.contains("\"completed\":true") }
            "tasks" to tasks
        }
    }

    private fun playerOverviewEndpoint(username: String): String {
        val player =
            getPlayerOnlineOrOffline(username)
                ?: return """{"error":"Player not found"}"""
        player.skills.setPlayer(player)
        player.taskManager.setPlayer(player)

        val skills = player.skills
        val taskManager = player.taskManager

        val totalTasks = Task.entries.size
        val completedTasks =
            Task.entries.count {
                taskManager.isCompleted(it)
            }

        val completionPercent =
            if (totalTasks == 0) 0 else (completedTasks * 100 / totalTasks)

        val skillsSummary =
            (0..24).map { id ->
                buildJson {
                    "id" to id
                    "name" to skills.getSkillName(id)
                    "level" to skills.getRealLevel(id)
                    "xp" to skills.getXp(id).toLong()
                }
            }

        return buildJson {
            "player" to username

            "displayName" to player.displayName
            "online" to isPlayerOnline(player.username)

            "totalLevel" to skills.getTotalLevel(player)
            "combatLevel" to skills.getCombatLevelWithSummoning()
            "totalXp" to skills.getTotalXP(player)

            "tasksCompleted" to completedTasks
            "tasksTotal" to totalTasks
            "completionPercent" to completionPercent

            "skills" to RawJsonArray(skillsSummary)
        }
    }

    private fun isPlayerOnline(username: String): Boolean =
        World.getPlayers().any {
            it != null && it.username.equals(username, ignoreCase = true)
        }

    fun stop() {
        server?.stop(1)
        server = null
        println("[LogApiServer] Stopped")
    }

    /** List every category folder that exists. */
    private fun categoriesEndpoint(): String {
        val cats = listDirectories(BASE_DIR)
        return buildJson {
            "categories" to cats
            "count" to cats.size
        }
    }

    /** List every username (file stem) that has logged in a given category. */
    private fun playersInCategoryEndpoint(category: String): String {
        val catDir = BASE_DIR.resolve(sanitize(category))
        val players =
            if (Files.exists(catDir)) {
                Files
                    .list(catDir)
                    .filter { it.toString().endsWith(".jsonl") }
                    .map { it.fileName.toString().removeSuffix(".jsonl") }
                    .sorted()
                    .toList()
            } else {
                emptyList()
            }

        return buildJson {
            "category" to category
            "players" to players
            "count" to players.size
        }
    }

    /**
     * /api/logs/{category}/{username}
     *
     * Optional filters: ?date=YYYY-MM-DD  ?limit=N  ?search=text
     */
    private fun playerFileEndpoint(
        category: String,
        username: String,
        params: Map<String, String>,
    ): String {
        val limit = (params["limit"]?.toIntOrNull() ?: 500).coerceIn(1, 5000)
        val date = params["date"]
        val search = params["search"]

        var entries = readPlayerEntries(category, username)

        if (date != null) entries = entries.filter { containsDate(it, date) }
        if (search != null) entries = entries.filter { it.contains(search, ignoreCase = true) }

        entries = entries.takeLast(limit)

        return buildJson {
            "category" to category
            "player" to username
            "count" to entries.size
            "entries" to RawJsonArray(entries)
        }
    }

    /**
     * /api/logs/player/{username}
     *
     * Searches every category's <username>.jsonl and merges results sorted by
     * timestamp.
     */
    private fun allCategoriesForPlayerEndpoint(
        username: String,
        params: Map<String, String>,
    ): String {
        val limit = (params["limit"]?.toIntOrNull() ?: 500).coerceIn(1, 5000)
        val date = params["date"]
        val search = params["search"]

        val categories = listDirectories(BASE_DIR)

        var allEntries =
            categories
                .flatMap { cat -> readPlayerEntries(cat, username) }
                .let { if (date != null) it.filter { line -> containsDate(line, date) } else it }
                .let { if (search != null) it.filter { line -> line.contains(search, ignoreCase = true) } else it }
                .sortedBy { extractTimestamp(it) }
                .takeLast(limit)

        return buildJson {
            "player" to username
            "count" to allEntries.size
            if (date != null) "date" to date
            "entries" to RawJsonArray(allEntries)
        }
    }

    private fun handle(
        exchange: HttpExchange,
        block: () -> String,
    ) {
        try {
            if (exchange.requestMethod != "GET") {
                respond(exchange, 405, """{"error":"Method not allowed"}""")
                return
            }

            val queryParams = parseQuery(exchange.requestURI.query)

            val keyFromHeader = exchange.requestHeaders.getFirst("X-Api-Key")?.trim()
            val keyFromQuery = queryParams["apiKey"]?.trim()

            val key =
                when {
                    !keyFromHeader.isNullOrBlank() -> keyFromHeader
                    !keyFromQuery.isNullOrBlank() -> keyFromQuery
                    else -> null
                }

            if (key == null || key != apiKey) {
                respond(
                    exchange,
                    401,
                    """
                    {
                      "error": "Unauthorized",
                      "debug": {
                        "header": "$keyFromHeader",
                        "query": "$keyFromQuery"
                      },
                      "hint": "Provide X-Api-Key header OR ?apiKey=YOUR_KEY"
                    }
                    """.trimIndent(),
                )
                return
            }

            respond(exchange, 200, block())
        } catch (e: Exception) {
            val msg = e.message?.replace("\"", "'") ?: "internal error"
            respond(exchange, 500, """{"error":"$msg"}""")
        } finally {
            exchange.close()
        }
    }

    private fun respond(
        exchange: HttpExchange,
        status: Int,
        body: String,
    ) {
        val bytes = body.toByteArray(Charsets.UTF_8)
        exchange.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
        exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
        exchange.sendResponseHeaders(status, bytes.size.toLong())
        exchange.responseBody.use { it.write(bytes) }
    }

    private fun respond404(message: String): String = """{"error":"Not found","hint":"$message"}"""

    /**
     * Read all non-blank lines from  data/logs/<category>/<username>.jsonl
     */
    private fun readPlayerEntries(
        category: String,
        username: String,
    ): List<String> {
        val file =
            BASE_DIR
                .resolve(sanitize(category))
                .resolve("${sanitize(username)}.jsonl")
                .toFile()
        if (!file.exists()) return emptyList()
        return try {
            BufferedReader(FileReader(file)).use { reader ->
                reader.lineSequence().filter { it.isNotBlank() }.toList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** True if the log line contains a timestamp on the given YYYY-MM-DD date. */
    private fun containsDate(
        line: String,
        date: String,
    ): Boolean = line.contains("\"timestamp\":\"$date", ignoreCase = false)

    private fun extractTimestamp(line: String): String {
        val match = Regex("\"timestamp\":\"([^\"]+)\"").find(line)
        return match?.groupValues?.get(1) ?: ""
    }

    private fun listDirectories(dir: Path): List<String> =
        if (Files.exists(dir)) {
            Files
                .list(dir)
                .filter { Files.isDirectory(it) }
                .map { it.fileName.toString() }
                .sorted()
                .toList()
        } else {
            emptyList()
        }

    private fun sanitize(s: String): String = s.replace(Regex("[^A-Za-z0-9._-]"), "_").lowercase()

    private fun parseQuery(query: String?): Map<String, String> {
        if (query.isNullOrBlank()) return emptyMap()
        return query
            .split("&")
            .mapNotNull { pair ->
                val parts = pair.split("=", limit = 2)
                if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
            }.toMap()
    }

    private class RawJsonArray(
        val lines: List<String>,
    )

    private fun buildJson(block: JsonBuilder.() -> Unit): String = JsonBuilder().apply(block).build()

    private class JsonBuilder {
        private val parts = mutableListOf<String>()

        infix fun String.to(value: Any?) {
            parts.add("\"$this\":${renderValue(value)}")
        }

        operator fun Boolean.invoke(block: JsonBuilder.() -> Unit) {
            if (this) block()
        }

        fun build(): String = "{${parts.joinToString(",")}}"

        private fun renderValue(value: Any?): String =
            when (value) {
                null -> "null"
                is Boolean -> value.toString()
                is Number -> value.toString()
                is String -> "\"${value.replace("\\","\\\\").replace("\"","\\\"")}\""
                is List<*> -> "[${value.joinToString(",") { renderValue(it) }}]"
                is RawJsonArray -> "[${value.lines.joinToString(",")}]"
                else -> "\"$value\""
            }
    }
}
