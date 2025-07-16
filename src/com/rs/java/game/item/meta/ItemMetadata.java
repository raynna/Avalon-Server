package com.rs.java.game.item.meta;

import java.io.Serializable;

public interface ItemMetadata extends Serializable {

    /** Determines if two items with metadata should stack. */
    boolean isStackableWith(ItemMetadata other);

    /** Creates a deep copy of the metadata for cloning. */
    ItemMetadata deepCopy();

    /** Suffix added to the item name display, like "(12 charges)". */
    String getDisplaySuffix();

    /** Optional: modify bonuses based on metadata. */
    default int getBonusOverride(CombatBonusType type, int baseBonus) {
        return baseBonus;
    }
}
