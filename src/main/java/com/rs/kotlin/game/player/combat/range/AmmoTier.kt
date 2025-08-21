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
    RUNE_BOLT(9),

    /** Dungeoneering Arrowrs*/
    NOVITE_ARROW(priority = 1),
    BATHUS_ARROW(priority = 2),
    MARMAROS_ARROW(priority = 3),
    KRATONITE_ARROW(priority = 4),
    FRACTITE_ARROW(priority = 5),
    ZEPHYRIUM_ARROW(priority = 6),
    ARGONITE_ARROW(priority = 7),
    KATAGON_ARROW(priority = 8),
    GORGONITE_ARROW(priority = 9),
    PROMETHIUM_ARROW(priority = 10),
    SAGITTARIAN_ARROW(priority = 11),

    ;

    fun canUse(tier: AmmoTier): Boolean {
        return this.priority >= tier.priority
    }
}
