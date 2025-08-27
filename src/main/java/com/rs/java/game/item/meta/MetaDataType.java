package com.rs.java.game.item.meta;

import java.util.HashMap;
import java.util.Map;

public enum MetaDataType {
    DRAGONFIRE_SHIELD(0),
    RUNE_POUCH(1),
    DEGRADE_TICKS(2),
    DEGRADE_HITS(3),
    POLYPORE(4),
    GREATER_RUNIC(5);

    private final int id;

    MetaDataType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private static final Map<Integer, MetaDataType> BY_ID = new HashMap<>();

    static {
        for (MetaDataType type : values()) {
            BY_ID.put(type.id, type);
        }
    }

    public static MetaDataType fromId(int id) {
        return BY_ID.get(id);
    }
}
