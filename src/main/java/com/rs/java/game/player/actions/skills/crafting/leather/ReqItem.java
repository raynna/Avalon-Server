package com.rs.java.game.player.actions.skills.crafting.leather;

import com.rs.kotlin.Rscm;

public final class ReqItem {

    private final Object id;
    private final int amount;

    public ReqItem(Object id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() {
        if (id instanceof Integer)
            return (Integer) id;
        return Rscm.lookup((String) id);
    }

    public static ReqItem item(Object id, int amount) {
        return new ReqItem(id, amount);
    }

    public static ReqItem[] requiredItems(ReqItem... items) {
        return items;
    }


    public int getAmount() {
        return amount;
    }
}
