package com.rs.java.game.player.actions.skills.crafting;

import com.rs.java.game.item.Item;

public enum LeatherData {

    SOFT_LEATHER(1741, new int[]{1059,1061,1129}, new int[]{1,7,14}, new double[]{13.8,16.25,25});

    private int baseLeather;
    private int[] products;
    private int[] levels;
    private double[] xp;

    LeatherData(int baseLeather, int[] products, int[] levels, double[] xp) {
        this.baseLeather = baseLeather;
        this.products = products;
        this.levels = levels;
        this.xp = xp;
    }

    public int getBaseLeather() { return baseLeather; }
    public int[] getProducts() { return products; }
    public int[] getLevels() { return levels; }
    public double[] getXp() { return xp; }

    public Item getProduct(int index) {
        return new Item(products[index], 1);
    }
}
