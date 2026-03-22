package raynna.game.npc

import raynna.core.cache.defintions.NPCDefinitions

class KillCountContainer {
    private val kills: MutableMap<String, Int> = HashMap()

    fun increment(npcId: Int) {
        val name = NPCDefinitions.getNPCDefinitions(npcId)?.name ?: return
        increment(name)
    }

    fun increment(name: String) {
        val normalized = normalize(name)

        add(normalized)

        if (normalized.startsWith("revenant_")) {
            add("revenants")
        }
        if (normalized.contains("demon")) {
            add("demons")
        }
        if (normalized.contains("dragon")) {
            add("dragons")
        }
    }

    fun get(npcId: Int): Int {
        val name = NPCDefinitions.getNPCDefinitions(npcId)?.name ?: return 0
        return getByName(name)
    }

    fun getByName(name: String): Int {
        val key = normalize(name)
        return kills[key] ?: 0
    }

    fun all(): Map<String, Int> = kills

    private fun add(key: String) {
        kills[key] = (kills[key] ?: 0) + 1
    }

    fun normalize(name: String): String =
        name
            .lowercase()
            .replace("[^a-z0-9]+".toRegex(), "_")
            .trim('_')

    fun clear() {
        kills.clear()
    }

    /** Remove a single killcount entry by npc name */
    fun reset(name: String) {
        kills.remove(normalize(name))
    }

    /** Remove a single killcount entry by npc id */
    fun reset(npcId: Int) {
        val name = NPCDefinitions.getNPCDefinitions(npcId)?.name ?: return
        reset(name)
    }
}
