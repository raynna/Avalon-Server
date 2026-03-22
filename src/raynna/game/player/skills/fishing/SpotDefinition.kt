package raynna.game.player.skills.fishing

import raynna.data.rscm.RscmResolver

/**
 * Defines all fishing spots: which NPC, which tool/bait, which fish can be caught,
 * and which right-click option triggers this spot.
 *
 * Success chance is now per-fish (see [FishDefinition.successCurve]) rather than
 * per-spot, matching the wiki skilling success chart.
 */
enum class SpotDefinition(
    val npcRefs: List<Any>,
    val option: Int,
    val tool: RodDefinition,
    val baitId: Int, // -1 = no bait required
    val catches: List<FishDefinition>,
    val animation: Int,
) {
    NET(
        npcRefs = listOf("npc.fishing_spot_net_bait"),
        option = 1,
        tool = RodDefinition.SMALL_NET,
        baitId = -1,
        catches = listOf(FishDefinition.SHRIMP, FishDefinition.ANCHOVIES),
        animation = 621,
    ),
    BAIT(
        npcRefs = listOf("npc.fishing_spot_net_bait"),
        option = 2,
        tool = RodDefinition.FISHING_ROD,
        baitId = 313,
        catches = listOf(FishDefinition.SARDINE, FishDefinition.HERRING),
        animation = 622,
    ),

    LURE(
        npcRefs = listOf("npc.fishing_spot_lure_bait"),
        option = 1,
        tool = RodDefinition.FLY_FISHING_ROD,
        baitId = 314,
        catches = listOf(FishDefinition.TROUT, FishDefinition.SALMON),
        animation = 622,
    ),
    BAIT_PIKE(
        npcRefs = listOf("npc.fishing_spot_lure_bait"),
        option = 2,
        tool = RodDefinition.FISHING_ROD,
        baitId = 313,
        catches = listOf(FishDefinition.PIKE),
        animation = 622,
    ),

    CAGE(
        npcRefs = listOf("npc.fishing_spot_cage_harpoon"),
        option = 1,
        tool = RodDefinition.LOBSTER_POT,
        baitId = -1,
        catches = listOf(FishDefinition.LOBSTER),
        animation = 619,
    ),
    HARPOON(
        npcRefs = listOf("npc.fishing_spot_cage_harpoon"),
        option = 2,
        tool = RodDefinition.HARPOON,
        baitId = -1,
        catches = listOf(FishDefinition.TUNA, FishDefinition.SWORDFISH),
        animation = 618,
    ),

    BIG_NET(
        npcRefs = listOf("npc.fishing_spot_bignet_shark"),
        option = 1,
        tool = RodDefinition.BIG_FISHING_NET,
        baitId = -1,
        catches =
            listOf(
                FishDefinition.MACKEREL,
                FishDefinition.COD,
                FishDefinition.BASS,
                FishDefinition.SEAWEED,
                FishDefinition.OYSTER,
            ),
        animation = 620,
    ),
    SHARK_HARPOON(
        npcRefs = listOf("npc.fishing_spot_bignet_shark"),
        option = 2,
        tool = RodDefinition.HARPOON,
        baitId = -1,
        catches = listOf(FishDefinition.SHARK),
        animation = 6705,
    ),

    CRAYFISH_CAGE(
        npcRefs = listOf("npc.fishing_spot_cage"),
        option = 1,
        tool = RodDefinition.CRAYFISH_CAGE,
        baitId = -1,
        catches = listOf(FishDefinition.CRAYFISH),
        animation = 621,
    ),
    MONKFISH(
        npcRefs = listOf("npc.fishing_spot_net"),
        option = 1,
        tool = RodDefinition.SMALL_NET,
        baitId = -1,
        catches = listOf(FishDefinition.MONKFISH),
        animation = 621,
    ),
    CAVEFISH_SHOAL(
        npcRefs = listOf("npc.cavefish_shoal"),
        option = 1,
        tool = RodDefinition.BARBARIAN_ROD,
        baitId = 314,
        catches = listOf(FishDefinition.CAVEFISH),
        animation = 622,
    ),
    ROCKTAIL_SHOAL(
        npcRefs = listOf("npc.rocktail_shoal"),
        option = 1,
        tool = RodDefinition.BARBARIAN_ROD,
        baitId = 314,
        catches = listOf(FishDefinition.ROCKTAIL),
        animation = 622,
    ),
    ;

    companion object {
        private val map: Map<Pair<Int, Int>, SpotDefinition> by lazy {
            RscmResolver.buildIdMapWithOption(
                entries = SpotDefinition.entries,
                refsSelector = { it.npcRefs },
                optionSelector = { it.option },
            )
        }

        fun forNpcId(
            id: Int,
            option: Int,
        ): SpotDefinition? = map[id to option]

        fun getAllNpcIds(): Set<Int> = map.keys.map { it.first }.toSet()
    }
}
