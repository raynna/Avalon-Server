package com.rs.kotlin.game.player.combat.range;

enum class AmmoTier(val priority: Int) {
    BRONZE_ARROW(1),
    IRON_ARROW(2),
    STEEL_ARROW(3),
    MITHRIL_ARROW(4),
    ADAMANT_ARROW(5),
    RUNE_ARROW(6),
    DRAGON_ARROW(7),

    BRONZE_BOLT(8),
    BLURITE_BOLT(9),
    IRON_BOLT(9),
    STEEL_BOLT(9),
    BLACK_BOLT(9),
    MITHRIL_BOLT(9),
    ADAMANT_BOLT(9),
    RUNE_BOLT(9);

    fun canUse(tier: AmmoTier): Boolean {
        return this.priority >= tier.priority
    }
}
