package raynna.game.player.actions.skills.crafting.loom

import raynna.game.player.actions.skills.crafting.ReqItem

enum class LoomData(
    val product: LoomProduct,
) {
    SACK(
        LoomProduct(
            "item.empty_sack",
            21,
            38.0,
            ReqItem.item("item.jute_fibre", 4),
        ),
    ),

    BASKET(
        LoomProduct(
            "item.basket",
            36,
            56.0,
            ReqItem.item("item.jute_fibre", 6),
        ),
    ),

    CLOTH(
        LoomProduct(
            "item.cloth",
            1,
            1.2,
            ReqItem.item("item.ball_of_wool", 2),
        ),
    ),

    SEAWEED_NET(
        LoomProduct(
            "item.empty_seaweed_net",
            52,
            -1.0,
            ReqItem.item("item.seaweed", 5),
        ),
    ),

    MILESTONE_CAPE(
        LoomProduct(
            "item.milestone_cape_10",
            1,
            -1.0,
        ),
    ),
}
