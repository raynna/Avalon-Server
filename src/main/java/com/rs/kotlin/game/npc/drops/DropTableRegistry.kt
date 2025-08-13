package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.NPCDefinitions

object DropTableRegistry {

    private val npcDropTables = mutableMapOf<Int, DropTable>()

    @JvmStatic
    fun registerDropTable(table: DropTable, vararg npcIds: Int) {
        val mappedIds = mutableListOf<Int>()
        for (npcId in npcIds) {
            npcDropTables[npcId] = table
            mappedIds += npcId
        }
    }

    @JvmStatic
    fun registerDropTable(table: DropTable, vararg npcKeyGroups: List<Int>) {
        val mappedIds = mutableListOf<Int>()
        for (group in npcKeyGroups) {
            for (npcId in group) {
                npcDropTables[npcId] = table
                mappedIds += npcId
            }
        }
    }

    /**
     * Get DropTable by NPC ID.
     */
    @JvmStatic
    fun getDropTableForNpc(id: Int): DropTable? {
        return npcDropTables[id]
    }

    /**
     * Converts NPC ID to RSCM-style key for debug/display purposes.
     */
    @JvmStatic
    fun npcKeyFromId(npcId: Int): String {
        return try {
            val name = NPCDefinitions.getNPCDefinitions(npcId)
                ?.name
                ?.lowercase()
                ?.replace("[^a-z0-9]+".toRegex(), "_")
                ?.trim('_') ?: "null"
            "npc.$name"
        } catch (e: Exception) {
            "npc.null"
        }
    }

    /**
     * Logs a summary of how many NPCs are linked to each drop table.
     */
    @JvmStatic
    fun logDropTableSizes() {
        val grouped = npcDropTables.entries.groupBy { it.value }
        for ((table, entries) in grouped) {
            val npcIds = entries.joinToString(", ") { it.key.toString() }
            val dropCount = table.totalDropCount()
            println("[DropTables] Table: ${table.nameOrClass()}, Drops: ${dropCount}, npc entries: [${npcIds}]")
            //println("[DropSystem] Npcs [$npcIds] share $dropCount drop entries in table ${table.nameOrClass()}.")
        }
    }

    /**
     * Extension to get a friendly name for DropTable (you need to add 'name' to DropTable)
     */

    private fun DropTable.nameOrClass(): String = this.toString()
}
