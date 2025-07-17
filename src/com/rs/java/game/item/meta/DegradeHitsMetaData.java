package com.rs.java.game.item.meta;


public class DegradeHitsMetaData implements ItemMetadata {
    private int charges;

    public DegradeHitsMetaData(int charges) {
        this.charges = charges;
    }

    @Override
    public Integer getType() {
        return MetaDataType.DEGRADE_HITS.getId();
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
        return new DegradeHitsMetaData(charges);
    }

    @Override
    public String getDisplaySuffix() {
        return "(" + charges + " charges)";
    }

}
