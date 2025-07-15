package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.NPCDefinitions

object DropTableRegistry {
    private val npcDropTables: MutableMap<Int, DropTable> = HashMap()

    @JvmStatic
    fun registerDropTable(table: DropTable, vararg npcIds: Int) {
        for (npcId in npcIds) {
            npcDropTables[npcId] = table
        }
    }

    @JvmStatic
    fun getDropTableForNpc(npcId: Int): DropTable? {
        return npcDropTables[npcId]
    }

    @JvmStatic
    fun logDropTableSizes() {

        // Group NPCs by shared DropTable instance
        val grouped = npcDropTables.entries.groupBy { it.value }

        for ((table, entries) in grouped) {
            val npcInfos = entries.map { (npcId, _) ->
                val name = try {
                    NPCDefinitions.getNPCDefinitions(npcId).name
                } catch (e: Exception) {
                    "Unknown"
                }
                "$npcId [$name]"
            }.joinToString(", ")

            val count = table.totalDropCount()
            println("[DropSystem] $npcInfos share $count drop entries.")
        }

    }

}
