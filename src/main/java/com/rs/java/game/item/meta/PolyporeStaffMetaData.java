package com.rs.java.game.item.meta;


public class PolyporeStaffMetaData implements ItemMetadata {
    public static final int MAX_CHARGES = 3000;

    private int charges;

    public PolyporeStaffMetaData(int charges) {
        this.charges = charges;
    }

    @Override
    public Integer getType() {
        return MetaDataType.POLYPORE.getId();
    }

    @Override
    public Integer getValue() {
        return charges;
    }

    @Override
    public void setValue(Object value) {
        this.charges = (int) value;
    }

    @Override
    public Integer getLastDisplayedPercentage() {
        return 0;
    }

    @Override
    public void setLastDisplayedPercentage(Object value) {

    }

    @Override
    public int getMaxValue() {
        return MAX_CHARGES;
    }

    @Override
    public void increment(int amount) {
        this.charges = Math.min(getMaxValue(), this.charges + amount);
    }

    @Override
    public void decrement(int amount) {
        this.charges = Math.max(0, this.charges - amount);
    }

    @Override
    public void reset() {
        this.charges = 0;
    }

    @Override
    public boolean isStackableWith(ItemMetadata other) {
        return false;
    }

    @Override
    public ItemMetadata deepCopy() {
        return new PolyporeStaffMetaData(charges);
    }

    @Override
    public String getDisplaySuffix() {
        return "(" + charges + " charges)";
    }

    @Override
    public int getBonusOverride(CombatBonusType type, int baseBonus) {
        return 0;
    }
}
