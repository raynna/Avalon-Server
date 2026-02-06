package com.rs.kotlin.game.player.interfaces

import com.rs.core.cache.defintions.NPCDefinitions
import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.kotlin.game.npc.drops.DropTableRegistry
import com.rs.kotlin.game.npc.drops.DropTableRegistry.getDropTableForNpc
import com.rs.kotlin.game.npc.drops.DropTableSource

object DropSearch {

    fun findSourcesByName(query: String): MutableList<DropTableSource> {

        val results = mutableListOf<DropTableSource>()
        val seenNames = HashSet<String>()

        // ---------- NPC TABLES ----------
        for ((npcId, def) in NPCDefinitions.getNpcDefinitions()) {

            val name = def.name ?: continue

            if (!name.contains(query, true))
                continue

            val table = getDropTableForNpc(npcId) ?: continue

            if (table.allDrops().isEmpty())
                continue

            if (seenNames.add(name)) {
                results.add(DropTableSource.Npc(npcId))
            }
        }

        // ---------- NAMED TABLES ----------
        for ((key, table) in DropTableRegistry.getAllNamedTables()) {

            if (!key.contains(query, true))
                continue

            if (table.allDrops().isEmpty())
                continue

            if (seenNames.add("named:$key")) {
                results.add(DropTableSource.Named(key))
            }
        }

        return results
    }


    fun findSourcesByDrop(itemName: String): MutableList<DropTableSource> {

        val results = mutableListOf<DropTableSource>()
        val seen = HashSet<String>()

        // ---------- NPC TABLES ----------
        for ((npcId, _) in NPCDefinitions.getNpcDefinitions()) {

            val table = getDropTableForNpc(npcId) ?: continue
            if (table.allDrops().isEmpty()) continue

            for (drop in table.allDrops()) {

                val def = ItemDefinitions.getItemDefinitions(drop.itemId)

                if (def.name.contains(itemName, true)) {

                    val npcName =
                        NPCDefinitions.getNPCDefinitions(npcId).name

                    if (seen.add(npcName))
                        results.add(DropTableSource.Npc(npcId))

                    break
                }
            }
        }

        // ---------- NAMED TABLES ----------
        for ((key, table) in DropTableRegistry.getAllNamedTables()) {

            if (table.allDrops().isEmpty()) continue

            for (drop in table.allDrops()) {

                val def = ItemDefinitions.getItemDefinitions(drop.itemId)

                if (def.name.contains(itemName, true)) {

                    if (seen.add("named:$key"))
                        results.add(DropTableSource.Named(key))

                    break
                }
            }
        }

        return results
    }


}
