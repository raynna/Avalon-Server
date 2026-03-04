package com.rs.java.game.player.actions.skills.crafting.spinningwheel

import com.rs.java.game.player.actions.skills.crafting.ReqItem

enum class SpinningData(
    val product: SpinningProduct,
) {
    BALL_OF_WOOL(
        SpinningProduct(
            "item.ball_of_wool",
            1,
            2.5,
            ReqItem.item("item.wool", 1),
        ),
    ),

    BALL_OF_BLACK_WOOL(
        SpinningProduct(
            "item.ball_of_black_wool",
            1,
            -1.0,
            ReqItem.item("item.black_wool", 1),
        ),
    ),

    BOWSTRING(
        SpinningProduct(
            "item.bow_string",
            1,
            15.0,
            ReqItem.item("item.flax", 1),
        ),
    ),

    CROSSBOW_STRING(
        SpinningProduct(
            "item.crossbow_string",
            10,
            15.0,
            ReqItem.item("item.sinew", 1),
        ),
    ),

    MAGIC_STRING(
        SpinningProduct(
            "item.magic_string",
            19,
            30.0,
            ReqItem.item("item.magic_roots", 1),
        ),
    ),

    ROPE(
        SpinningProduct(
            "item.rope",
            30,
            25.0,
            ReqItem.item("item.hair", 1),
        ),
    ),
}
