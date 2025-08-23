package com.rs.kotlin.game.npc.combatdata;

import java.util.List;

public enum NpcAttackStyle {
    STAB,
    SLASH,
    CRUSH,
    RANGED,
    MAGIC,
    MAGICAL_MELEE,
    UNKNOWN;

    public static NpcAttackStyle fromList(List<String> styles) {
        for (String style : styles) {
            String lower = style.toLowerCase();
            if (lower.contains("stab")) return STAB;
            if (lower.contains("slash")) return SLASH;
            if (lower.contains("crush")) return CRUSH;
            if (lower.contains("ranged")) return RANGED;
            if (lower.contains("magic")) return MAGIC;
            if (lower.contains("magical melee")) return MAGICAL_MELEE;
        }
        return UNKNOWN;
    }
}
