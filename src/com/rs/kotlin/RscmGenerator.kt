package com.rs.kotlin

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.core.cache.defintions.NPCDefinitions
import kotlin.io.path.Path
import kotlin.io.path.writeLines
import kotlin.io.path.createDirectories

object RscmGenerator {


    fun generateGroupedNpcRscm() {
        NPCDefinitions.loadAll()
        val definitions = NPCDefinitions.getNpcDefinitions().values.filterNotNull()
        val outputPath = Path("data/rscm/npc_group.rscm")

        // Map (baseName, combatLevel) -> MutableList of npcIds
        val grouped = mutableMapOf<Pair<String, Int>, MutableList<Int>>()

        fun toBaseName(name: String): String =
            name.lowercase().replace("[^a-z0-9]+".toRegex(), "_").trim('_')

        for (def in definitions) {
            if (def.name.isBlank()) continue
            val baseName = toBaseName(def.name)
            val key = baseName to def.combatLevel
            grouped.getOrPut(key) { mutableListOf() }.add(def.id)
        }

        val entries = mutableListOf<String>()

        for ((key, npcIds) in grouped.toSortedMap(compareBy({ it.first }, { it.second }))) {
            val (base, level) = key
            val groupName = if (level > 0) "${base}_lv$level" else base
            val comment = if (level > 0) "# Combat Lvl. $level" else "# Friendly NPCs"
            // Format list as [id1, id2, id3]
            val idsString = npcIds.joinToString(", ", "[", "]")
            //entries += comment
            entries += "$groupName=$idsString"
        }

        outputPath.parent.createDirectories()
        outputPath.writeLines(entries)
        println("Generated grouped NPC mappings to $outputPath")
    }



    fun generateNpcRscm() {
        NPCDefinitions.loadAll()
        val definitions = NPCDefinitions.getNpcDefinitions()
        val outputPath = Path("data/rscm/npc.rscm")

        val entries = mutableListOf<String>()
        val usedNames = mutableSetOf<String>()
        val addedIds = mutableSetOf<Int>()

        fun toBaseName(name: String): String =
            name.lowercase().replace("[^a-z0-9]+".toRegex(), "_").trim('_')

        fun uniqueName(base: String, combatLevel: Int, npcId: Int): String {
            var name = if (combatLevel > 0) "${base}_lv$combatLevel" else base
            if (usedNames.add(name)) return name // name wasn't used yet
            name = "${name}_$npcId"
            usedNames.add(name)
            return name
        }

        for (def in definitions.values) {
            if (def == null || def.name.isNullOrBlank()) continue
            if (def.id in addedIds) continue

            val base = toBaseName(def.name)
            val unique = uniqueName(base, def.combatLevel, def.id)

            val comment = if (def.combatLevel == 0) "# Friendly" else "# Combat Lvl. ${def.combatLevel}"
            entries += comment
            entries += "$unique=${def.id}"
            addedIds += def.id
        }

        outputPath.parent.createDirectories()
        outputPath.writeLines(entries)
        println("Generated ${entries.size} npc mappings to $outputPath")
    }



    fun generateItemRscm() {
        ItemDefinitions.loadAll()
        val definitions = ItemDefinitions.getItemDefinitions()
        val outputPath = Path("data/rscm/item.rscm")

        val entries = mutableListOf<String>()
        val nameCounts = mutableMapOf<String, Int>()
        val addedItemIds = mutableSetOf<Int>()

        fun toBaseName(name: String): String =
            name.lowercase().replace("[^a-z0-9]+".toRegex(), "_").trim('_')

        fun uniqueName(base: String): String {
            val count = nameCounts.getOrDefault(base, 0)
            nameCounts[base] = count + 1
            return if (count == 0) base else "${base}_${count + 1}"
        }

        val notedByUnnoted = definitions
            .filter { it != null && it.loaded && it.isNoted && it.certId != -1 }
            .groupBy { it.certId }

        for (def in definitions) {
            if (def == null || !def.loaded || def.name.isNullOrBlank()) continue
            if (def.id in addedItemIds) continue
            if (def.isNoted) continue

            val baseName = toBaseName(def.name)
            val uniqueBase = uniqueName(baseName)
            entries += "$uniqueBase=${def.id}"
            addedItemIds += def.id

            val notedItems = notedByUnnoted[def.id] ?: emptyList()
            for (noted in notedItems) {
                if (noted.id in addedItemIds) continue
                val notedName = uniqueName("${baseName}_noted")
                entries += "$notedName=${noted.id}"
                addedItemIds += noted.id
            }
        }

        outputPath.parent.createDirectories()
        outputPath.writeLines(entries)
        println("Generated ${entries.size} item mappings to $outputPath")
    }
}
