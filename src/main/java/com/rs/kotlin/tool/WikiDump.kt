package com.rs.kotlin.tool

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object WikiApi {
    private val client = OkHttpClient()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val file = File("data/npcs/npc_data.json")
    private val failedFile = File("data/npcs/npc_data_failed.json")
    private val npcData: MutableMap<Int, NpcData> = ConcurrentHashMap()
    private val failedNpc: MutableMap<Int, FailureEntry> = ConcurrentHashMap()

    init {
        file.parentFile.mkdirs()
        if (file.exists()) {
            val type = object : com.google.gson.reflect.TypeToken<Map<Int, NpcData>>() {}.type
            npcData.putAll(gson.fromJson(file.readText(), type))
        }
        if (failedFile.exists()) {
            val type = object : com.google.gson.reflect.TypeToken<Map<Int, FailureEntry>>() {}.type
            failedNpc.putAll(gson.fromJson(failedFile.readText(), type))
        }
    }

    val disabled = false

    @JvmStatic
    @Synchronized
    fun hasData(npcId: Int): Boolean = npcData.containsKey(npcId)
    fun hasFailed(npcId: Int): Boolean = failedNpc.containsKey(npcId)

    fun clearFailure(npcId: Int) {
        if (failedNpc.remove(npcId) != null) persistFailures()
    }

    @JvmStatic
    @Synchronized
    fun dumpData(npcId: Int, npcName: String, combatLevel: Int): Boolean {
        if (disabled) return false

        if (npcData.containsKey(npcId)) {
            println("⚠ Data already exists for $npcName (ID=$npcId)")
            return true
        }

        // Skip if previously failed
        failedNpc[npcId]?.let {
            return false
        }

        return when (val result = fetchNpcCombatData(npcName)) {
            is FetchResult.Failure -> {
                println("❌ Failed to fetch combat data for $npcName — ${result.reason}")
                markFailure(npcId, npcName, reason = result.reason, details = result.details)
                false
            }
            is FetchResult.Success -> {
                val npcVersions = result.list
                val npc = npcVersions.minByOrNull { kotlin.math.abs(it.combatLevel - combatLevel) }
                if (npc == null) {
                    val msg = "No combat level match found"
                    println("⚠ $msg for $npcName")
                    markFailure(npcId, npcName, reason = msg, details = "Requested=$combatLevel; found none")
                    false
                } else {
                    if (npc.combatLevel != combatLevel) {
                        println("⚠ Combat level mismatch for $npcName. Local=$combatLevel Wiki=${npc.combatLevel}")
                    }

                    npcData[npcId] = npc.copy(id = npcId)
                    persistData()
                    // Clear any previous failure entry if we finally succeeded
                    if (failedNpc.remove(npcId) != null) persistFailures()
                    println("✅ Combat data saved for $npcName (ID=$npcId)")
                    true
                }
            }
        }
    }


    fun retryFailed(reasonContains: String? = null) {
        val snapshot = failedNpc.toMap()
        for ((npcId, fail) in snapshot) {
            if (reasonContains != null && !fail.reason.contains(reasonContains, ignoreCase = true)) continue
            println("🔁 Retrying ${fail.npcName} (ID=$npcId) — previous reason: ${fail.reason}")
            // Clear before retry so we don't auto-skip
            clearFailure(npcId)
            dumpData(npcId, fail.npcName, fail.expectedCombatLevel ?: 0)
        }
    }


    sealed class FetchResult {
        data class Success(val list: List<NpcData>) : FetchResult()
        data class Failure(val reason: String, val details: String? = null) : FetchResult()
    }

    fun fetchNpcCombatData(name: String): FetchResult {
        val encoded = URLEncoder.encode(name, StandardCharsets.UTF_8)
        val url = "https://oldschool.runescape.wiki/api.php?action=parse&prop=wikitext&format=json&redirects=1&page=$encoded"

        println("🔹 Fetching URL: $url")
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "AvalonBot/1.0 (contact: me@example.com)")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return FetchResult.Failure(
                        reason = "HTTP ${response.code}",
                        details = response.message
                    )
                }

                val body = response.body?.string()
                    ?: return FetchResult.Failure("Empty body")

                val json = try {
                    Gson().fromJson(body, JsonObject::class.java)
                } catch (e: Exception) {
                    return FetchResult.Failure("JSON parse error", e.message)
                }

                val wikitext = json["parse"]?.asJsonObject
                    ?.get("wikitext")?.asJsonObject
                    ?.get("*")?.asString
                    ?: return FetchResult.Failure("'wikitext' not found in JSON")

                val list = parseCombatDataFromInfobox(wikitext, name)
                if (list.isEmpty()) {
                    return FetchResult.Failure("No Infobox Monster found")
                }
                FetchResult.Success(list)
            }
        } catch (e: Exception) {
            FetchResult.Failure("Request exception", e.message)
        }
    }

    private fun parseCombatDataFromInfobox(wikitext: String, name: String): List<NpcData> {
        val npcList = mutableListOf<NpcData>()

        // FIX: Better regex to capture the entire Infobox Monster template including nested templates
        val regex = "\\{\\{Infobox Monster\\s*(.*?)(?=\\}\\}\\}$|\\n\\}\\}\\}$|\\n\\{\\{)".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matches = regex.findAll(wikitext)

        for (match in matches) {
            val block = match.groupValues[1]
            val map = mutableMapOf<String, String>()

            // Parse the block line by line
            var currentLine = StringBuilder()
            block.lines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("|")) {
                    // Process the previous line if it exists
                    if (currentLine.isNotEmpty()) {
                        processInfoboxLine(currentLine.toString(), map)
                        currentLine = StringBuilder()
                    }
                    currentLine.append(trimmed.substring(1)) // Remove the leading |
                } else if (currentLine.isNotEmpty()) {
                    // Continue the current line (for multi-line values)
                    currentLine.append(" ").append(trimmed)
                }
            }
            // Process the last line
            if (currentLine.isNotEmpty()) {
                processInfoboxLine(currentLine.toString(), map)
            }

            fun yesNo(key: String) = map[key.lowercase()]?.equals("yes", true) ?: false

            fun extractNumberFromValue(value: String): Int {
                // Handle template values like {{plainlist|...}}
                if (value.startsWith("{{") && value.contains("plainlist")) {
                    // Extract the first numeric value from plainlist templates
                    val numbers = Regex("\\d+").findAll(value).map { it.value.toInt() }.toList()
                    if (numbers.isNotEmpty()) return numbers.first()
                }
                // Handle regular numeric values
                return value.toIntOrNull() ?: 0
            }

            fun intVal(vararg keys: String, default: Int = 0): Int {
                for (key in keys) {
                    val lowerKey = key.lowercase()
                    map[lowerKey]?.let { value ->
                        return extractNumberFromValue(value)
                    }
                }
                return default
            }

            fun doubleVal(key: String) = map[key.lowercase()]?.toDoubleOrNull()

            val versionKeys = map.keys.filter { it.startsWith("version") }.toMutableList()
            if (versionKeys.isEmpty()) versionKeys.add("1")

            // For monsters like Tormented Demon that have versions but no versioned stats,
            // we need to handle them differently
            val hasVersionedStats = versionKeys.any { versionKey ->
                val versionNum = versionKey.removePrefix("version").toIntOrNull() ?: 1
                map.containsKey("combat$versionNum") || map.containsKey("att$versionNum") || map.containsKey("str$versionNum")
            }

            versionKeys.forEach { versionKey ->
                val versionNum = versionKey.removePrefix("version").toIntOrNull() ?: 1

                fun getStatValue(baseKey: String, vararg altKeys: String, default: Int = 0): Int {
                    // If this monster has versioned stats, try versioned key first
                    if (hasVersionedStats) {
                        val versionedKey = "$baseKey$versionNum"
                        map[versionedKey]?.let { value ->
                            return extractNumberFromValue(value)
                        }
                    }

                    // Always try base key (e.g., "combat", "att", "str")
                    map[baseKey]?.let { value ->
                        return extractNumberFromValue(value)
                    }

                    // Then try alternative keys
                    for (altKey in altKeys) {
                        map[altKey]?.let { value ->
                            return extractNumberFromValue(value)
                        }
                    }

                    return default
                }

                val ids = map["id$versionNum"]?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: listOf(-1)

                ids.forEach { wikiNpcId ->
                    val drange = intVal("drange")

                    val rangedDefence = mapOf(
                        "light" to (intVal("dlight").takeIf { it > 0 } ?: drange),
                        "standard" to (intVal("dstandard").takeIf { it > 0 } ?: drange),
                        "heavy" to (intVal("dheavy").takeIf { it > 0 } ?: drange)
                    )

                    val meleeDefence = mapOf(
                        "stab" to intVal("dstab"),
                        "slash" to intVal("dslash"),
                        "crush" to intVal("dcrush")
                    )

                    val magicDefence = mapOf(
                        "magic" to intVal("dmagic")
                    )

                    val npcData = NpcData(
                        id = wikiNpcId,
                        name = name,
                        members = yesNo("members"),
                        combatLevel = getStatValue("combat"),
                        attackLevel = getStatValue("att", "attack"),
                        strengthLevel = getStatValue("str", "strength"),
                        defenceLevel = getStatValue("def", "defence"),
                        magicLevel = getStatValue("mage", "magic"),
                        rangedLevel = getStatValue("range", "ranged"),
                        constitutionLevel = getStatValue("hitpoints", "hp"),
                        attackBonus = intVal("attbns", "attbonus"),
                        strengthBonus = intVal("strbns", "strbonus"),
                        magicBonus = intVal("mbns", "magicbns"),
                        magicStrengthBonus = intVal("amagic", "amagicbns"),
                        rangedBonus = intVal("arange", "rangebns"),
                        rangedStrengthBonus = intVal("rngbns", "rangedstrbns"),
                        meleeDefence = meleeDefence,
                        magicDefence = magicDefence,
                        rangedDefence = rangedDefence,
                        immunities = mapOf(
                            "poison" to yesNo("immunepoison"),
                            "venom" to yesNo("immunevenom"),
                            "cannons" to yesNo("immunecannon"),
                            "thralls" to yesNo("immunethrall"),
                            "burn" to false
                        ),
                        releaseDate = map["release"],
                        aliases = emptyList(),
                        size = map["size"],
                        examine = map["examine"],
                        attributes = map["attributes"]?.split(",")?.map { it.trim() } ?: emptyList(),
                        xpBonus = doubleVal("xpbonus"),
                        maxHit = mapOf("maxhit" to getStatValue("max hit")),
                        aggressive = yesNo("aggressive"),
                        poisonous = null,
                        attackStyles = map["attack style"]?.split(",")?.map { it.trim() } ?: emptyList(),
                        attackSpeedTicks = intVal("attack speed").takeIf { it > 0 } ?: 4,
                        respawnTicks = getStatValue("respawn").takeIf { it > 0 } ?: 25,
                        slayerLevel = null,
                        slayerXp = getStatValue("slayxp"),
                        categories = emptyList(),
                        assignedBy = map["assignedby"]?.split(",")?.map { it.trim() } ?: emptyList()
                    )

                    println("Parsed $name: " +
                            "combat=${npcData.combatLevel}, att=${npcData.attackLevel}, str=${npcData.strengthLevel}, def=${npcData.defenceLevel}, " +
                            "mage=${npcData.magicLevel}, range=${npcData.rangedLevel}, hp=${npcData.constitutionLevel}, " +
                            "attBonus=${npcData.attackBonus}, strBonus=${npcData.strengthBonus}, " +
                            "mageBonus=${npcData.magicBonus}, mageStrBonus=${npcData.magicStrengthBonus}, " +
                            "rangeBonus=${npcData.rangedBonus}, rangeStrBonus=${npcData.rangedStrengthBonus}, " +
                            "maxHit=${npcData.maxHit["maxhit"]}, " +
                            "meleeDef=[stab=${npcData.meleeDefence["stab"]}, slash=${npcData.meleeDefence["slash"]}, crush=${npcData.meleeDefence["crush"]}], " +
                            "mageDef=${npcData.magicDefence["magic"]}, " +
                            "rangeDef=[light=${npcData.rangedDefence["light"]}, standard=${npcData.rangedDefence["standard"]}, heavy=${npcData.rangedDefence["heavy"]}], " +
                            "attackSpeed=${npcData.attackSpeedTicks}, respawn=${npcData.respawnTicks}, " +
                            "slayerXp=${npcData.slayerXp}, aggressive=${npcData.aggressive}")

                    npcList.add(npcData)
                }
            }
        }

        return npcList
    }

    private fun processInfoboxLine(line: String, map: MutableMap<String, String>) {
        val parts = line.split("=", limit = 2)
        if (parts.size == 2) {
            val key = parts[0].trim().lowercase()
            val value = parts[1].trim()
            map[key] = value
        }
    }
    private fun markFailure(
        npcId: Int,
        npcName: String,
        reason: String,
        details: String? = null,
        expectedCombatLevel: Int? = null
    ) {
        val now = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val existing = failedNpc[npcId]
        val entry = if (existing == null) {
            FailureEntry(
                npcId = npcId,
                npcName = npcName,
                reason = reason,
                details = details,
                firstSeen = now,
                lastSeen = now,
                attempts = 1,
                expectedCombatLevel = expectedCombatLevel
            )
        } else {
            existing.copy(
                reason = reason,
                details = details ?: existing.details,
                lastSeen = now,
                attempts = existing.attempts + 1
            )
        }
        failedNpc[npcId] = entry
        persistFailures()
    }

    @Synchronized
    private fun persistData() {
        val snapshot = TreeMap(npcData) // sorted copy
        file.writeText(gson.toJson(snapshot))
    }

    private fun persistFailures() {
        failedFile.parentFile.mkdirs()
        failedFile.writeText(gson.toJson(failedNpc.toSortedMap()))
    }

    data class FailureEntry(
        val npcId: Int,
        val npcName: String,
        val reason: String,
        val details: String? = null,
        val firstSeen: String,
        val lastSeen: String,
        val attempts: Int,
        val expectedCombatLevel: Int? = null
    )


    data class NpcData(
        val id: Int,
        val name: String,
        val members: Boolean,
        val combatLevel: Int,
        val attackLevel: Int,
        val strengthLevel: Int,
        val defenceLevel: Int,
        val magicLevel: Int,
        val rangedLevel: Int,
        val constitutionLevel: Int,
        val attackBonus: Int,
        val strengthBonus: Int,
        val magicBonus: Int,
        val magicStrengthBonus: Int,
        val rangedBonus: Int,
        val rangedStrengthBonus: Int,
        val meleeDefence: Map<String, Int>,
        val magicDefence: Map<String, Int>,
        val rangedDefence: Map<String, Int>,
        val immunities: Map<String, Boolean>,

        // Optional / ignored for combat-only
        val releaseDate: String?,
        val aliases: List<String>,
        val size: String?,
        val examine: String?,
        val attributes: List<String>,
        val xpBonus: Double?,
        val maxHit: Map<String, Int>,
        val aggressive: Boolean,
        val poisonous: Map<String, Any>?,
        val attackStyles: List<String>,
        val attackSpeedTicks: Int,
        val respawnTicks: Int,
        val slayerLevel: Int?,
        val slayerXp: Int?,
        val categories: List<String>,
        val assignedBy: List<String>
    )
}
