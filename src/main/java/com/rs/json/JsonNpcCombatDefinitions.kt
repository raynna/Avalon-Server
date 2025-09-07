package com.rs.json

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rs.java.utils.Logger
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory

object JsonNpcCombatDefinitions {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val defType = object : TypeToken<NpcCombatDefinition>() {}.type

    private val baseDir: Path = Paths.get(System.getProperty("user.dir"), "data/npcs/combat")

    /** Lookups */
    private val byName: MutableMap<String, NpcCombatDefinition> = mutableMapOf()
    private val byId: MutableMap<Int, NpcCombatDefinition> = mutableMapOf()

    fun init() {
        try {
            loadAll()
            Logger.log(
                "JsonNpcCombatDefinitions",
                "Loaded ${byName.size} name-keys and ${byId.size} id-keys combat definitions."
            )
        } catch (t: Throwable) {
            Logger.handle(t)
        }
    }

    private fun loadAll() {
        byName.clear()
        byId.clear()

        if (!baseDir.isDirectory()) return
        Files.walk(baseDir).use { paths ->
            paths.filter { it.toString().endsWith(".json") }
                .forEach { file ->
                    val def = readDefinition(file.toFile())
                    def?.let {
                        it.names.forEach { name -> byName[name.lowercase()] = it }
                        it.ids.forEach { id -> byId[id] = it }
                    }
                }
        }
    }

    private fun readDefinition(file: File): NpcCombatDefinition? {
        return try {
            file.reader().use { gson.fromJson<NpcCombatDefinition>(it, defType) }
        } catch (e: Exception) {
            Logger.handle(e)
            null
        }
    }

    fun getByName(name: String?): NpcCombatDefinition? =
        name?.lowercase()?.let { byName[it] }
    fun getById(id: Int): NpcCombatDefinition? = byId[id]
}
