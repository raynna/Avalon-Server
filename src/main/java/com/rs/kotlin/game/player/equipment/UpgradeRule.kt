package com.rs.kotlin.game.player.equipment

data class UpgradeRule(
    val skill: Int,
    val slot: Byte,
    val tiers: List<Tier>,
    val levelBreakpoints: IntArray = intArrayOf(20, 30, 40),
    val message: String
) {
    init {
        require(tiers.size == levelBreakpoints.size + 1) {
            "tiers must be breakpoints+1 (e.g. 4 tiers for 20/30/40)"
        }
    }
}

sealed interface Tier {
    data class Key(val key: String) : Tier
    data class Id(val id: Int) : Tier
}
