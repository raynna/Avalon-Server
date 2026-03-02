package com.rs.kotlin.game.player.skills.woodcutting

enum class TreeDefinition(
    val objectIds: IntArray,
    val level: Int,
    val xp: Double,
    val logId: Int,
    val low: Int,
    val high: Int,
    val respawnTicks: Int,
    val stumpId: Int,
    val depletionMode: DepletionMode,
    val despawnTicks: Int = 0, // only used if TIMED
) {
    NORMAL(
        intArrayOf(38783, 38785, 38787),
        1,
        25.0,
        1511,
        30,
        100,
        respawnTicks = 20,
        stumpId = 1342,
        depletionMode = DepletionMode.INSTANT,
    ),
    EVERGREEN(
        intArrayOf(54778, 54787),
        1,
        25.0,
        1511,
        30,
        100,
        respawnTicks = 20,
        stumpId = 1342,
        depletionMode = DepletionMode.INSTANT,
    ),

    OAK(
        intArrayOf(38731, 38732),
        15,
        37.5,
        1521,
        40,
        120,
        respawnTicks = 14,
        stumpId = 1356,
        depletionMode = DepletionMode.TIMED,
        despawnTicks = 45,
    ),

    WILLOW(
        intArrayOf(1308),
        30,
        67.5,
        1519,
        50,
        140,
        respawnTicks = 14,
        stumpId = 5554,
        depletionMode = DepletionMode.TIMED,
        despawnTicks = 50,
    ),
    ;

    companion object {
        private val map =
            TreeDefinition.entries
                .flatMap { def -> def.objectIds.map { it to def } }
                .toMap()

        @JvmStatic
        fun forObjectId(id: Int): TreeDefinition? = map[id]
    }
}
