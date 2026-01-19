package com.rs.java.game.player.actions.skills.crafting.leather;

import com.rs.kotlin.Rscm;

public enum LeatherData {

    SOFT_LEATHER(
            "item.leather",
            product("item.leather_gloves", 1, 13.8),
            product("item.leather_boots", 7, 16.3),
            product("item.leather_cowl", 9, 18.5),
            product("item.leather_vambraces", 11, 22),
            product("item.leather_body", 14, 25),
            product("item.leather_chaps", 18, 27),
            product("item.coif", 38, 37)),

    HARD_LEATHER("item.hard_leather", product("item.hardleather_body", 28, 35)),

    SNAKESKIN("item.snakeskin",
            product("item.snakeskin_boots", 45, 30, requires("item.snakeskin", 6)),
            product("item.snakeskin_vambraces", 47, 35, requires("item.snakeskin", 8)),
            product("item.snakeskin_bandana", 48, 45, requires("item.snakeskin", 5)),
            product("item.snakeskin_chaps", 51, 50, requires("item.snakeskin", 12)),
            product("item.snakeskin_body", 53, 55, requires("item.snakeskin", 15))),

    GREEN_DRAGONHIDE("item.green_dragon_leather",
            product("item.green_d_hide_vambraces", 57, 62),
            product("item.green_d_hide_chaps", 60, 124, requires("item.green_dragon_leather", 2)),
            product("item.green_d_hide_coif_100", 61, 124, requires("item.green_dragon_leather", 2)),
            product("item.green_d_hide_body", 63, 186, requires("item.green_dragon_leather", 3))),

    BLUE_DRAGONHIDE("item.blue_dragon_leather",
            product("item.blue_d_hide_vambraces", 66, 70),
            product("item.blue_d_hide_chaps", 68, 140, requires("item.blue_dragon_leather", 2)),
            product("item.blue_d_hide_coif_100", 69, 140, requires("item.blue_dragon_leather", 2)),
            product("item.blue_d_hide_body", 71, 210, requires("item.blue_dragon_leather", 3))
    ),

    RED_DRAGONHIDE("item.red_dragon_leather",
            product("item.red_d_hide_vambraces", 73, 78),
            product("item.red_d_hide_chaps", 75, 156, requires("item.red_dragon_leather", 2)),
            product("item.red_d_hide_coif_100", 76, 156, requires("item.red_dragon_leather", 2)),
            product("item.red_d_hide_body", 77, 234, requires("item.red_dragon_leather", 3))
    ),

    BLACK_DRAGONHIDE("item.black_dragon_leather",
            product("item.black_d_hide_vambraces", 79, 86),
            product("item.black_d_hide_chaps", 82, 172, requires("item.black_dragon_leather", 2)),
            product("item.black_d_hide_coif_100", 83, 172, requires("item.black_dragon_leather", 2)),
            product("item.black_d_hide_body", 84, 258, requires("item.black_dragon_leather", 3))
    ),

    ROYAL_DRAGONHIDE("item.royal_dragon_leather",
            product("item.royal_d_hide_vambraces", 87, 94),
            product("item.royal_d_hide_chaps", 89, 188, requires("item.royal_dragon_leather", 2)),
            product("item.royal_d_hide_coif_100", 91, 188, requires("item.royal_dragon_leather", 2)),
            product("item.royal_d_hide_body", 93, 282, requires("item.royal_dragon_leather", 3))
    ),

    SUQAH_LEATHER("item.suqah_leather",
            product("item.lunar_boots", 61, 25),
            product("item.lunar_gloves", 61, 25),
            product("item.lunar_helm", 61, 25),
            product("item.lunar_legs", 61, 30),
            product("item.lunar_torso", 61, 30)
    ),
    ;

    private final Object baseLeather;
    private final LeatherProduct[] products;

    private static LeatherProduct product(Object id, int lvl, double xp, ReqItem... req) {
        return new LeatherProduct(id, lvl, xp, req);
    }

    private static ReqItem requires(Object id, int amount) {
        return ReqItem.item(id, amount);
    }


    LeatherData(Object baseLeather, LeatherProduct... products) {
        this.baseLeather = baseLeather;
        this.products = products;
    }


    public int getBaseLeather() {
        if (baseLeather instanceof Integer)
            return (Integer) baseLeather;
        if (baseLeather instanceof String)
            return Rscm.lookup((String) baseLeather);
        throw new IllegalStateException("Invalid leather id: " + baseLeather);
    }

    public LeatherProduct[] getProducts() {
        return products;
    }
}
