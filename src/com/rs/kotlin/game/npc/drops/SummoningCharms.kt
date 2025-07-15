package com.rs.kotlin.game.npc.drops

/**
 * Represents a summoning charm drop with a percentage chance.
 * @param type The type of charm (e.g., CRIMSON)
 * @param amount The number of charms dropped
 * @param percent Chance to drop this charm (e.g., 13.5 = 13.5%)
 */
data class SummoningCharms(val type: CharmType, val amount: Int, val percent: Double) {
    enum class CharmType(val itemId: Int) {
        GOLD(12158),
        GREEN(12159),
        CRIMSON(12160),
        BLUE(12163),
        ABYSSAL(12161),

        NONE(-1)
    }
}
