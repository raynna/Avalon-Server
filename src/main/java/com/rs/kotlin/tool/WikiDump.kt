package com.rs.kotlin.tool

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WikiApi {
    private val client = OkHttpClient()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val file = File("data/npcs/npc_data.json")
    private val npcData: MutableMap<Int, NpcData> = sortedMapOf()

    init {
        file.parentFile.mkdirs()
        if (file.exists()) {
            val type = object : com.google.gson.reflect.TypeToken<Map<Int, NpcData>>() {}.type
            npcData.putAll(gson.fromJson(file.readText(), type))
        }
    }

    val disabled = false

    fun dumpData(npcId: Int, npcName: String, combatLevel: Int) {
        if (disabled)
            return
        if (npcData.containsKey(npcId)) {
            println("⚠ Data already exists for $npcName (ID=$npcId)")
            return
        }

        val npcVersions = fetchNpcCombatData(npcName) ?: run {
            println("❌ Failed to fetch combat data for $npcName")
            return
        }

        // Pick the NPC version with the closest combat level to the requested
        val npc = npcVersions.minByOrNull { kotlin.math.abs(it.combatLevel - combatLevel) }
        if (npc == null) {
            println("⚠ No combat level match found for $npcName")
            return
        }
        if (npc.combatLevel != combatLevel) {
            println("⚠ Combat level mismatch for $npcName. Local=$combatLevel Wiki=${npc.combatLevel}")
        }

        npcData[npcId] = npc.copy(id = npcId)
        file.writeText(gson.toJson(npcData.toSortedMap()))
        println("✅ Combat data saved for $npcName (ID=$npcId)")
    }

    fun hasData(npcId: Int): Boolean = npcData.containsKey(npcId)


    fun fetchNpcCombatData(name: String): List<NpcData>? {
        val encoded = URLEncoder.encode(name, StandardCharsets.UTF_8)
        val url = "https://oldschool.runescape.wiki/api.php?action=parse&prop=wikitext&format=json&redirects=1&page=$encoded"


        println("🔹 Fetching URL: $url")
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "AvalonBot/1.0 (contact: me@example.com)")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("❌ HTTP request failed with code ${response.code}")
                return null
            }

            val body = response.body?.string() ?: run {
                println("❌ Response body is null")
                return null
            }

            val json = try { Gson().fromJson(body, JsonObject::class.java) } catch (e: Exception) {
                println("❌ Failed to parse JSON: ${e.message}")
                return null
            }

            val wikitext = json["parse"]?.asJsonObject?.get("wikitext")?.asJsonObject?.get("*")?.asString
            if (wikitext == null) {
                println("❌ 'wikitext' not found in JSON")
                return null
            }

            // Return list of all versions
            return parseCombatDataFromInfobox(wikitext, name)
        }
    }


    private fun parseCombatDataFromInfobox(wikitext: String, name: String): List<NpcData> {
        val npcList = mutableListOf<NpcData>()

        // Match all Infobox Monster templates in the page
        val regex = "\\{\\{Infobox Monster(.*?)\\}\\}".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matches = regex.findAll(wikitext)

        for (match in matches) {
            val block = match.groupValues[1]
            val map = mutableMapOf<String, String>()
            block.lines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("|")) {
                    val parts = trimmed.substring(1).split("=", limit = 2)
                    if (parts.size == 2) map[parts[0].trim().lowercase()] = parts[1].trim()
                }
            }

            fun yesNo(key: String) = map[key.lowercase()]?.equals("yes", true) ?: false
            fun intVal(vararg keys: String, default: Int = 0): Int {
                for (key in keys) map[key.lowercase()]?.toIntOrNull()?.let { return it }
                return default
            }
            fun doubleVal(key: String) = map[key.lowercase()]?.toDoubleOrNull()

            val versionKeys = map.keys.filter { it.startsWith("version") }.toMutableList()
            if (versionKeys.isEmpty()) versionKeys.add("1") // default to version 1 if no version keys exist

            versionKeys.forEach { versionKey ->
                val versionNum = versionKey.removePrefix("version").toIntOrNull() ?: 1
                val ids = map["id$versionNum"]?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: listOf(-1)
                ids.forEach { npcId ->
                    npcList.add(
                        NpcData(
                            id = npcId,
                            name = name,
                            members = yesNo("members"),
                            combatLevel = intVal("combat$versionNum", "combat"),
                            attackLevel = intVal("att$versionNum", "att", "attack"),
                            strengthLevel = intVal("str$versionNum", "str", "strength"),
                            defenceLevel = intVal("def$versionNum", "def", "defence"),
                            magicLevel = intVal("mage", "magic"),
                            rangedLevel = intVal("range", "ranged"),
                            constitutionLevel = intVal("hitpoints$versionNum", "hitpoints", "hp"),
                            attackBonus = intVal("attbns", "attbonus"),
                            strengthBonus = intVal("strbns", "strbonus"),
                            magicBonus = intVal("mbns", "magicbns"),
                            magicStrengthBonus = intVal("amagic", "amagicbns"),
                            rangedBonus = intVal("arange", "rangebns"),
                            rangedStrengthBonus = intVal("rngbns", "rangedstrbns"),
                            meleeDefence = mapOf(
                                "stab" to intVal("dstab"),
                                "slash" to intVal("dslash"),
                                "crush" to intVal("dcrush")
                            ),
                            magicDefence = mapOf("magic" to intVal("dmagic")),
                            rangedDefence = mapOf(
                                "light" to 0,
                                "standard" to intVal("dstandard"),
                                "heavy" to 0
                            ),
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
                            maxHit = mapOf("maxhit" to intVal("max hit$versionNum", "max hit")),
                            aggressive = yesNo("aggressive"),
                            poisonous = null,
                            attackStyles = map["attack style"]?.split(",")?.map { it.trim() } ?: emptyList(),
                            attackSpeedTicks = intVal("attack speed").takeIf { it > 0 } ?: 4,
                            respawnTicks = intVal("respawn$versionNum", "respawn").takeIf { it > 0 } ?: 25,
                            slayerLevel = null,
                            slayerXp = intVal("slayxp$versionNum", "slayxp"),
                            categories = emptyList(),
                            assignedBy = map["assignedby"]?.split(",")?.map { it.trim() } ?: emptyList()
                        )
                    )
                }
            }
        }

        return npcList
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
