package com.rs.kotlin;

import com.rs.java.game.WorldTile
import java.nio.file.Path
import kotlin.io.path.*

object Rscm {

    fun item(name: String) = lookup("item.$name")
    fun graphic(name: String) = lookup("graphic.$name")
    fun animation(name: String) = lookup("animation.$name")
    fun sound(name: String) = lookup("sound.$name")

    sealed class RscmEntry {
        data class Id(val value: Int) : RscmEntry()
        data class Location(val x: Int, val y: Int, val plane: Int) : RscmEntry()
        data class Tile(val x: Int, val y: Int, val plane: Int) : RscmEntry()
        data class IdList(val values: List<Int>) : RscmEntry()
    }

    private lateinit var mappings: Map<String, Map<String, RscmEntry>>
    private lateinit var reverseMappings: Map<Int, String>

    @JvmStatic
    @OptIn(ExperimentalPathApi::class)
    fun loadAll() {
        require(!this::mappings.isInitialized) {
            "Mappings initialized."
        }
        val folder = Path("data", "rscm").walk().filter { it.extension == "rscm" }
        this.mappings =
            buildMap {
                for (file in folder) {
                    val results = load(file)
                    put(file.nameWithoutExtension, results)
                }
            }
        this.reverseMappings = buildReverseMappings()
    }

    private fun buildReverseMappings(): Map<Int, String> {
        val reverseMap = mutableMapOf<Int, String>()
        for ((type, entries) in mappings) {
            for ((name, entry) in entries) {
                when (entry) {
                    is RscmEntry.Id -> reverseMap[entry.value] = "$type.$name"
                    is RscmEntry.IdList -> entry.values.forEach { id ->
                        reverseMap[id] = "$type.$name"
                    }
                    else -> {} // ignore locations and tiles
                }
            }
        }
        return reverseMap
    }

    @JvmStatic
    fun lookupLocation(name: String): WorldTile {
        val (type, ref) = name.split('.').let {
            require(it.size == 2) { "Invalid format: '$name'. Use 'type.name'" }
            it
        }

        val entry = mappings[type]?.get(ref)
            ?: error("Mapping '$ref' not found in $type.")

        return when (entry) {
            is RscmEntry.Location -> WorldTile(entry.x, entry.y, entry.plane)
            is RscmEntry.Tile -> WorldTile(entry.x, entry.y, entry.plane)
            else -> error("Mapping '$ref' in $type is not a location.")
        }
    }

    @JvmStatic
    fun lookupList(name: String): List<Int> {
        val parts = name.split('.')
        if (parts.size != 2) {
            error("[Rscm] Invalid format: '$name'. Use 'type.name' (e.g. 'npc.goblin_lv2').")
        }
        val (type, ref) = parts
        val entry = mappings[type]?.get(ref)
            ?: error("Mapping '$ref' not found in $type mappings.")

        val result = when (entry) {
            is RscmEntry.IdList -> entry.values
            else -> error("Mapping '$ref' in $type is not an IdList.")
        }

        //println("[Rscm] lookupList '$name' returned IDs: $result")
        return result
    }

    @JvmStatic
    fun lookup(name: String): Int {
        val parts = name.split('.')
        if (parts.size != 2) {
            println("[Rscm] Invalid format: '$name'. Use 'type.name' (e.g. 'item.rune_platelegs').")
            return -1
        }

        val (type, ref) = parts
        val entry = mappings[type]?.get(ref)
            ?: error("Mapping '$ref' not found in $type mappings.")

        return when (entry) {
            is RscmEntry.Id -> entry.value
            else -> error("Mapping '$ref' in $type is not an ID.")
        }
    }

    @JvmStatic
    fun reverseLookup(id: Int): String? {
        return reverseMappings[id]
    }

    private fun load(file: Path): Map<String, RscmEntry> {
        val locationRegex = Regex("""^(?<NAME>.+)=(?<X>\d+),(?<Y>\d+),(?<PLANE>\d+)$""")
        val idRegex = Regex("""^(?<NAME>.+)=(?<ID>\d+)$""")
        val idListRegex = Regex("""^(?<NAME>.+)=\[(?<IDS>[\d,\s]+)]$""")

        return file.readLines().mapIndexedNotNull { index, line ->
            val trimmedLine = line.trim()

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                // silently skip blank lines and comments
                return@mapIndexedNotNull null
            }

            when {
                locationRegex.matches(trimmedLine) -> {
                    val match = locationRegex.find(trimmedLine)!!
                    val name = match.groups["NAME"]!!.value.trim()
                    val x = match.groups["X"]!!.value.toInt()
                    val y = match.groups["Y"]!!.value.toInt()
                    val plane = match.groups["PLANE"]!!.value.toInt()
                    name to RscmEntry.Location(x, y, plane)
                }
                idRegex.matches(trimmedLine) -> {
                    val match = idRegex.find(trimmedLine)!!
                    val name = match.groups["NAME"]!!.value.trim()
                    val id = match.groups["ID"]!!.value.toInt()
                    name to RscmEntry.Id(id)
                }
                idListRegex.matches(trimmedLine) -> {
                    val match = idListRegex.find(trimmedLine)!!
                    val name = match.groups["NAME"]!!.value.trim()
                    val idsString = match.groups["IDS"]!!.value
                    val ids = idsString.split(",").map { it.trim().toInt() }
                    name to RscmEntry.IdList(ids)
                }
                else -> {
                    println("[Rscm] Ignored invalid line #${index + 1}: \"$line\"")
                    null
                }
            }
        }.toMap()
    }
}