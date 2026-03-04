package com.rs.java.game.player.actions.skills.crafting.spinningwheel;

import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;

public enum SpinningData {

    BALL_OF_WOOL(product("item.ball_of_wool", 1, 2.5, req("item.wool"))),
    BALL_OF_BLACK_WOOL(product("item.ball_of_black_wool", 1, -1, req("item.black_wool"))),
    BOWSTRING(product("item.bow_string", 1, 15, req("item.flax"))),
    CROSSBOW_STRING(product("item.crossbow_string", 10, 15, req("item.sinew"))),
    MAGIC_STRING(product("item.magic_string", 19, 30, req("item.magic_roots"))),
    ROPE(product("item.rope", 30, 25, req("item.hair")));

    private final SpinningProduct product;

    SpinningData(SpinningProduct product) {
        this.product = product;
    }

    public SpinningProduct getProduct() {
        return product;
    }

    private static SpinningProduct product(Object id, int lvl, double xp, ReqItem... req) {
        return new SpinningProduct(id, lvl, xp, req);
    }

    private static ReqItem req(Object id) {
        return ReqItem.item(id, 1);
    }
}
