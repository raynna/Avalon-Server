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
import java.util.concurrent.ConcurrentHashMap

object WikiApi {
    private val client = OkHttpClient()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val dataDir = File("data/npcs/wikidata/")
    private val failedFile = File("data/npcs/wikidata/failed.json")

    /**
     * Fast runtime lookup by actual server npc id.
     */
    private val npcDataById: MutableMap<Int, NpcData> = ConcurrentHashMap()

    /**
     * Grouped by normalized npc name -> one json file per npc type.
     * Example: ice_giant.json
     */
    private val npcGroups: MutableMap<String, NpcGroup> = ConcurrentHashMap()

    private val failedNpc: MutableMap<Int, FailureEntry> = ConcurrentHashMap()

    val disabled = false

    init {
        dataDir.mkdirs()

        dataDir.listFiles()?.forEach { npcFile ->
            if (!npcFile.name.endsWith(".json")) return@forEach
            if (npcFile.name == failedFile.name) return@forEach

            try {
                val group = gson.fromJson(npcFile.readText(), NpcGroup::class.java) ?: return@forEach
                val key = toFileName(group.name)
                npcGroups[key] = group

                group.variants.forEach { variant ->
                    variant.ids.forEach { id ->
                        npcDataById[id] = variant.toNpcData(id)
                    }
                }
            } catch (e: Exception) {
                println("⚠ Failed to load NPC file: ${npcFile.name} - ${e.message}")
            }
        }

        if (failedFile.exists()) {
            try {
                val type = object : com.google.gson.reflect.TypeToken<Map<Int, FailureEntry>>() {}.type
                val loaded: Map<Int, FailureEntry>? = gson.fromJson(failedFile.readText(), type)
                if (loaded != null) {
                    failedNpc.putAll(loaded)
                }
            } catch (e: Exception) {
                println("⚠ Failed to load failure file: ${failedFile.name} - ${e.message}")
            }
        }
    }

    @JvmStatic
    @Synchronized
    fun hasData(npcId: Int): Boolean = npcDataById.containsKey(npcId)

    fun hasFailed(npcId: Int): Boolean = failedNpc.containsKey(npcId)

    fun getData(npcId: Int): NpcData? = npcDataById[npcId]

    fun clearFailure(npcId: Int) {
        if (failedNpc.remove(npcId) != null) {
            persistFailures()
        }
    }

    @JvmStatic
    @Synchronized
    fun dumpData(
        npcId: Int,
        npcName: String,
        combatLevel: Int,
    ): Boolean {
        if (disabled) return false

        if (npcDataById.containsKey(npcId)) {
            println("⚠ Data already exists for $npcName (ID=$npcId)")
            return true
        }

        failedNpc[npcId]?.let {
            return false
        }

        return when (val result = fetchNpcCombatData(npcName)) {
            is FetchResult.Failure -> {
                println("❌ Failed to fetch combat data for $npcName — ${result.reason}")
                markFailure(
                    npcId = npcId,
                    npcName = npcName,
                    reason = result.reason,
                    details = result.details,
                    expectedCombatLevel = combatLevel,
                )
                false
            }

            is FetchResult.Success -> {
                val npcVersions = result.list
                val npc = npcVersions.minByOrNull { kotlin.math.abs(it.combatLevel - combatLevel) }

                if (npc == null) {
                    val msg = "No combat level match found"
                    println("⚠ $msg for $npcName")
                    markFailure(
                        npcId = npcId,
                        npcName = npcName,
                        reason = msg,
                        details = "Requested=$combatLevel; found none",
                        expectedCombatLevel = combatLevel,
                    )
                    false
                } else {
                    if (npc.combatLevel != combatLevel) {
                        println("⚠ Combat level mismatch for $npcName. Local=$combatLevel Wiki=${npc.combatLevel}")
                    }

                    val finalNpc = npc.copy(id = npcId)
                    npcDataById[npcId] = finalNpc
                    persistNpc(finalNpc)

                    if (failedNpc.remove(npcId) != null) {
                        persistFailures()
                    }

                    println("✅ Combat data saved for $npcName (ID=$npcId)")
                    true
                }
            }
        }
    }

    fun retryFailed(reasonContains: String? = null) {
        val snapshot = failedNpc.toMap()
        for ((npcId, fail) in snapshot) {
            if (reasonContains != null && !fail.reason.contains(reasonContains, ignoreCase = true)) {
                continue
            }

            println("🔁 Retrying ${fail.npcName} (ID=$npcId) — previous reason: ${fail.reason}")
            clearFailure(npcId)
            dumpData(npcId, fail.npcName, fail.expectedCombatLevel ?: 0)
        }
    }

