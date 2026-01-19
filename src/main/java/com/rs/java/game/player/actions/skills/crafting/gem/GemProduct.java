package com.rs.java.game.player.actions.skills.crafting.gem;

import com.rs.kotlin.Rscm;

public final class GemProduct {

    private final Object uncut;
    private final Object cut;
    private final double xp;
    private final int level;
    private final int animation;

    public GemProduct(Object uncut, Object cut, double xp, int level, int animation) {
        this.uncut = uncut;
        this.cut = cut;
        this.xp = xp;
        this.level = level;
        this.animation = animation;
    }

    public int getUncut() {
        return uncut instanceof Integer ? (Integer) uncut : Rscm.lookup((String) uncut);
    }

    public int getCut() {
        return cut instanceof Integer ? (Integer) cut : Rscm.lookup((String) cut);
    }

    public double getXp() { return xp; }
    public int getLevel() { return level; }
    public int getAnimation() { return animation; }
}
