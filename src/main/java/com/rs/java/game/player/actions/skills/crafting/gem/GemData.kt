package com.rs.java.game.player.actions.skills.crafting.gem;

import com.rs.java.game.item.Item;

public enum GemData {

    OPAL(product("item.uncut_opal", "item.opal", 15, 1, 886)),
    JADE(product("item.uncut_jade", "item.jade", 20, 13, 886)),
    RED_TOPAZ(product("item.uncut_red_topaz", "item.red_topaz", 25, 16, 887)),
    SAPPHIRE(product("item.uncut_sapphire", "item.sapphire", 50, 20, 888)),
    EMERALD(product("item.uncut_emerald", "item.emerald", 67, 27, 889)),
    RUBY(product("item.uncut_ruby", "item.ruby", 85, 34, 887)),
    DIAMOND(product("item.uncut_diamond", "item.diamond", 107.5, 43, 890)),
    DRAGONSTONE(product("item.uncut_dragonstone", "item.dragonstone", 137.5, 55, 885)),
    ONYX(product("item.uncut_onyx", "item.onyx", 167.5, 67, 2717));

    private final GemProduct product;

    GemData(GemProduct product) {
        this.product = product;
    }

    public GemProduct getProduct() {
        return product;
    }

    private static GemProduct product(Object uncut, Object cut, double xp, int lvl, int anim) {
        return new GemProduct(uncut, cut, xp, lvl, anim);
    }

    public static GemData forUncut(int id) {
        for (GemData g : values())
            if (g.product.getUncut() == id)
                return g;
        return null;
    }

    public static GemData getGem(Item a, Item b) {
        GemData g = GemData.forUncut(a.getId());
        if (g == null)
            g = GemData.forUncut(b.getId());
        return g;
    }
}