    sealed class FetchResult {
        data class Success(
            val list: List<NpcData>,
        ) : FetchResult()

        data class Failure(
            val reason: String,
            val details: String? = null,
        ) : FetchResult()
    }

    fun fetchNpcCombatData(name: String): FetchResult {
        val encoded = URLEncoder.encode(name, StandardCharsets.UTF_8)
        val url = "https://oldschool.runescape.wiki/api.php?action=parse&prop=wikitext&format=json&redirects=1&page=$encoded"

        println("🔹 Fetching URL: $url")

        val request =
            Request
                .Builder()
                .url(url)
                .header("User-Agent", "AvalonBot/1.0 (contact: me@example.com)")
                .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return FetchResult.Failure(
                        reason = "HTTP ${response.code}",
                        details = response.message,
                    )
                }

                val body =
                    response.body?.string()
                        ?: return FetchResult.Failure("Empty body")

                val json =
                    try {
                        Gson().fromJson(body, JsonObject::class.java)
                    } catch (e: Exception) {
                        return FetchResult.Failure("JSON parse error", e.message)
                    }

                val wikitext =
                    json["parse"]
                        ?.asJsonObject
                        ?.get("wikitext")
                        ?.asJsonObject
                        ?.get("*")
                        ?.asString
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

    private fun parseCombatDataFromInfobox(
        wikitext: String,
        name: String,
    ): List<NpcData> {
        val npcList = mutableListOf<NpcData>()

        val regex = "\\{\\{Infobox Monster\\s*(.*?)(?=\\}\\}\\}$|\\n\\}\\}\\}$|\\n\\{\\{)".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matches = regex.findAll(wikitext)

        for (match in matches) {
            val block = match.groupValues[1]
            val map = mutableMapOf<String, String>()

            var currentLine = StringBuilder()
            block.lines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("|")) {
                    if (currentLine.isNotEmpty()) {
                        processInfoboxLine(currentLine.toString(), map)
                        currentLine = StringBuilder()
                    }
                    currentLine.append(trimmed.substring(1))
                } else if (currentLine.isNotEmpty()) {
                    currentLine.append(" ").append(trimmed)
                }
            }
            if (currentLine.isNotEmpty()) {
                processInfoboxLine(currentLine.toString(), map)
            }

            fun yesNo(key: String): Boolean {
                val value = map[key.lowercase()]?.trim()?.lowercase() ?: return false
                return value == "yes" || value == "true"
            }

            fun extractNumberFromValue(value: String): Int {
                if (value.startsWith("{{") && value.contains("plainlist")) {
                    val numbers = Regex("\\d+").findAll(value).map { it.value.toInt() }.toList()
                    if (numbers.isNotEmpty()) return numbers.first()
                }
                return value.toIntOrNull() ?: 0
            }

            fun intVal(
                vararg keys: String,
                default: Int = 0,
            ): Int {
                for (key in keys) {
                    map[key.lowercase()]?.let { value ->
                        return extractNumberFromValue(value)
                    }
                }
                return default
            }

            fun doubleVal(key: String): Double? = map[key.lowercase()]?.toDoubleOrNull()

            val versionKeys = map.keys.filter { it.startsWith("version") }.toMutableList()
            if (versionKeys.isEmpty()) {
                versionKeys.add("1")
            }

            val hasVersionedStats =
                versionKeys.any { versionKey ->
                    val versionNum = versionKey.removePrefix("version").toIntOrNull() ?: 1
                    map.containsKey("combat$versionNum") ||
                        map.containsKey("att$versionNum") ||
                        map.containsKey("str$versionNum")
                }

