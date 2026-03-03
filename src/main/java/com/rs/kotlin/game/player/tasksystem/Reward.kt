package com.rs.kotlin.game.player.tasksystem

enum class Reward(
    val itemId: Int,
    val difficulty: Difficulty,
) {
    ARDOUGNE_CLOAK_1(15345, Difficulty.EASY),
    VARROCK_ARMOUR_1(11756, Difficulty.EASY),

    ARDOUGNE_CLOAK_2(15347, Difficulty.MEDIUM),
    VARROCK_ARMOUR_2(11757, Difficulty.MEDIUM),

    ARDOUGNE_CLOAK_3(15349, Difficulty.HARD),
    VARROCK_ARMOUR_3(11758, Difficulty.HARD),

    ARDOUGNE_CLOAK_4(19748, Difficulty.ELITE),
    VARROCK_ARMOUR_4(19757, Difficulty.ELITE),
}
