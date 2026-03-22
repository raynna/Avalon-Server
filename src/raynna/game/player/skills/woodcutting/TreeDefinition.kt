package raynna.game.player.skills.woodcutting

import raynna.game.player.tasksystem.Task
import raynna.data.rscm.RscmResolver

enum class TreeDefinition(
    val objectRefs: List<Any>,
    val level: Int,
    val xp: Double,
    val logId: Int,
    val respawnTicks: Int,
    val depleteChance: Int = 8,
    val task: Task? = null,
) {
    NORMAL(
        listOf("object_group.tree_chop_down"),
        level = 1,
        xp = 25.0,
        logId = 1511,
        respawnTicks = 14,
        depleteChance = 1,
        task = Task.CHOP_LOGS,
    ),
    OAK(
        listOf("object_group.oak_chop_down", "object_group.oak_chop_down_clear_guide_inspect"),
        15,
        37.5,
        1521,
        respawnTicks = 14,
    ),

    WILLOW(
        listOf("object_group.willow_chop_down", "object_group.willow_tree_chop_down_clear_gather_branches_guide_inspect"),
        30,
        67.5,
        1519,
        respawnTicks = 14,
    ),
    MAPLE(
        listOf("object_group.maple_tree_chop_down", "object_group.maple_tree_chop_down_clear_guide_inspect"),
        45,
        100.0,
        1517,
        respawnTicks = 59,
        task = Task.CHOP_MAPLE_LOGS,
    ),
    YEW(
        listOf("object_group.yew_chop_down", "object_group.yew_tree_chop_down_clear_guide_inspect"),
        60,
        175.0,
        1515,
        respawnTicks = 99,
        task = Task.CHOP_YEW_LOGS,
    ),
    IVY(
        listOf("object_group.ivy_chop"),
        68,
        332.5,
        -1,
        respawnTicks = 64,
    ),
    MAGIC(
        listOf("object_group.magic_tree_chop_down", "object_group.magic_tree_chop_down_clear_guide_inspect"),
        75,
        365.0,
        1513,
        respawnTicks = 199,
        task = Task.CHOP_MAGIC_LOGS,
    ),
    BLOODWOOD(
        listOf("object_group.bloodwood_tree_chop_down"),
        85,
        320.0,
        24121,
        respawnTicks = 199,
    ),

    FRUIT_TREES(
        listOf(
            "object_group.orange_tree_chop_down_guide_inspect",
            "object_group.apple_tree_chop_down_guide_inspect",
            "object_group.palm_tree_chop_down_guide_inspect",
            "object_group.banana_tree_chop_down_guide_inspect",
            "object_group.papaya_tree_chop_down_guide_inspect",
            "object_group.curry_tree_chop_down_guide_inspect",
            "object_group.pineapple_plant_chop_down_guide_inspect",
        ),
        1,
        1.0,
        -1,
        respawnTicks = -1,
    ),
    ;

    companion object {
        private val map: Map<Int, TreeDefinition> by lazy {
            RscmResolver.buildIdMap(
                entries = TreeDefinition.entries,
                refsSelector = { it.objectRefs },
            )
        }

        fun forObjectId(id: Int): TreeDefinition? = map[id]

        fun getAllObjectIds(): Set<Int> = map.keys
    }
}
