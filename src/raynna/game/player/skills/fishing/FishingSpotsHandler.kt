package raynna.game.player.skills.fishing

import raynna.game.WorldTile
import raynna.game.npc.NPC

/**
 * Manages fishing spot NPC roaming pairs.
 *
 * Each entry is a bidirectional pair — when a spot moves it swaps between
 * the two tiles, so no spot is ever "stuck" at one end permanently.
 */
object FishingSpotsHandler {
    /**
     * Roaming pairs defined as (tileA, tileB).
     * When the NPC is at tileA it moves to tileB, and vice versa.
     */
    private val PAIRS: List<Pair<WorldTile, WorldTile>> =
        listOf(
            WorldTile(2836, 3431, 0) to WorldTile(2845, 3429, 0),
            WorldTile(2853, 3423, 0) to WorldTile(2860, 3426, 0),
            WorldTile(3110, 3432, 0) to WorldTile(3104, 3423, 0),
            WorldTile(3104, 3424, 0) to WorldTile(3110, 3433, 0),
            WorldTile(3632, 5082, 0) to WorldTile(3621, 5087, 0),
            WorldTile(3625, 5083, 0) to WorldTile(3617, 5087, 0),
            WorldTile(3621, 5119, 0) to WorldTile(3617, 5123, 0),
            WorldTile(3628, 5136, 0) to WorldTile(3633, 5137, 0),
            WorldTile(3637, 5139, 0) to WorldTile(3634, 5148, 0),
            WorldTile(3652, 5141, 0) to WorldTile(3658, 5145, 0),
            WorldTile(3680, 5110, 0) to WorldTile(3675, 5114, 0),
        )

    /**
     * Flat map: tileHash → destination tileHash, built bidirectionally so
     * both ends of each pair resolve without a reverse-lookup loop.
     */
    private val destinations: Map<Int, WorldTile> by lazy {
        buildMap {
            for ((a, b) in PAIRS) {
                put(a.tileHash, b)
                put(b.tileHash, a)
            }
        }
    }

    /**
     * Moves [npc] to its paired tile if one is registered.
     * @return true if the spot was moved, false if no pair exists for this NPC's tile.
     */
    fun moveSpot(npc: NPC): Boolean {
        val destination = destinations[npc.tileHash] ?: return false
        npc.nextWorldTile = destination
        return true
    }
}
