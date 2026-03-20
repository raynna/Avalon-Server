package com.rs.kotlin.game.player.skills.fishing

import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills

/**
 * All fishing tools ordered by priority (highest priority = best tool to use).
 * Unlike axes, fishing tools are not strictly "better" across the board — they
 * are tied to specific spot types — but priority is still used by [getBestTool]
 * to pick the highest-tier tool a player owns that is valid for a given spot.
 */
enum class RodDefinition(
    val itemId: Int,
    val priority: Int,
) {
    CRAYFISH_CAGE(itemId = 13431, priority = 1),
    SMALL_NET(itemId = 303, priority = 2),
    BIG_FISHING_NET(itemId = 305, priority = 3),
    LOBSTER_POT(itemId = 301, priority = 4),
    FISHING_ROD(itemId = 307, priority = 5),
    FLY_FISHING_ROD(itemId = 309, priority = 6),
    BARBARIAN_ROD(itemId = 11323, priority = 7),
    HARPOON(itemId = 311, priority = 8),
    DRAGON_HARPOON(itemId = 21028, priority = 9),
    ;

    companion object {
        /**
         * Returns the highest-priority tool the player owns that matches the
         * required tool for [spot]. Returns null if the player lacks it entirely.
         */
        fun getTool(
            player: Player,
            required: RodDefinition,
        ): RodDefinition? {
            // Dragon harpoon upgrades a regular harpoon spot
            if (required == HARPOON && player.hasTool(DRAGON_HARPOON.itemId)) return DRAGON_HARPOON
            return if (player.hasTool(required.itemId)) required else null
        }
    }
}
