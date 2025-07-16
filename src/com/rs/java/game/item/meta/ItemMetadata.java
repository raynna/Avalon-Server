package com.rs.java.game.item.meta;

import java.io.Serializable;

public interface ItemMetadata extends Serializable {

    String getType();

    boolean isStackableWith(ItemMetadata other);

    ItemMetadata deepCopy();

    String getDisplaySuffix();

    default int getBonusOverride(CombatBonusType type, int baseBonus) {
        return baseBonus;
    }//TODO
}
