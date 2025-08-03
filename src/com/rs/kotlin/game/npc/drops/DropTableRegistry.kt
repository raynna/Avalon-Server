package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.NPCDefinitions

object DropTableRegistry {
    private val npcDropTables: MutableMap<String, DropTable> = HashMap()

    @JvmStatic
    fun registerDropTable(table: DropTable, vararg npcKeys: String) {
        for (npcKey in npcKeys) {
            npcDropTables[npcKey] = table
        }
    }

    fun registerDropTable(table: DropTable, vararg npcIds: Int) {
        npcIds.forEach { id ->
            val key = npcKeyFromId(id)
            npcDropTables[key] = table
        }
    }

    @JvmStatic
    fun getDropTableForNpcKey(npcKey: String): DropTable? {
        return npcDropTables[npcKey]
    }

    fun npcKeyFromId(npcId: Int): String {
        return try {
            val name = NPCDefinitions.getNPCDefinitions(npcId).name.lowercase().replace(" ", "_")
            "npc.$name"
        } catch (e: Exception) {
            "npc.null"
        }
    }

    @JvmStatic
    fun logDropTableSizes() {
        val grouped = npcDropTables.entries.groupBy { it.value }
        for ((table, entries) in grouped) {
            val npcInfos = entries.map { (npcKey, _) ->
                npcKey // now npcKey is already a string like "npc.black_dragon"
            }.joinToString(", ")

            val count = table.totalDropCount()
            println("[DropSystem] $npcInfos share $count drop entries.")
        }
    }

}
