package com.rs.java.game.player.prayer;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

public enum NormalPrayer implements Prayer {
        THICK_SKIN(0, 1, 1, 1.2, "Thick Skin") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getDefenceBoost() { return 0.05; }
        },
        BURST_OF_STRENGTH(1, 2, 4, 1.2, "Burst of Strength") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.STRENGTH}; }
            @Override public double getStrengthBoost() { return 0.05; }
        },
        CLARITY_OF_THOUGHT(2, 4, 7, 1.2, "Clarity of Thought") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.ATTACK}; }
            @Override public double getAttackBoost() { return 0.05; }
        },
        SHARP_EYE(3, 262144, 8, 1.2, "Sharp Eye") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED}; }
            @Override public int getAffectedStatIndex() { return Skills.RANGE; }
            @Override public double getRangedBoost() { return 0.05; }
        },
        MYSTIC_WILL(4, 524288, 9, 1.2, "Mystic Will") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.MAGIC}; }
            @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
            @Override public double getMagicBoost() { return 0.05; }
        },
        ROCK_SKIN(5, 8, 10, 1.2, "Rock Skin") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getDefenceBoost() { return 0.10; }
        },
        SUPERHUMAN_STRENGTH(6, 16, 13, 1.2, "Superhuman Strength") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.STRENGTH}; }
            @Override public double getStrengthBoost() { return 0.10; }
        },
        IMPROVED_REFLEXES(7, 32, 16, 1.2, "Improved Reflexes") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.ATTACK}; }
            @Override public double getAttackBoost() { return 0.10; }
        },
        RAPID_RESTORE(8, 64, 19, 0.6, "Rapid Restore") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RESTORATION}; }
        },
        RAPID_HEAL(9, 128, 22, 0.6, "Rapid Heal") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RESTORATION}; }
        },
        PROTECT_ITEM(10, 256, 25, 0.6, "Protect Item") {
            @Override public boolean isProtectItemPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECT_ITEM}; }
        },
        HAWK_EYE(11, 1048576, 26, 1.2, "Hawk Eye") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED}; }
            @Override public int getAffectedStatIndex() { return Skills.RANGE; }
            @Override public double getRangedBoost() { return 0.10; }
        },
        MYSTIC_LORE(12, 2097152, 27, 1.2, "Mystic Lore") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.MAGIC}; }
            @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
            @Override public double getMagicBoost() { return 0.10; }
        },
        STEEL_SKIN(13, 512, 28, 1.2, "Steel Skin") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getDefenceBoost() { return 0.15; }
        },
        ULTIMATE_STRENGTH(14, 1024, 31, 1.2, "Ultimate Strength") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.STRENGTH}; }
            @Override public double getStrengthBoost() { return 0.15; }
        },
        INCREDIBLE_REFLEXES(15, 2048, 34, 1.2, "Incredible Reflexes") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.ATTACK}; }
            @Override public double getAttackBoost() { return 0.15; }
        },
        PROTECT_FROM_SUMMONING(16, 16777216, 35, 3.6, "Protect from Summoning") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public boolean isSummoningProtection() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.STANDARD_PROTECTION}; }
            @Override public double getDamageReduction() { return 0.6; }
        },
        PROTECT_FROM_MAGIC(17, 4096, 37, 3.6, "Protect from Magic") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.STANDARD_PROTECTION}; }
            @Override public double getDamageReduction() { return 0.6; }
        },
        PROTECT_FROM_MISSILES(18, 8192, 40, 3.6, "Protect from Missiles") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.STANDARD_PROTECTION}; }
            @Override public double getDamageReduction() { return 0.6; }
        },
        PROTECT_FROM_MELEE(19, 16384, 43, 3.6, "Protect from Melee") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.STANDARD_PROTECTION}; }
            @Override public double getDamageReduction() { return 0.6; }
        },
        EAGLE_EYE(20, 4194304, 44, 1.2, "Eagle Eye") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED}; }
            @Override public int getAffectedStatIndex() { return Skills.RANGE; }
            @Override public double getRangedBoost() { return 0.15; }
        },
        MYSTIC_MIGHT(21, 8388608, 45, 1.2, "Mystic Might") {
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.MAGIC}; }
            @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
            @Override public double getMagicBoost() { return 0.15; }
        },
        RETRIBUTION(22, 32768, 46, 1.2, "Retribution") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RETRIBUTION}; }
        },
        REDEMPTION(23, 65536, 49, 1.8, "Redemption") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SPECIAL_PROTECTION}; }
            @Override public double getHealPercentage() { return 0.25; }
        },
        SMITE(24, 131072, 52, 1.8, "Smite") {
            @Override public boolean isProtectionPrayer() { return true; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SPECIAL_PROTECTION}; }
        },
        RAPID_RENEWAL(25, 33554432, 65, 0.3, "Rapid Renewal") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.hasRenewal; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RESTORATION}; }
            @Override public double getHealPercentage() { return 0.05; }
        },
        CHIVALRY(26, 134217728, 60, 1.8, "Chivalry") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.getSkills().getLevelForXp(Skills.DEFENCE) >= 60; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.ATTACK, PrayerConflictGroup.STRENGTH, PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getAttackBoost() { return 0.15; }
            @Override public double getStrengthBoost() { return 0.18; }
            @Override public double getDefenceBoost() { return 0.20; }
        },
        PIETY(27, 67108864, 70, 1.8, "Piety") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.getSkills().getLevelForXp(Skills.DEFENCE) >= 70; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.ATTACK, PrayerConflictGroup.STRENGTH, PrayerConflictGroup.DEFENSIVE_SKINS}; }
            @Override public double getAttackBoost() { return 0.20; }
            @Override public double getStrengthBoost() { return 0.23; }
            @Override public double getDefenceBoost() { return 0.25; }
        },
        RIGOUR(28, 268435456, 74, 1.2, "Rigour") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.hasRigour && player.getSkills().getLevelForXp(Skills.DEFENCE) >= 70; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.RANGED}; }
            @Override public int getAffectedStatIndex() { return Skills.RANGE; }
            @Override public double getRangedBoost() { return 0.20; }
            @Override public double getDefenceBoost() { return 0.25; }
        },
        AUGURY(29, 268435456*2, 77, 1.2, "Augury") {
            @Override public boolean hasSpecialRequirements(Player player) { return player.hasAugury && player.getSkills().getLevelForXp(Skills.DEFENCE) >= 70; }
            @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.MAGIC}; }
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

    // Default implementations
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