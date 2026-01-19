package com.rs.java.game.player.actions.skills.crafting.loom;

import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;
import com.rs.kotlin.Rscm;

public final class LoomProduct {

    private final Object id;
    private final int level;
    private final double xp;
    private final ReqItem[] requirements;

    public LoomProduct(Object id, int level, double xp, ReqItem... requirements) {
        this.id = id;
        this.level = level;
        this.xp = xp;
        this.requirements = requirements;
    }

    public int getId() {
        if (id instanceof Integer) return (Integer) id;
        return Rscm.lookup((String) id);
    }

    public int getLevel() { return level; }
    public double getXp() { return xp; }
    public ReqItem[] getRequirements() { return requirements; }
}
