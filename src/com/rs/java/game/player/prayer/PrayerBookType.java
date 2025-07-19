package com.rs.java.game.player.prayer;

public enum PrayerBookType {
    NORMAL(0, "Normal Prayers"),
    ANCIENT_CURSES(1, "Ancient Curses");

    private final int id;
    private final String name;

    PrayerBookType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}