package com.rs.java.game.player.actions.skills.crafting.leather

import com.rs.java.game.player.actions.skills.crafting.ReqItem
import com.rs.kotlin.rscm.Rscm

enum class LeatherData(
    val baseLeatherRef: Any,
    val products: Array<LeatherProduct>,
) {
    SOFT_LEATHER(
        "item.leather",
        arrayOf(
            LeatherProduct("item.leather_gloves", 1, 13.8),
            LeatherProduct("item.leather_boots", 7, 16.3),
            LeatherProduct("item.leather_cowl", 9, 18.5),
            LeatherProduct("item.leather_vambraces", 11, 22.0),
            LeatherProduct("item.leather_body", 14, 25.0),
            LeatherProduct("item.leather_chaps", 18, 27.0),
            LeatherProduct("item.coif", 38, 37.0),
        ),
    ),

    HARD_LEATHER(
        "item.hard_leather",
        arrayOf(
            LeatherProduct("item.hardleather_body", 28, 35.0),
        ),
    ),

    SNAKESKIN(
        "item.snakeskin",
        arrayOf(
            LeatherProduct("item.snakeskin_boots", 45, 30.0, requires("item.snakeskin", 6)),
            LeatherProduct("item.snakeskin_vambraces", 47, 35.0, requires("item.snakeskin", 8)),
            LeatherProduct("item.snakeskin_bandana", 48, 45.0, requires("item.snakeskin", 5)),
            LeatherProduct("item.snakeskin_chaps", 51, 50.0, requires("item.snakeskin", 12)),
            LeatherProduct("item.snakeskin_body", 53, 55.0, requires("item.snakeskin", 15)),
        ),
    ),

    GREEN_DRAGONHIDE(
        "item.green_dragon_leather",
        arrayOf(
            LeatherProduct("item.green_d_hide_vambraces", 57, 62.0),
            LeatherProduct("item.green_d_hide_chaps", 60, 124.0, requires("item.green_dragon_leather", 2)),
            LeatherProduct("item.green_d_hide_coif_100", 61, 124.0, requires("item.green_dragon_leather", 2)),
            LeatherProduct("item.green_d_hide_body", 63, 186.0, requires("item.green_dragon_leather", 3)),
        ),
    ),

    BLUE_DRAGONHIDE(
        "item.blue_dragon_leather",
        arrayOf(
            LeatherProduct("item.blue_d_hide_vambraces", 66, 70.0),
            LeatherProduct("item.blue_d_hide_chaps", 68, 140.0, requires("item.blue_dragon_leather", 2)),
            LeatherProduct("item.blue_d_hide_coif_100", 69, 140.0, requires("item.blue_dragon_leather", 2)),
            LeatherProduct("item.blue_d_hide_body", 71, 210.0, requires("item.blue_dragon_leather", 3)),
        ),
    ),

    RED_DRAGONHIDE(
        "item.red_dragon_leather",
        arrayOf(
            LeatherProduct("item.red_d_hide_vambraces", 73, 78.0),
            LeatherProduct("item.red_d_hide_chaps", 75, 156.0, requires("item.red_dragon_leather", 2)),
            LeatherProduct("item.red_d_hide_coif_100", 76, 156.0, requires("item.red_dragon_leather", 2)),
            LeatherProduct("item.red_d_hide_body", 77, 234.0, requires("item.red_dragon_leather", 3)),
        ),
    ),

    BLACK_DRAGONHIDE(
        "item.black_dragon_leather",
        arrayOf(
            LeatherProduct("item.black_d_hide_vambraces", 79, 86.0),
            LeatherProduct("item.black_d_hide_chaps", 82, 172.0, requires("item.black_dragon_leather", 2)),
            LeatherProduct("item.black_d_hide_coif_100", 83, 172.0, requires("item.black_dragon_leather", 2)),
            LeatherProduct("item.black_d_hide_body", 84, 258.0, requires("item.black_dragon_leather", 3)),
        ),
    ),

    ROYAL_DRAGONHIDE(
        "item.royal_dragon_leather",
        arrayOf(
            LeatherProduct("item.royal_d_hide_vambraces", 87, 94.0),
            LeatherProduct("item.royal_d_hide_chaps", 89, 188.0, requires("item.royal_dragon_leather", 2)),
            LeatherProduct("item.royal_d_hide_coif_100", 91, 188.0, requires("item.royal_dragon_leather", 2)),
            LeatherProduct("item.royal_d_hide_body", 93, 282.0, requires("item.royal_dragon_leather", 3)),
        ),
    ),

    SUQAH_LEATHER(
        "item.suqah_leather",
        arrayOf(
            LeatherProduct("item.lunar_boots", 61, 25.0),
            LeatherProduct("item.lunar_gloves", 61, 25.0),
            LeatherProduct("item.lunar_helm", 61, 25.0),
            LeatherProduct("item.lunar_legs", 61, 30.0),
            LeatherProduct("item.lunar_torso", 61, 30.0),
        ),
    ),
    ;

    fun getBaseLeather(): Int =
        when (baseLeatherRef) {
            is Int -> baseLeatherRef
            is String -> Rscm.lookup(baseLeatherRef)
            else -> error("Invalid leather id: $baseLeatherRef")
        }
}

fun requires(
    id: Any,
    amount: Int = 1,
): ReqItem = ReqItem.item(id, amount)
