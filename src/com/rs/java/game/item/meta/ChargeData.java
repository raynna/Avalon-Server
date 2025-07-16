package com.rs.java.game.item.meta;

import java.util.logging.Logger;

public class ChargeData implements ItemMetadata {
    private static final Logger logger = Logger.getLogger(ChargeData.class.getName());
    private int charges;

    public ChargeData(int charges) {
        this.charges = charges;
    }

    @Override
    public String getType() {
        return "dfs";
    }

    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }

    public void increment(int amount) {
        this.charges = Math.min(50, this.charges + amount);
    }

    @Override
    public boolean isStackableWith(ItemMetadata other) {
        return false;
    }

    @Override
    public ItemMetadata deepCopy() {
        logger.fine(() -> "ChargeData deepCopy called, charges=" + charges);
        return new ChargeData(charges);
    }

    @Override
    public String getDisplaySuffix() {
        return "(" + charges + " charges)";
    }

    @Override
    public int getBonusOverride(CombatBonusType type, int baseBonus) {
        if (type == CombatBonusType.DEFENCE) {
            return baseBonus + charges;
        }
        return baseBonus;
    }
}
