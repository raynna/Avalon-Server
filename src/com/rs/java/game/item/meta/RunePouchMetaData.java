package com.rs.java.game.item.meta;


import com.rs.java.game.item.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RunePouchMetaData implements ItemMetadata {

    private final Map<Integer, Integer> runes = new HashMap<>();

    public RunePouchMetaData() {}

    public RunePouchMetaData(Map<Integer, Integer> runes) {
        if (runes != null) {
            this.runes.putAll(runes);
        }
    }

    @Override
    public Integer getType() {
        return MetaDataType.RUNE_POUCH.getId();
    }

    @Override
    public Object getValue() {
        return Collections.unmodifiableMap(runes);
    }

    @Override
    public int getMaxValue() {
        return 16000;
    }

    @Override
    public int getMaxEntries() {
        return 3;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        runes.clear();
        if (value instanceof Map<?, ?> m) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                if (entry.getKey() instanceof Integer key && entry.getValue() instanceof Integer amount) {
                    if (amount > getMaxValue())
                        amount = getMaxValue();
                    runes.put(key, amount);
                }
            }
            if (runes.size() > getMaxEntries())
                throw new IllegalStateException("Rune pouch can only hold 3 rune types.");
        } else {
            throw new IllegalArgumentException("Expected Map<Integer, Integer> for RunePouchMetaData");
        }
    }

    public boolean isEmpty() {
        return runes.isEmpty();
    }

    public Item getRuneAtSlot(int slot) {
        if (slot < 0 || slot >= runes.size()) {
            return null;
        }
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : runes.entrySet()) {
            if (i == slot) {
                int runeId = entry.getKey();
                int amount = entry.getValue();
                return new Item(runeId, amount);
            }
            i++;
        }
        return null;
    }

    public void addRune(int itemId, int amount) {
        runes.merge(itemId, amount, Integer::sum);
        if (runes.size() > 3)
            throw new IllegalStateException("Rune pouch can only hold 3 rune types.");
    }

    public void removeRune(int itemId, int amount) {
        runes.computeIfPresent(itemId, (id, count) -> (count - amount <= 0) ? null : count - amount);
    }

    public Map<Integer, Integer> getRunes() {
        return Collections.unmodifiableMap(runes);
    }

    public Item[] getRunesToArray() {
        Item[] items = new Item[3];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : runes.entrySet()) {
            items[i++] = new Item(entry.getKey(), entry.getValue());
        }
        while (i < 3)
            items[i++] = null;
        return items;
    }

    @Override
    public boolean isStackableWith(ItemMetadata other) {
        return false;
    }

    @Override
    public ItemMetadata deepCopy() {
        return new RunePouchMetaData(new HashMap<>(runes));
    }

    @Override
    public String getDisplaySuffix() {
        StringBuilder sb = new StringBuilder("(Runes: ");
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : runes.entrySet()) {
            if (i++ > 0) sb.append(", ");
            sb.append("ID ").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return sb.append(")").toString();
    }

    @Override
    public int getBonusOverride(CombatBonusType type, int baseBonus) {
        return baseBonus;
    }
}

