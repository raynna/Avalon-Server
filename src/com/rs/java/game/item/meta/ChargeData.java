package com.rs.java.game.item.meta;

public class ChargeData implements ItemMetadata {

    private int charges;

    public ChargeData(int charges) {
        this.charges = charges;
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
        return other instanceof ChargeData cd && this.charges == cd.charges;
    }

    @Override
    public ItemMetadata deepCopy() {
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
