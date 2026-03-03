package com.rs.kotlin.game.player.skills.mining

import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils

enum class PickaxeDefinition(
    val itemId: Int,
    val levelRequired: Int,
    val animation: Int,
    val priority: Int,
    val baseTicks: Int,
    val fastTicks: Int? = null,
    val fastChance: Int? = null, // 1 in X chance
) {
    BRONZE(1265, 1, 625, 1, 8),
    IRON(1267, 1, 626, 2, 7),
    STEEL(1269, 6, 627, 3, 6),
    MITHRIL(1273, 21, 629, 5, 5),
    ADAMANT(1271, 31, 628, 6, 4),
    RUNE(1275, 41, 624, 7, 3),

    DRAGON(
        15259,
        61,
        12189,
        8,
        baseTicks = 3,
        fastTicks = 2,
        fastChance = 6, // 1/6
    ),

    INFERNAL(
        13661,
        61,
        10222,
        9,
        baseTicks = 3,
        fastTicks = 2,
        fastChance = 6,
    ),

    CRYSTAL(
        23680,
        71,
        8330,
        10,
        baseTicks = 3,
        fastTicks = 2,
        fastChance = 4, // 1/4
    ),
    ;

    fun isInfernal() = this == INFERNAL

    fun getMiningDelay(): Int {
        if (fastTicks != null && fastChance != null) {
            if (Utils.roll(1, fastChance)) {
                return fastTicks
            }
        }
        return baseTicks
    }

    companion object {
        fun getBestPickaxe(player: Player): PickaxeDefinition? =
            entries
                .filter {
                    player.hasTool(it.itemId) &&
                        player.skills.getLevel(Skills.MINING) >= it.levelRequired
                }.maxByOrNull { it.priority }
    }
}
