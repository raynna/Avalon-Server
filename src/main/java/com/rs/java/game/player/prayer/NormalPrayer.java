package com.rs.java.game.player.prayer;

import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

public enum NormalPrayer implements Prayer {
        THICK_SKIN(0, 1, 1, 1, "Thick Skin") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getDefenceBoost() { return 0.05; }
        },
        BURST_OF_STRENGTH(1, 2, 4, 1, "Burst of Strength") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED, PrayerConflictGroup.MAGIC, PrayerConflictGroup.STRENGTH}; }
            @Override public double getStrengthBoost() { return 0.05; }
        },
        CLARITY_OF_THOUGHT(2, 4, 7, 1, "Clarity of Thought") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED, PrayerConflictGroup.MAGIC, PrayerConflictGroup.ATTACK}; }
            @Override public double getAttackBoost() { return 0.05; }
        },
        SHARP_EYE(3, 262144, 8, 1, "Sharp Eye") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE }; }
            @Override public int getAffectedStatIndex() { return Skills.RANGE; }
            @Override public double getRangedBoost() { return 0.05; }
        },
        MYSTIC_WILL(4, 524288, 9, 1, "Mystic Will") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE}; }
            @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
            @Override public double getMagicBoost() { return 0.05; }
        },
        ROCK_SKIN(5, 8, 10, 6, "Rock Skin") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getDefenceBoost() { return 0.10; }
        },
        SUPERHUMAN_STRENGTH(6, 16, 13, 6, "Superhuman Strength") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED, PrayerConflictGroup.MAGIC, PrayerConflictGroup.STRENGTH}; }
            @Override public double getStrengthBoost() { return 0.10; }
        },
        IMPROVED_REFLEXES(7, 32, 16, 6, "Improved Reflexes") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED, PrayerConflictGroup.MAGIC, PrayerConflictGroup.ATTACK}; }
            @Override public double getAttackBoost() { return 0.10; }
        },
        RAPID_RESTORE(8, 64, 19, 1, "Rapid Restore") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SPECIAL}; }
        },
        RAPID_HEAL(9, 128, 22, 2, "Rapid Heal") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SPECIAL}; }
        },
        PROTECT_ITEM(10, 256, 25, 2, "Protect Item") {
            @Override public boolean isProtectItemPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECT_ITEM}; }
        },
        HAWK_EYE(11, 1048576, 26, 6, "Hawk Eye") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE}; }
            @Override public int getAffectedStatIndex() { return Skills.RANGE; }
            @Override public double getRangedBoost() { return 0.10; }
        },
        MYSTIC_LORE(12, 2097152, 27, 6, "Mystic Lore") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE}; }
            @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
            @Override public double getMagicBoost() { return 0.10; }
        },
        STEEL_SKIN(13, 512, 28, 12, "Steel Skin") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getDefenceBoost() { return 0.15; }
        },
        ULTIMATE_STRENGTH(14, 1024, 31, 12, "Ultimate Strength") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED, PrayerConflictGroup.MAGIC, PrayerConflictGroup.STRENGTH}; }
            @Override public double getStrengthBoost() { return 0.15; }
        },
        INCREDIBLE_REFLEXES(15, 2048, 34, 12, "Incredible Reflexes") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED, PrayerConflictGroup.MAGIC, PrayerConflictGroup.ATTACK}; }
            @Override public double getAttackBoost() { return 0.15; }
        },
        PROTECT_FROM_SUMMONING(16, 16777216, 35, 6, "Protect from Summoning") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public boolean isSummoningProtection() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER}; }
            @Override public double getDamageReduction() { return 0.6; }
        },
        PROTECT_FROM_MAGIC(17, 4096, 37, 12, "Protect from Magic") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECTION, PrayerConflictGroup.OVERHEAD}; }
            @Override public double getDamageReduction() { return 0.6; }
        },
        PROTECT_FROM_MISSILES(18, 8192, 40, 12, "Protect from Missiles") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECTION, PrayerConflictGroup.OVERHEAD}; }
            @Override public double getDamageReduction() { return 0.6; }
        },
        PROTECT_FROM_MELEE(19, 16384, 43, 12, "Protect from Melee") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECTION, PrayerConflictGroup.OVERHEAD}; }
            @Override public double getDamageReduction() { return 0.6; }
        },
        EAGLE_EYE(20, 4194304, 44, 12, "Eagle Eye") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE}; }
            @Override public int getAffectedStatIndex() { return Skills.RANGE; }
            @Override public double getRangedBoost() { return 0.15; }
        },
        MYSTIC_MIGHT(21, 8388608, 45, 12, "Mystic Might") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE}; }
            @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
            @Override public double getMagicBoost() { return 0.15; }
        },
        RETRIBUTION(22, 32768, 46, 3, "Retribution") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER, PrayerConflictGroup.OVERHEAD}; }
        },
        REDEMPTION(23, 65536, 49, 6, "Redemption") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER, PrayerConflictGroup.OVERHEAD}; }
            @Override public double getHealPercentage() { return 0.25; }
        },
        SMITE(24, 131072, 52, 18, "Smite") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER, PrayerConflictGroup.OVERHEAD}; }
            @Override public double getEnemyDrainPercentage() { return 0.25; }
        },
        CHIVALRY(25, 33554432, 60, 24, "Chivalry") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.getSkills().getLevelForXp(Skills.DEFENCE) >= 60; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE, PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getAttackBoost() { return 0.15; }
            @Override public double getStrengthBoost() { return 0.18; }
            @Override public double getDefenceBoost() { return 0.20; }
        },
        RAPID_RENEWAL(26, 134217728, 65, 2, "Rapid Renewal") {
            @Override public boolean hasSpecialRequirements(Player player) { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RESTORATION, PrayerConflictGroup.SPECIAL}; }
            @Override public double getHealPercentage() { return 0.05; }
        },
        PIETY(27, 67108864, 70, 24, "Piety") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.getSkills().getLevelForXp(Skills.DEFENCE) >= 70; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE, PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getAttackBoost() { return 0.20; }
            @Override public double getStrengthBoost() { return 0.23; }
            @Override public double getDefenceBoost() { return 0.25; }
        },
        RIGOUR(28, 268435456*2, 74, 24, "Rigour") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.getSkills().getLevelForXp(Skills.DEFENCE) >= 70; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE, PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public int getAffectedStatIndex() { return Skills.RANGE; }
            @Override public double getRangedBoost() { return 0.20; }
            @Override public double getDefenceBoost() { return 0.25; }
        },
        AUGURY(29, 268435456, 77, 24, "Augury") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.getSkills().getLevelForXp(Skills.DEFENCE) >= 70; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OFFENSIVE, PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
            @Override public double getMagicBoost() { return 0.25; }
            @Override public double getDefenceBoost() { return 0.25; }
        };

    private final int id;
    private final int configValue;
    private final int requiredLevel;
    private final double drainRate;
    private final String name;

    NormalPrayer(int id, int configValue, int requiredLevel, double drainRate, String name) {
        this.id = id;
        this.configValue = configValue;
        this.requiredLevel = requiredLevel;
        this.drainRate = drainRate;
        this.name = name;
    }

    @Override public int getId() { return id; }
    @Override public int getConfigValue() { return configValue; }
    @Override public int getRequiredLevel() { return requiredLevel; }
    @Override public double getDrainRate() { return drainRate; }
    @Override public String getName() { return name; }
    @Override public PrayerBookType getBook() { return PrayerBookType.NORMAL; }

    @Override public boolean isProtectionPrayer() { return false; }
    @Override public double getAttackBoost() { return 0; }
    @Override public double getStrengthBoost() { return 0; }
    @Override public double getDefenceBoost() { return 0; }
    @Override public double getRangedBoost() { return 0; }
    @Override public double getMagicBoost() { return 0; }
    @Override public double getDamageReduction() { return 0; }
    @Override public double getHealPercentage() { return 0; }

    public static NormalPrayer forId(int id) {
        for (NormalPrayer prayer : values()) {
            if (prayer.id == id) {
                return prayer;
            }
        }
        return null;
    }
}