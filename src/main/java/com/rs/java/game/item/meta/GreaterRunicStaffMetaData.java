package com.rs.java.game.item.meta;


import com.rs.java.game.item.Item;
import com.rs.java.game.player.content.GreaterRunicStaff;
import com.rs.kotlin.game.player.combat.magic.Spellbook;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GreaterRunicStaffMetaData implements ItemMetadata {

    private int spellId = -1;
    private int charges = 0;

    public GreaterRunicStaffMetaData() {}

    public GreaterRunicStaffMetaData(int spellId, int charges) {
        this.spellId = spellId;
        this.charges = Math.min(charges, getMaxValue());
    }

    public void setSpellId(int spellId) {
        this.spellId = spellId;
    }

    public int getSpellId() {
        return spellId;
    }

    public int getCharges() {
        return charges;
    }

    public void addCharges(int amount) {
        charges = Math.min(charges + amount, getMaxValue());
    }

    public void removeCharges(int amount) {
        charges -= amount;
        if (charges <= 0) {
            spellId = -1;
            charges = 0;
        }
    }


    @Override
    public Integer getType() {
        return MetaDataType.GREATER_RUNIC.getId();
    }

    @Override
    public Object getValue() {
        return charges;
    }

    @Override
    public int getMaxValue() {
        return 1000;
    }

    @Override
    public int getMaxEntries() {
        return 1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
       charges = (int) value;
    }

    @Override
    public Integer getLastDisplayedPercentage() {
        return 0;
    }

    @Override
    public void setLastDisplayedPercentage(Object value) {}

    /** For debugging / UI display */
    @Override
    public String getDisplaySuffix() {
        StringBuilder sb = new StringBuilder("(Spells: ");
        return sb.append(")").toString();
    }

    @Override
    public boolean isStackableWith(ItemMetadata other) {
        return false;
    }

    @Override
    public ItemMetadata deepCopy() {
        return new GreaterRunicStaffMetaData(this.spellId, this.charges);
    }

    @Override
    public int getBonusOverride(CombatBonusType type, int baseBonus) {
        return baseBonus;
    }
}