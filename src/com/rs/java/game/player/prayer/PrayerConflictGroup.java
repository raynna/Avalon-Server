package com.rs.java.game.player.prayer;

public enum PrayerConflictGroup {
    NONE(0),
    MELEE(1),
    OFFENSIVE(2),
    ATTACK(3),
    STRENGTH(4),
    DEFENSIVE_SKINS(5),
    RANGED(6),
    MAGIC(7),
    RESTORATION(8),
    PROTECT_ITEM(9),
    PROTECTION(10),
    OVERHEAD(11),
    SPECIAL(12),
    OTHER(13);

    private final int groupId;

    PrayerConflictGroup(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }
}