            versionKeys.forEach { versionKey ->
                val versionNum = versionKey.removePrefix("version").toIntOrNull() ?: 1

                fun getStatValue(
                    baseKey: String,
                    vararg altKeys: String,
                    default: Int = 0,
                ): Int {
                    if (hasVersionedStats) {
                        val versionedKey = "$baseKey$versionNum"
                        map[versionedKey]?.let { value ->
                            return extractNumberFromValue(value)
                        }
                    }

                    map[baseKey]?.let { value ->
                        return extractNumberFromValue(value)
                    }

                    for (altKey in altKeys) {
                        map[altKey]?.let { value ->
                            return extractNumberFromValue(value)
                        }
                    }

                    return default
                }

                val ids =
                    map["id$versionNum"]
                        ?.split(",")
                        ?.mapNotNull { it.trim().toIntOrNull() }
                        ?: listOf(-1)

                ids.forEach { wikiNpcId ->
                    val drange = intVal("drange")

                    val rangedDefence =
                        mapOf(
                            "light" to (intVal("dlight").takeIf { it > 0 } ?: drange),
                            "standard" to (intVal("dstandard").takeIf { it > 0 } ?: drange),
                            "heavy" to (intVal("dheavy").takeIf { it > 0 } ?: drange),
                        )

                    val meleeDefence =
                        mapOf(
                            "stab" to intVal("dstab"),
                            "slash" to intVal("dslash"),
                            "crush" to intVal("dcrush"),
                        )

                    val magicDefence =
                        mapOf(
                            "magic" to intVal("dmagic"),
                        )

                    val weaknesses = mutableMapOf<String, Int>()
                    map["elementalweaknesstype"]?.let { type ->
                        val percent = map["elementalweaknesspercent"]?.toIntOrNull() ?: 0
                        weaknesses[type.trim().lowercase()] = percent
                    }

                    val maxHitRaw =
                        if (hasVersionedStats) {
                            val current = map["max hit$versionNum"]
                            val currentVal =
                                current?.let {
                                    Regex("\\d+").find(it)?.value?.toIntOrNull() ?: 0
                                } ?: 0

                            if (currentVal > 0) {
                                current
                            } else {
                                versionKeys
                                    .mapNotNull { map["max hit${it.removePrefix("version")}"] }
                                    .firstOrNull {
                                        (Regex("\\d+").find(it)?.value?.toIntOrNull() ?: 0) > 0
                                    }
                            }
                        } else {
                            map["max hit"]
                        }

                    val npcData =
                        NpcData(
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
                            magicBonus = intVal("amagic", "magicbns"),
                            magicStrengthBonus = intVal("mbns", "amagicbns"),
                            rangedBonus = intVal("arange", "rangebns"),
                            rangedStrengthBonus = intVal("rngbns", "rangedstrbns"),
                            meleeDefence = meleeDefence,
                            magicDefence = magicDefence,
                            rangedDefence = rangedDefence,
                            weaknesses = weaknesses,
                            immunities =
                                mapOf(
                                    "poison" to yesNo("immunepoison"),
                                    "venom" to yesNo("immunevenom"),
                                    "cannons" to yesNo("immunecannon"),
                                    "thralls" to yesNo("immunethrall"),
                                    "burn" to false,
                                ),
                            releaseDate = map["release"],
                            aliases = emptyList(),
                            size = map["size"],
                            examine = map["examine"],
                            attributes = map["attributes"]?.split(",")?.map { it.trim() } ?: emptyList(),
                            xpBonus = doubleVal("xpbonus"),
                            maxHit = parseMaxHit(maxHitRaw),
                            aggressive = yesNo("aggressive"),
                            poisonous = null,
                            attackStyles = map["attack style"]?.split(",")?.map { it.trim() } ?: emptyList(),
                            attackSpeedTicks = intVal("attack speed").takeIf { it > 0 } ?: 4,
                            respawnTicks = getStatValue("respawn").takeIf { it > 0 } ?: 25,
                            slayerLevel = null,
                            slayerXp = getStatValue("slayxp"),
                            categories = emptyList(),
                            assignedBy = map["assignedby"]?.split(",")?.map { it.trim() } ?: emptyList(),
                        )

                    println(
                        "Parsed $name: " +
                            "combat=${npcData.combatLevel}, att=${npcData.attackLevel}, str=${npcData.strengthLevel}, def=${npcData.defenceLevel}, " +
                            "mage=${npcData.magicLevel}, range=${npcData.rangedLevel}, hp=${npcData.constitutionLevel}, " +
                            "attBonus=${npcData.attackBonus}, strBonus=${npcData.strengthBonus}, " +
                            "mageBonus=${npcData.magicBonus}, mageStrBonus=${npcData.magicStrengthBonus}, " +
                            "rangeBonus=${npcData.rangedBonus}, rangeStrBonus=${npcData.rangedStrengthBonus}, " +
                            "maxHit=${npcData.maxHit["default"]}, " +
                            "meleeDef=[stab=${npcData.meleeDefence["stab"]}, slash=${npcData.meleeDefence["slash"]}, crush=${npcData.meleeDefence["crush"]}], " +
                            "mageDef=${npcData.magicDefence["magic"]}, " +
                            "rangeDef=[light=${npcData.rangedDefence["light"]}, standard=${npcData.rangedDefence["standard"]}, heavy=${npcData.rangedDefence["heavy"]}], " +
                            "weaknesses=${npcData.weaknesses}, " +
                            "attackSpeed=${npcData.attackSpeedTicks}, respawn=${npcData.respawnTicks}, " +
                            "slayerXp=${npcData.slayerXp}, aggressive=${npcData.aggressive}",
                    )

                    npcList.add(npcData)
                }
            }
        }

        return npcList
    }

    fun parseMaxHit(value: String?): Map<String, Int> {
        if (value == null) return emptyMap()

        val result = mutableMapOf<String, Int>()
        val regex = Regex("(\\d+)\\s*\\(([^)]+)\\)")
        val matches = regex.findAll(value)

        for (m in matches) {
            val hit = m.groupValues[1].toIntOrNull() ?: continue
            val style = m.groupValues[2].trim().lowercase()
            result[style] = hit
        }

        if (result.isEmpty()) {
            val single = Regex("\\d+").find(value)?.value?.toIntOrNull()
            if (single != null) {
                result["default"] = single
            }
        }

        return result
    }

    private fun processInfoboxLine(
        line: String,
        map: MutableMap<String, String>,
    ) {
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
        expectedCombatLevel: Int? = null,
    ) {
        val now = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val existing = failedNpc[npcId]

        val entry =
            if (existing == null) {
                FailureEntry(
                    npcId = npcId,
                    npcName = npcName,
                    reason = reason,
                    details = details,
                    firstSeen = now,
                    lastSeen = now,
                    attempts = 1,
                    expectedCombatLevel = expectedCombatLevel,
                )
            } else {
                existing.copy(
                    reason = reason,
                    details = details ?: existing.details,
                    lastSeen = now,
                    attempts = existing.attempts + 1,
                    expectedCombatLevel = expectedCombatLevel ?: existing.expectedCombatLevel,
                )
            }

        failedNpc[npcId] = entry
        persistFailures()
    }

    @Synchronized
    private fun persistNpc(npc: NpcData) {
        val key = toFileName(npc.name)
        val group = npcGroups[key] ?: NpcGroup(name = npc.name, variants = mutableListOf())

        val existingIndex =
            group.variants.indexOfFirst { variant ->
                variant.matchesSameVariant(npc)
            }

        if (existingIndex >= 0) {
            val existing = group.variants[existingIndex]
            val mergedIds = (existing.ids + npc.id).distinct().sorted()

            group.variants[existingIndex] =
                existing.copy(
                    ids = mergedIds,
                )
        } else {
            group.variants.add(NpcVariant.fromNpcData(npc))
        }

        npcGroups[key] = group

        val npcFile = File(dataDir, "$key.json")
        npcFile.writeText(gson.toJson(group))
    }

    private fun persistFailures() {
        failedFile.parentFile.mkdirs()
        failedFile.writeText(gson.toJson(failedNpc.toSortedMap()))
    }

    private fun toFileName(name: String): String =
        name
            .lowercase()
            .replace("'", "")
            .replace("[^a-z0-9]+".toRegex(), "_")
            .trim('_')

    data class FailureEntry(
        val npcId: Int,
        val npcName: String,
        val reason: String,
        val details: String? = null,
        val firstSeen: String,
        val lastSeen: String,
        val attempts: Int,
        val expectedCombatLevel: Int? = null,
    )

    data class NpcGroup(
        val name: String,
        val variants: MutableList<NpcVariant>,
    )

    data class NpcVariant(
        val ids: List<Int>,
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
        val weaknesses: Map<String, Int>,
        val immunities: Map<String, Boolean>,
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
        val assignedBy: List<String>,
    ) {
        fun matchesSameVariant(npc: NpcData): Boolean =
            combatLevel == npc.combatLevel &&
                attackLevel == npc.attackLevel &&
                strengthLevel == npc.strengthLevel &&
                defenceLevel == npc.defenceLevel &&
                magicLevel == npc.magicLevel &&
                rangedLevel == npc.rangedLevel &&
                constitutionLevel == npc.constitutionLevel &&
                attackBonus == npc.attackBonus &&
                strengthBonus == npc.strengthBonus &&
                magicBonus == npc.magicBonus &&
                magicStrengthBonus == npc.magicStrengthBonus &&
                rangedBonus == npc.rangedBonus &&
                rangedStrengthBonus == npc.rangedStrengthBonus &&
                meleeDefence == npc.meleeDefence &&
                magicDefence == npc.magicDefence &&
                rangedDefence == npc.rangedDefence &&
                weaknesses == npc.weaknesses &&
                immunities == npc.immunities &&
                maxHit == npc.maxHit &&
                aggressive == npc.aggressive &&
                attackStyles == npc.attackStyles &&
                attackSpeedTicks == npc.attackSpeedTicks &&
                respawnTicks == npc.respawnTicks &&
                slayerXp == npc.slayerXp

        fun toNpcData(id: Int): NpcData =
            NpcData(
                id = id,
                name = "",
                members = members,
                combatLevel = combatLevel,
                attackLevel = attackLevel,
                strengthLevel = strengthLevel,
                defenceLevel = defenceLevel,
                magicLevel = magicLevel,
                rangedLevel = rangedLevel,
                constitutionLevel = constitutionLevel,
                attackBonus = attackBonus,
                strengthBonus = strengthBonus,
                magicBonus = magicBonus,
                magicStrengthBonus = magicStrengthBonus,
                rangedBonus = rangedBonus,
                rangedStrengthBonus = rangedStrengthBonus,
                meleeDefence = meleeDefence,
                magicDefence = magicDefence,
                rangedDefence = rangedDefence,
                weaknesses = weaknesses,
                immunities = immunities,
                releaseDate = releaseDate,
                aliases = aliases,
                size = size,
                examine = examine,
                attributes = attributes,
                xpBonus = xpBonus,
                maxHit = maxHit,
                aggressive = aggressive,
                poisonous = poisonous,
                attackStyles = attackStyles,
                attackSpeedTicks = attackSpeedTicks,
                respawnTicks = respawnTicks,
                slayerLevel = slayerLevel,
                slayerXp = slayerXp,
                categories = categories,
                assignedBy = assignedBy,
            )

        companion object {
            fun fromNpcData(npc: NpcData): NpcVariant =
                NpcVariant(
                    ids = listOf(npc.id),
                    members = npc.members,
                    combatLevel = npc.combatLevel,
                    attackLevel = npc.attackLevel,
                    strengthLevel = npc.strengthLevel,
                    defenceLevel = npc.defenceLevel,
                    magicLevel = npc.magicLevel,
                    rangedLevel = npc.rangedLevel,
                    constitutionLevel = npc.constitutionLevel,
                    attackBonus = npc.attackBonus,
                    strengthBonus = npc.strengthBonus,
                    magicBonus = npc.magicBonus,
                    magicStrengthBonus = npc.magicStrengthBonus,
                    rangedBonus = npc.rangedBonus,
                    rangedStrengthBonus = npc.rangedStrengthBonus,
                    meleeDefence = npc.meleeDefence,
                    magicDefence = npc.magicDefence,
                    rangedDefence = npc.rangedDefence,
                    weaknesses = npc.weaknesses,
                    immunities = npc.immunities,
                    releaseDate = npc.releaseDate,
                    aliases = npc.aliases,
                    size = npc.size,
                    examine = npc.examine,
                    attributes = npc.attributes,
                    xpBonus = npc.xpBonus,
                    maxHit = npc.maxHit,
                    aggressive = npc.aggressive,
                    poisonous = npc.poisonous,
                    attackStyles = npc.attackStyles,
                    attackSpeedTicks = npc.attackSpeedTicks,
                    respawnTicks = npc.respawnTicks,
                    slayerLevel = npc.slayerLevel,
                    slayerXp = npc.slayerXp,
                    categories = npc.categories,
                    assignedBy = npc.assignedBy,
                )
        }
    }

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
        val weaknesses: Map<String, Int>,
        val immunities: Map<String, Boolean>,
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
        val assignedBy: List<String>,
    ) {
        fun matchesSameVariant(other: NpcData): Boolean =
            combatLevel == other.combatLevel &&
                attackLevel == other.attackLevel &&
                strengthLevel == other.strengthLevel &&
                defenceLevel == other.defenceLevel &&
                magicLevel == other.magicLevel &&
                rangedLevel == other.rangedLevel &&
                constitutionLevel == other.constitutionLevel &&
                attackBonus == other.attackBonus &&
                strengthBonus == other.strengthBonus &&
                magicBonus == other.magicBonus &&
                magicStrengthBonus == other.magicStrengthBonus &&
                rangedBonus == other.rangedBonus &&
                rangedStrengthBonus == other.rangedStrengthBonus &&
                meleeDefence == other.meleeDefence &&
                magicDefence == other.magicDefence &&
                rangedDefence == other.rangedDefence &&
                weaknesses == other.weaknesses &&
                immunities == other.immunities &&
                maxHit == other.maxHit &&
                aggressive == other.aggressive &&
                attackStyles == other.attackStyles &&
                attackSpeedTicks == other.attackSpeedTicks &&
                respawnTicks == other.respawnTicks &&
                slayerXp == other.slayerXp
    }
}
