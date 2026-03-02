package com.rs.kotlin.game.player.skills.woodcutting

import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills

enum class AxeDefinition(
    val itemId: Int,
    val levelRequired: Int,
    val animation: Int,
    val efficiencyMultiplier: Double,
) {
    BRONZE(1351, 1, 879, 1.0),
    IRON(1349, 1, 877, 1.5),
    STEEL(1353, 6, 875, 2.0),
    BLACK(1361, 11, 873, 2.25),
    MITHRIL(1355, 21, 871, 2.5),
    ADAMANT(1357, 31, 869, 3.0),
    RUNE(1359, 41, 867, 3.5),
    DRAGON(6739, 61, 2846, 3.85),
    CRYSTAL(23673, 71, 8334, 4.025),
    ;

    companion object {
        fun getBestAxe(player: Player): AxeDefinition? =
            entries
                .filter {
                    player.hasTool(it.itemId) &&
                        player.skills.getLevel(Skills.WOODCUTTING) >= it.levelRequired
                }.maxByOrNull { it.efficiencyMultiplier }
    }
}
