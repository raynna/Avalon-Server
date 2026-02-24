package com.rs.kotlin.game.npc

import com.rs.core.cache.defintions.NPCDefinitions

class KillCountContainer {
    private val kills: MutableMap<String, Int> = HashMap()

    fun increment(npcId: Int) {
        val name = NPCDefinitions.getNPCDefinitions(npcId)?.name ?: return
        val key = normalize(name)

        kills[key] = (kills[key] ?: 0) + 1
    }

    fun increment(name: String) {
        val key = normalize(name)
        kills[key] = (kills[key] ?: 0) + 1
    }

    fun get(npcId: Int): Int {
        val name = NPCDefinitions.getNPCDefinitions(npcId)?.name ?: return 0
        val key = normalize(name)

        return kills[key] ?: 0
    }

    fun getByName(name: String): Int {
        val key = normalize(name)
        return kills[key] ?: 0
    }

    fun all(): Map<String, Int> = kills

    fun normalize(name: String): String =
        name
            .lowercase()
            .replace("[^a-z0-9]+".toRegex(), "_")
            .trim('_')

    fun clear() {
        kills.clear()
    }

    /** Remove a single killcount entry by npc name (raw, will be normalized). */
    fun reset(name: String) {
        kills.remove(normalize(name))
    }

    /** Remove a single killcount entry by npc id. */
    fun reset(npcId: Int) {
        val name = NPCDefinitions.getNPCDefinitions(npcId)?.name ?: return
        kills.remove(normalize(name))
    }
}
