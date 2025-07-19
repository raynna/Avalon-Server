package com.rs.java.game.player.prayer;

public enum PrayerConflictGroup {
    NONE(0),
    ATTACK(1),
    STRENGTH(2),
    DEFENSIVE_SKINS(3),
    RANGED(4),
    MAGIC(5),
    RESTORATION(6),
    PROTECT_ITEM(7),
    STANDARD_PROTECTION(8),
    RETRIBUTION(9),
    SPECIAL_PROTECTION(10),
    OTHER(11);

    private final int groupId;

    PrayerConflictGroup(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }
}