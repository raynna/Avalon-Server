package com.rs.java.game.item.meta;


public class DragonFireShieldMetaData implements ItemMetadata {
    private int charges;

    public DragonFireShieldMetaData(int charges) {
        this.charges = charges;
    }

    @Override
    public Integer getType() {
        return MetaDataType.DRAGONFIRE_SHIELD.getId();
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
        return 50;
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
        return new DragonFireShieldMetaData(charges);
    }

    @Override
    public String getDisplaySuffix() {
        return "(" + charges + " charges)";
    }

    @Override
    public int getBonusOverride(CombatBonusType type, int baseBonus) {
        return type == CombatBonusType.DEFENCE ? baseBonus + charges : baseBonus;
    }
}
