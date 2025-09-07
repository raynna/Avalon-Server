package com.rs.kotlin.game.npc.combatdata

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

object CombatDataParser {

    private var npcDataMap: Map<Int, JsonObject>

    init {
        val file = File("data/npcs/npc_data.json")
        if (!file.exists()) {
            throw IllegalStateException("NPC data file not found at: ${file.absolutePath}")
        }

        val fileContent = file.readText()
        val gson = Gson()
        val json = gson.fromJson(fileContent, JsonObject::class.java)

        npcDataMap = json.entrySet().associate { it.key.toInt() to it.value.asJsonObject }
    }

    @JvmStatic
    fun reload() {
        val file = File("data/npcs/npc_data.json")
        if (file.exists()) {
            val gson = Gson()
            val json = gson.fromJson(file.readText(), JsonObject::class.java)
            npcDataMap = json.entrySet().associate { it.key.toInt() to it.value.asJsonObject }
        }
    }

    @JvmStatic
    fun getData(npcId: Int): CombatData? {
        val json = npcDataMap[npcId] ?: return null

        val melee = json["meleeDefence"].asJsonObject
        val ranged = json["rangedDefence"].asJsonObject
        val magic = json["magicDefence"].asJsonObject
        val immunities = json["immunities"].asJsonObject
        val maxHit = json["maxHit"].asJsonObject

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
            meleeDefence = MeleeDefence(
                stab = melee["stab"].asInt,
                slash = melee["slash"].asInt,
                crush = melee["crush"].asInt
            ),
            magicDefence = MagicDefence(
                magic = magic["magic"].asInt
            ),
            rangedDefence = RangedDefence(
                light = ranged["light"]?.asInt ?: ranged["standard"].asInt,
                standard = ranged["standard"].asInt,
                heavy = ranged["heavy"]?.asInt ?: ranged["standard"].asInt
            ),
            immunities = Immunities(
                poison = immunities["poison"].asBoolean,
                venom = immunities["venom"].asBoolean,
                cannons = immunities["cannons"].asBoolean,
                thralls = immunities["thralls"].asBoolean,
                burn = immunities["burn"].asBoolean
            ),
            maxHit = MaxHit(maxHit["maxhit"].asInt),
            aggressive = json["aggressive"].asBoolean,
            attackStyles = json["attackStyles"].asJsonArray.map { it.asString },
            attackSpeedTicks = json["attackSpeedTicks"].asInt,
            respawnTicks = json["respawnTicks"].asInt,
            slayerXp = json["slayerXp"].asInt
        )
    }
}
