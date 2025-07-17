package com.rs.java.game.item.meta;

import java.io.Serializable;

public interface ItemMetadata extends Serializable {

    Integer getType();
    Object getValue();
    void setValue(Object value);

    default int getMaxValue() {
        return Integer.MAX_VALUE;
    }

    default int getMaxEntries() {
        return 28;
    }

    default void increment(int amount) {
        // Default no-op or throw UnsupportedOperationException if you want to force override
        throw new UnsupportedOperationException("Increment not supported");
    }

    default void decrement(int amount) {
        throw new UnsupportedOperationException("Decrement not supported");
    }

    default void reset() {
        throw new UnsupportedOperationException("Reset not supported");
    }

    boolean isStackableWith(ItemMetadata other);

    ItemMetadata deepCopy();

    String getDisplaySuffix();

    default int getBonusOverride(CombatBonusType type, int baseBonus) {
        return baseBonus;
    }//TODO
}
