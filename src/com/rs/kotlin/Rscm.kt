package com.rs.kotlin;

import com.rs.java.game.WorldTile
import java.nio.file.Path
import kotlin.io.path.*

object Rscm {

    sealed class RscmEntry {
        data class Id(val value: Int) : RscmEntry()
        data class Location(val x: Int, val y: Int, val plane: Int) : RscmEntry()
        data class Tile(val x: Int, val y: Int, val plane: Int) : RscmEntry()
    }

    private lateinit var mappings: Map<String, Map<String, RscmEntry>>

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

    private fun load(file: Path): Map<String, RscmEntry> {
        val locationRegex = Regex("""^(?<NAME>.+)=(?<X>\d+),(?<Y>\d+),(?<PLANE>\d+)$""")
        val idRegex = Regex("""^(?<NAME>.+)=(?<ID>\d+)$""")

        return file.readLines().mapNotNull { line ->
            when {
                locationRegex.matches(line) -> {
                    val match = locationRegex.find(line)!!
                    val name = match.groups["NAME"]!!.value
                    val x = match.groups["X"]!!.value.toInt()
                    val y = match.groups["Y"]!!.value.toInt()
                    val plane = match.groups["PLANE"]!!.value.toInt()
                    name to RscmEntry.Location(x, y, plane)
                    name to RscmEntry.Tile(x, y, plane)
                }
                idRegex.matches(line) -> {
                    val match = idRegex.find(line)!!
                    val name = match.groups["NAME"]!!.value
                    val id = match.groups["ID"]!!.value.toInt()
                    name to RscmEntry.Id(id)
                }
                else -> {
                    println("[Rscm] Ignored invalid line: $line")
                    null
                }
            }
        }.toMap()
    }

}