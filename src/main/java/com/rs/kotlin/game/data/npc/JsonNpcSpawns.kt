package com.rs.kotlin.game.data.npc

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.utils.Logger
import com.rs.java.utils.Utils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.IdentityHashMap
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

object JsonNpcSpawns {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val listType = object : TypeToken<List<NpcSpawnEntry>>() {}.type

    private val areasDir: Path = Paths.get(System.getProperty("user.dir"), "data/npcs/spawns/areas")
    private val unorganizedFile: Path = Paths.get(System.getProperty("user.dir"), "data/npcs/spawns/main.json")

    /** memory index of which file each spawn came from */
    private val sourceIndex: MutableMap<NpcSpawnEntry, File> = IdentityHashMap()

    fun init() {
        try {
            val all = readAllSpawns()
            validate(all)
            indexSpawns(all)
            Logger.log("JsonNpcSpawns", "Indexed ${all.size} NPC spawns into ${spawnsByRegion.size} regions.")
        } catch (t: Throwable) {
            Logger.handle(t)
        }
    }

    private val spawnsByRegion: MutableMap<Int, MutableList<NpcSpawnEntry>> = mutableMapOf()

    private fun indexSpawns(spawns: List<NpcSpawnEntry>) {
        for (e in spawns) {
            val regionId = WorldTile(e.tile.x, e.tile.y, e.tile.plane).regionId
            spawnsByRegion.computeIfAbsent(regionId) { mutableListOf() }.add(e)
        }
    }

    fun loadRegion(regionId: Int) {
        val entries = spawnsByRegion[regionId] ?: return
        for (e in entries) {
            val hash = e.mapAreaName?.let { Utils.getNameHash(it) } ?: -1
            val canOut = e.canBeAttackedFromOutside ?: true
            World.spawnNPC(
                e.npcId,
                WorldTile(e.tile.x, e.tile.y, e.tile.plane),
                hash,
                canOut
            )
        }
        Logger.log("JsonNpcSpawns", "Spawned ${entries.size} NPCs in region $regionId")
    }

    private fun readAllSpawns(): List<NpcSpawnEntry> {
        val out = mutableListOf<NpcSpawnEntry>()

        if (areasDir.isDirectory()) {
            Files.newDirectoryStream(areasDir, "*.json").use { ds ->
                for (p in ds) out += readOneFile(p.toFile())
            }
        }

        if (unorganizedFile.exists()) out += readOneFile(unorganizedFile.toFile())

        return out
    }

    private fun readOneFile(file: File): List<NpcSpawnEntry> {
        return try {
            file.reader().use { reader ->
                val list: List<NpcSpawnEntry?> = gson.fromJson(reader, listType) ?: emptyList()

                val result = mutableListOf<NpcSpawnEntry>()
                list.forEachIndexed { i, e ->
                    if (e == null) {
                        Logger.log("JsonNpcSpawns", "Null spawn in file ${file.name} at local index $i")
                    } else {
                        sourceIndex[e] = file
                        result.add(e)
                    }
                }
                result
            }
        } catch (e: Exception) {
            Logger.handle(e)
            Logger.log("JsonNpcSpawns", "Failed to parse spawns from ${file.absolutePath}")
            emptyList()
        }
    }


    private fun validate(all: List<NpcSpawnEntry>) {
        all.forEachIndexed { i, e ->
            if (e == null) {
                Logger.log("JsonNpcSpawns", "Null entry at index $i")
                throw IllegalArgumentException("Null NPC spawn at index $i (check JSON formatting)")
            }

            if (e.npcId < 0) {
                Logger.log("JsonNpcSpawns", "Invalid npcId at index $i: $e")
                throw IllegalArgumentException("Invalid npcId at index $i")
            }

            if (e.tile == null) {
                Logger.log("JsonNpcSpawns", "Missing tile at index $i: $e")
                throw IllegalArgumentException("Missing tile at index $i")
            }
        }
    }


    fun removeSpawn(spawn: NpcSpawnEntry): Boolean {
        val src = sourceIndex[spawn] ?: return false
        val current = readOneFile(src).toMutableList()
        val removed = current.removeIf {
            it.npcId == spawn.npcId &&
                    it.tile.x == spawn.tile.x &&
                    it.tile.y == spawn.tile.y &&
                    it.tile.plane == spawn.tile.plane
        }
        if (!removed) return false

        src.writer().use { gson.toJson(current, listType, it) }
        return true
    }
}
