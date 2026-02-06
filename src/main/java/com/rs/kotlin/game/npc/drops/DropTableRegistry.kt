package com.rs.kotlin.game.npc.drops

import com.rs.core.cache.defintions.NPCDefinitions
import com.rs.kotlin.game.npc.MonsterCategory

object DropTableRegistry {

    private val npcDropTables = mutableMapOf<Int, DropTable>()
    private val namedDropTables = mutableMapOf<String, DropTable>()
    private val itemDropTables = mutableMapOf<Int, DropTable>()

    private val objectDropTables = mutableMapOf<Int, DropTable>()
    private val objectAliases = mutableMapOf<Int, String>()

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

    @JvmStatic
    fun registerNamedTable(name: String, table: DropTable) {
        namedDropTables[name.lowercase()] = table
    }
    @JvmStatic
    fun registerItemTable(itemId: Int, table: DropTable) {
        itemDropTables[itemId] = table
    }
    @JvmStatic
    fun registerObjectTable(objectId: Int, table: DropTable) {
        objectDropTables[objectId] = table
    }
    @JvmStatic
    fun registerObjectTable(name: String, objectId: Int, table: DropTable) {
        objectDropTables[objectId] = table
        objectAliases[objectId] = name
    }

    @JvmStatic
    fun isTrackable(npcId: Int): Boolean {
        return npcDropTables.containsKey(npcId)
    }

    @JvmStatic
    fun getCategory(npcId: Int): MonsterCategory {
        return npcDropTables[npcId]?.category ?: MonsterCategory.REGULAR
    }


    /**
     * Get DropTable by NPC ID.
     */
    @JvmStatic
    fun getDropTableForNpc(id: Int): DropTable? {
        return npcDropTables[id]
    }


    @JvmStatic
    fun getNamedDropTable(name: String): DropTable? =
        namedDropTables[name.lowercase()]

    @JvmStatic
    fun getAllNamedTables(): Map<String, DropTable> =
        namedDropTables

    fun getItemDropTable(itemId: Int): DropTable? =
        itemDropTables[itemId]

    fun getObjectDropTable(objectId: Int): DropTable? =
        objectDropTables[objectId]

    fun getObjectAlias(objectId: Int): String? =
        objectAliases[objectId]

    fun getAllItemTables(): Map<Int, DropTable> = itemDropTables
    fun getAllObjectTables(): Map<Int, DropTable> = objectDropTables

    fun getAllTables(): Collection<DropTable> {
        return buildList {
            addAll(npcDropTables.values)
            addAll(namedDropTables.values)
            addAll(itemDropTables.values)
            addAll(objectDropTables.values)
        }
    }

    @JvmStatic
    fun getSourceForNpc(npcId: Int): DropTableSource? {
        return if (npcDropTables.containsKey(npcId))
            DropTableSource.Npc(npcId)
        else
            null
    }


    @JvmStatic
    fun getSourceForItem(itemId: Int): DropTableSource? {
        return if (itemDropTables.containsKey(itemId))
            DropTableSource.Item(itemId)
        else
            null
    }

    @JvmStatic
    fun getSourceForObject(objectId: Int): DropTableSource? {
        return if (objectDropTables.containsKey(objectId))
            DropTableSource.Object(objectId)
        else
            null
    }


    fun getTableForSource(source: DropTableSource): DropTable? =
        when (source) {
            is DropTableSource.Npc -> npcDropTables[source.id]
            is DropTableSource.Named -> namedDropTables[source.key.lowercase()]
            is DropTableSource.Item -> itemDropTables[source.id]
            is DropTableSource.Object -> objectDropTables[source.id]
        }

    fun getAllSources(): List<DropTableSource> {

        val list = mutableListOf<DropTableSource>()

        npcDropTables.keys.forEach {
            list += DropTableSource.Npc(it)
        }

        namedDropTables.keys.forEach {
            list += DropTableSource.Named(it)
        }

        itemDropTables.keys.forEach {
            list += DropTableSource.Item(it)
        }

        objectDropTables.keys.forEach {
            list += DropTableSource.Object(it)
        }

        return list
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
