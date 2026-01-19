package com.rs.java.game.player.actions.skills.fletching;

import com.rs.kotlin.Rscm;

public class FletchingProduct {
    private final Object productId;
    private final int amount;
    private final int level;
    private final double experience;
    private final int inputPer;

    public FletchingProduct(Object productId, int amount, int level, double experience, int inputPer) {
        this.productId = productId;
        this.amount = amount;
        this.level = level;
        this.experience = experience;
        this.inputPer = inputPer;
    }

    public FletchingProduct(Object productId, int amount, int level, double experience) {
        this(productId, amount, level, experience, 1);
    }

    public int getProductId() {
        if (productId instanceof Integer) {
            return (Integer) productId;
        } else if (productId instanceof String) {
            return Rscm.lookup((String) productId);
        }
        return -1;
    }

    public int getAmount() {
        return amount;
    }

    public int getLevel() {
        return level;
    }

    public double getExperience() {
        return experience;
    }

    public int getInputPerUnit() {
        return inputPer;
    }

    public boolean consumesMultiple() {
        return inputPer > 1;
    }

    public Object getRawProductId() {
        return productId;
    }
}