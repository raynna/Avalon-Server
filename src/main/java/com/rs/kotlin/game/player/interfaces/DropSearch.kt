package com.rs.kotlin.game.player.interfaces

import com.rs.core.cache.defintions.NPCDefinitions
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.kotlin.game.npc.drops.DropTableRegistry.getDropTableForNpc

object DropSearch {

    fun findNpcsByName(query: String): MutableList<Int> {

        val results = mutableListOf<Int>()
        val seenNames = HashSet<String>()

        for ((npcId, def) in NPCDefinitions.getNpcDefinitions()) {

            val name = def.name ?: continue

            if (!name.contains(query, true))
                continue

            val table = getDropTableForNpc(npcId) ?: continue

            if (table.allDrops().isEmpty())
                continue

            if (seenNames.add(name)) {
                results.add(npcId)
            }
        }

        return results
    }


    fun findNpcsByDrop(itemName: String): MutableList<Int> {

        val results = mutableListOf<Int>()
        val seen = HashSet<String>()

        for ((npcId, _) in NPCDefinitions.getNpcDefinitions()) {

            val table = getDropTableForNpc(npcId) ?: continue

            if (table.allDrops().isEmpty())
                continue

            for (drop in table.allDrops()) {

                val def = ItemDefinitions.getItemDefinitions(drop.itemId)

                if (def.name.contains(itemName, true)) {

                    val npcName =
                        NPCDefinitions.getNPCDefinitions(npcId).name

                    if (seen.add(npcName))
                        results.add(npcId)

                    break
                }
            }
        }

        return results
    }

}
