package com.rs.java.game.player.actions.skills.crafting.glass;

import com.rs.kotlin.Rscm;
import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;

public enum GlassBlowingData {

    MOLTEN_GLASS(
            "item.molten_glass",

            product("item.beer_glass", 1, 17.5),
            product("item.candle_lantern", 4, 19),
            product("item.oil_lamp", 12, 25),
            product("item.vial", 33, 35),
            product("item.fishbowl", 42, 42.5),
            product("item.unpowered_orb", 46, 52.5),
            product("item.lantern_lens", 49, 55),
            product("item.empty_light_orb", 87, 70)
    );

    private final Object base;
    private final GlassProduct[] products;

    GlassBlowingData(Object base, GlassProduct... products) {
        this.base = base;
        this.products = products;
    }

    private static GlassProduct product(Object id, int lvl, double xp, ReqItem... req) {
        return new GlassProduct(id, lvl, xp, req);
    }

    public int getBase() {
        if (base instanceof Integer)
            return (Integer) base;
        return Rscm.lookup((String) base);
    }

    public static GlassBlowingData getGlassData(int id1, int id2) {

        int pipe = 1785; // glassblowing pipe
        int molten = Rscm.lookup("item.molten_glass");

        boolean hasPipe = id1 == pipe || id2 == pipe;
        boolean hasMolten = id1 == molten || id2 == molten;

        if (hasPipe && hasMolten)
            return MOLTEN_GLASS;

        return null;
    }


    public GlassProduct[] getProducts() {
        return products;
    }
}
