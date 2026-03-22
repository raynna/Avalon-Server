package raynna.game.npc.combatdata

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object CombatDataParser {
    private val gson = Gson()

    private val dataDir = File("data/npcs/wikidata/")
    private val npcDataMap: MutableMap<Int, CombatData> = ConcurrentHashMap()

    init {
        loadAll()
    }

    @JvmStatic
    fun reload() {
        loadAll()
    }

    private fun loadAll() {
        npcDataMap.clear()

        if (!dataDir.exists()) {
            throw IllegalStateException("NPC data folder not found at: ${dataDir.absolutePath}")
        }

        dataDir.listFiles()?.forEach { file ->
            if (!file.name.endsWith(".json")) return@forEach
            if (file.name == "failed.json") return@forEach

            try {
                val root = gson.fromJson(file.readText(), JsonObject::class.java)

                val name = root["name"]?.asString ?: "unknown"
                val variants = root["variants"]?.asJsonArray ?: return@forEach

                variants.forEach { variantElement ->
                    val variant = variantElement.asJsonObject
                    val ids = variant["ids"].asJsonArray.map { it.asInt }

                    val combatData = parseVariant(variant)

                    ids.forEach { id ->
                        npcDataMap[id] = combatData
                    }
                }
            } catch (e: Exception) {
                println("⚠ Failed to parse ${file.name}: ${e.message}")
            }
        }

        println("✅ Loaded ${npcDataMap.size} NPC combat entries")
    }

    @JvmStatic
    fun getData(npcId: Int): CombatData? = npcDataMap[npcId]

    private fun parseVariant(json: JsonObject): CombatData {
        val melee = json["meleeDefence"].asJsonObject
        val ranged = json["rangedDefence"].asJsonObject
        val magic = json["magicDefence"].asJsonObject
        val immunities = json["immunities"].asJsonObject
        val weaknesses = json["weaknesses"]?.asJsonObject

        val maxHitObj = json["maxHit"].asJsonObject

        val maxHit =
            MaxHit(
                melee = maxHitObj["melee"]?.asInt ?: 0,
                magic = maxHitObj["magic"]?.asInt ?: 0,
                ranged = maxHitObj["ranged"]?.asInt ?: 0,
                default = maxHitObj["default"]?.asInt ?: 0,
            )

        val weaknessData =
            Weaknesses(
                elemental =
                    weaknesses?.entrySet()?.associate {
                        it.key to it.value.asInt
                    } ?: emptyMap(),
            )

        return CombatData(
            combatLevel = json["combatLevel"].asInt,
            attackLevel = json["attackLevel"].asInt,
            strengthLevel = json["strengthLevel"].asInt,
            defenceLevel = json["defenceLevel"].asInt,
            magicLevel = json["magicLevel"].asInt,
            rangedLevel = json["rangedLevel"].asInt,
            constitutionLevel = json["constitutionLevel"].asInt,
            attackBonus = json["attackBonus"].asInt,
            strengthBonus = json["strengthBonus"].asInt,
            magicBonus = json["magicBonus"].asInt,
            magicStrengthBonus = json["magicStrengthBonus"].asInt,
            rangedBonus = json["rangedBonus"].asInt,
            rangedStrengthBonus = json["rangedStrengthBonus"].asInt,
            meleeDefence =
                MeleeDefence(
                    stab = melee["stab"].asInt,
                    slash = melee["slash"].asInt,
                    crush = melee["crush"].asInt,
                ),
            magicDefence =
                MagicDefence(
                    magic = magic["magic"].asInt,
                ),
            rangedDefence =
                RangedDefence(
                    light = ranged["light"]?.asInt ?: ranged["standard"].asInt,
                    standard = ranged["standard"].asInt,
                    heavy = ranged["heavy"]?.asInt ?: ranged["standard"].asInt,
                ),
            immunities =
                Immunities(
                    poison = immunities["poison"].asBoolean,
                    venom = immunities["venom"].asBoolean,
                    cannons = immunities["cannons"].asBoolean,
                    thralls = immunities["thralls"].asBoolean,
                    burn = immunities["burn"].asBoolean,
                ),
            weaknesses = weaknessData,
            maxHit = maxHit,
            aggressive = json["aggressive"].asBoolean,
            attackStyles = json["attackStyles"].asJsonArray.map { it.asString },
            attackSpeedTicks = json["attackSpeedTicks"].asInt,
            respawnTicks = json["respawnTicks"].asInt,
            slayerXp = json["slayerXp"]?.asInt ?: 0,
        )
    }
}
