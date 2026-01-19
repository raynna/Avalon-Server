package com.rs.java.game.player.actions.skills.crafting.loom;

import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;

public enum LoomData {

    SACK(product("item.empty_sack", 21, 38, req("item.jute_fibre", 4))),
    BASKET(product("item.basket", 36, 56, req("item.jute_fibre", 6))),
    CLOTH(product("item.cloth", 1, 1.2, req("item.ball_of_wool", 2))),
    SEAWEED_NET(product("item.empty_seaweed_net", 52, -1, req("item.seaweed", 5))),
    MILESTONE_CAPE(product("item.milestone_cape_10", 1, -1));

    private final LoomProduct product;

    LoomData(LoomProduct product) {
        this.product = product;
    }

    public LoomProduct getProduct() {
        return product;
    }

    private static LoomProduct product(Object id, int lvl, double xp, ReqItem... req) {
        return new LoomProduct(id, lvl, xp, req);
    }

    private static ReqItem req(Object id, int amount) {
        return ReqItem.item(id, amount);
    }
}
