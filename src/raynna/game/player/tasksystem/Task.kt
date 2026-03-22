package raynna.game.player.tasksystem

enum class Task(
    val difficulty: Difficulty,
    val amount: Int = 1,
) {
        /*
         * EASY TASKS
         */
    CHOP_LOGS(Difficulty.EASY, 50),
    SMITH_BRONZE_PLATEBODY(Difficulty.EASY, 20),
    STEAL_FROM_BAKERS_STALL(Difficulty.EASY, 25),
    CLAIM_WILDSTALKER_HELMET(Difficulty.EASY),
    FLETCH_SHORTBOW(Difficulty.EASY, 50),
    MINE_COPPER_AND_TIN(Difficulty.EASY, 100),
    GNOME_AGILITY(Difficulty.EASY, 5),
    CUT_UNCUT_SAPPHIRE(Difficulty.EASY, 25),
    MAKE_ATTACK_POTION(Difficulty.EASY, 10),
    FISH_SHRIMP(Difficulty.EASY, 75),
    COOK_SARDINE(Difficulty.EASY, 25),
    CRAFT_AIR_RUNE(Difficulty.EASY, 200),

        /*
         * MEDIUM TASKS
         */
    CHOP_MAPLE_LOGS(Difficulty.MEDIUM, 50),
    SMITH_MITHRIL_SCIMITAR(Difficulty.MEDIUM, 20),
    MINE_MITHRIL_ORE(Difficulty.MEDIUM, 50),
    FLETCH_MAPLE_LONGBOW(Difficulty.MEDIUM, 75),
    BARBARIAN_AGILITY(Difficulty.MEDIUM, 10),
    COMPLETE_SLAYER_TASK(Difficulty.MEDIUM, 10),
    CLEAN_AVANTOE(Difficulty.MEDIUM, 50),
    CUT_UNCUT_DIAMOND(Difficulty.MEDIUM, 50),
    LIGHT_MAPLE_LOG(Difficulty.MEDIUM, 100),
    STEAL_FROM_SILK_STALL(Difficulty.MEDIUM, 50),

        /*
         * HARD TASKS
         */
    MINE_ADAMANT_ORE(Difficulty.HARD, 100),
    CHOP_YEW_LOGS(Difficulty.HARD, 100),
    FISH_SHARK(Difficulty.HARD, 150),
    CUT_UNCUT_DRAGONSTONE(Difficulty.HARD, 50),
    SMITH_ADAMANT_SWORD(Difficulty.HARD, 100),
    FLETCH_YEW_SHORTBOW(Difficulty.HARD, 150),
    MAKE_RANGING_POTION(Difficulty.HARD, 50),
    LIGHT_YEW_LOGS(Difficulty.HARD, 150),

        /*
         * ELITE TASKS
         */
    FLETCH_MAGIC_LONGBOW(Difficulty.ELITE, 200),
    CUT_UNCUT_ONYX(Difficulty.ELITE),
    CHOP_MAGIC_LOGS(Difficulty.ELITE, 150),
    MAKE_EXTREME_STRENGTH(Difficulty.ELITE, 150),
    SMITH_ADAMANT_PLATEBODY(Difficulty.ELITE, 100),
    LIGHT_MAGIC_LOG(Difficulty.ELITE, 200),
    GNOME_AGILITY_ADVANCED(Difficulty.ELITE, 25),
    STEAL_FROM_GEM_STALL(Difficulty.ELITE, 150),
    KILL_ABYSSAL_DEMON(Difficulty.ELITE),
    CRAFT_BLOOD_RUNE(Difficulty.ELITE, 500),
    MINE_RUNITE_ORE(Difficulty.ELITE, 150),
    SUMMON_UNICORN_STALLION(Difficulty.ELITE),
}
