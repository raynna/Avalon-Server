package com.rs.kotlin

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.core.cache.defintions.NPCDefinitions
import kotlin.io.path.Path
import kotlin.io.path.writeLines
import kotlin.io.path.createDirectories

object RscmGenerator {

    fun generateNpcRscm() {
        NPCDefinitions.loadAll()
        val definitions = NPCDefinitions.getNpcDefinitions()
        val outputPath = Path("data/rscm/npc.rscm")

        val entries = mutableListOf<String>()
        val nameCounts = mutableSetOf<String>()  // Track which base names have appeared
        val addedIds = mutableSetOf<Int>()

        fun toBaseName(name: String) =
            name.lowercase().replace("[^a-z0-9]+".toRegex(), "_").trim('_')

        fun uniqueName(base: String, npcId: Int): String {
            return if (!nameCounts.contains(base)) {
                nameCounts.add(base)
                base
            } else {
                // Duplicate: use npcId as suffix
                "${base}_$npcId"
            }
        }

        for (def in definitions.values) {
            if (def == null || def.name.isNullOrBlank()) continue
            if (def.id in addedIds) continue

            val baseName = toBaseName(def.name)
            val uniqueBase = uniqueName(baseName, def.id)
            entries += "$uniqueBase=${def.id}"
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
