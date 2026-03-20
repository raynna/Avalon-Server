package com.rs.kotlin.game.player.skills.mining

import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.kotlin.rscm.RscmResolver

enum class RockDefinition(
    val objectRefs: List<Any>,
    val level: Int,
    val xp: Double,
    val oreId: Int,
    val respawnTicks: Int,
    val lowChance: Int = 0,
    val highChance: Int = 0,
    val alwaysSuccess: Boolean = false,
    val alternativeOre: ((Player) -> Int)? = null,
    val variants: List<RockVariant>? = null,
) {
    RUNE_ESSENCE(
        listOf("object_group.rune_essence_mine_prospect"),
        level = 1,
        xp = 5.0,
        oreId = 1436,
        respawnTicks = -1,
        lowChance = 0,
        highChance = 0,
        alwaysSuccess = true,
        alternativeOre = { player ->
            if (player.skills.getLevel(Skills.MINING) >= 30) 7936 else 1436
        },
    ),
    CLAY(
        listOf("object_group.clay_rocks_mine"),
        level = 1,
        xp = 5.0,
        oreId = 434,
        respawnTicks = 2,
        lowChance = 128,
        highChance = 400,
    ),
    LIMESTONE(
        listOf("object_group.limestone_rocks_mine"),
        level = 10,
        xp = 26.5,
        oreId = 3211,
        respawnTicks = 9,
        lowChance = 95,
        highChance = 350,
    ),
    COPPER(
        listOf("object_group.copper_ore_rocks_mine"),
        level = 1,
        xp = 17.5,
        oreId = 436,
        respawnTicks = 4,
        lowChance = 100,
        highChance = 350,
    ),

    TIN(
        listOf("object_group.tin_ore_rocks_mine"),
        1,
        17.5,
        438,
        4,
        lowChance = 100,
        highChance = 350,
    ),

    IRON(
        listOf("object_group.iron_ore_rocks_mine"),
        15,
        35.0,
        440,
        9,
        lowChance = 96,
        highChance = 350,
    ),

    SILVER(
        listOf("object_group.silver_ore_rocks_mine"),
        20,
        40.0,
        442,
        96,
        lowChance = 25,
        highChance = 200,
    ),

    COAL(
        listOf("object_group.coal_rocks_mine"),
        30,
        50.0,
        453,
        48,
        lowChance = 16,
        highChance = 100,
    ),

    SANDSTONE(
        listOf("object_group.sandstone_rocks_mine"),
        35,
        50.0,
        -1,
        9,
        variants =
            listOf(
                RockVariant(6971, 25, 200, 30.0), // 1kg
                RockVariant(6973, 16, 100, 40.0), // 2kg
                RockVariant(6975, 8, 75, 50.0), // 5kg
                RockVariant(6977, 4, 50, 60.0), // 10kg
            ),
    ),

    GRANITE(
        listOf("object_group.granite_rocks_mine"),
        35,
        50.0,
        -1,
        9,
        variants =
            listOf(
                RockVariant(6979, 16, 100, 50.0), // 500g
                RockVariant(6981, 8, 75, 60.0), // 2kg
                RockVariant(6983, 6, 64, 75.0), // 5kg
            ),
    ),

    GEM(
        listOf("object_group.gem_rocks_mine"),
        level = 40,
        xp = 65.0,
        oreId = -1,
        respawnTicks = 99,
        lowChance = 28,
        highChance = 70,
    ),
    GOLD(
        listOf("object_group.gold_ore_rocks_mine"),
        level = 40,
        xp = 65.0,
        oreId = 444,
        respawnTicks = 99,
        lowChance = 7,
        highChance = 75,
    ),

    MITHRIL(
        listOf("object_group.mithril_ore_rocks_mine"),
        55,
        80.0,
        447,
        200,
        lowChance = 4,
        highChance = 50,
    ),

    ADAMANTITE(
        listOf("object_group.adamantite_ore_rocks_mine"),
        70,
        95.0,
        449,
        400,
        lowChance = 2,
        highChance = 25,
    ),

    RUNITE(
        listOf("object_group.runite_ore_rocks_mine"),
        85,
        125.0,
        451,
        600,
        lowChance = 1,
        highChance = 18,
    ),
    ;

    companion object {
        private val map: Map<Int, RockDefinition> by lazy {
            RscmResolver.buildIdMap(
                entries = entries,
                refsSelector = { it.objectRefs },
            )
        }

        fun forObjectId(id: Int): RockDefinition? = map[id]

        fun getAllObjectIds(): Set<Int> = map.keys
    }
}
