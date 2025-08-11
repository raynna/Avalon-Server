package com.rs.java.game.player.prayer;

public final class BonusIndex {
    public static final int ATTACK = 0;
    public static final int STRENGTH = 1;
    public static final int DEFENCE = 2;
    public static final int RANGED = 3;
    public static final int MAGIC = 4;

    public static final String[] NAMES = {
        "Attack", "Strength", "Defence", "Ranged", "Magic"
    };

    public static String nameOf(int index) {
        return (index >= 0 && index < NAMES.length) ? NAMES[index] : "Unknown";
    }
}
