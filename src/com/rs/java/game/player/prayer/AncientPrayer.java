package com.rs.java.game.player.prayer;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

public enum AncientPrayer implements Prayer {

    DEFLECT_MAGIC(7, 65, 0.3, "Deflect Magic", 2228, 12573) {
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public double getDamageReduction() { return 0.6; } // 40% damage reduction
        @Override public double getReflectChance() { return 0.5; } // 50% chance
        @Override public double getReflectAmount() { return 0.1; } // Reflects 10% damage
    },
    DEFLECT_MISSILES(8, 65, 0.3, "Deflect Missiles", 2229, 12573) {
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public double getDamageReduction() { return 0.6; } // 40% damage reduction
        @Override public double getReflectChance() { return 0.5; } // 50% chance
        @Override public double getReflectAmount() { return 0.1; } // Reflects 10% damage
    },
    DEFLECT_MELEE(9, 65, 0.3, "Deflect Melee", 2230, 12573) {
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public double getDamageReduction() { return 0.6; } // 40% damage reduction
        @Override public double getReflectChance() { return 0.5; } // 50% chance
        @Override public double getReflectAmount() { return 0.1; } // Reflects 10% damage
    },
    DEFLECT_SUMMONING(18, 85, 0.3, "Deflect Summoning", 2231, 12573) {
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public boolean isSummoningProtection() { return true; }
    },

    SAP_WARRIOR(1, 50, 0.25, "Sap Warrior", 2216, 12569) {
        @Override public int getAffectedStatIndex() { return Skills.ATTACK; }
    },
    SAP_RANGER(2, 52, 0.25, "Sap Ranger", 2217, 12569) {
        @Override public int getAffectedStatIndex() { return Skills.RANGE; }
    },
    SAP_MAGE(3, 54, 0.25, "Sap Mage", 2218, 12569) {
        @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
    },
    SAP_SPIRIT(4, 56, 0.25, "Sap Spirit", 2219, 12569) {
        @Override public int getAffectedStatIndex() { return Skills.PRAYER; }
    },

    LEECH_ATTACK(10, 62, 0.35, "Leech Attack", 2235, 12575) {
        @Override public int getAffectedStatIndex() { return Skills.ATTACK; }
        @Override public int getLeechBonusIndex() { return 0; }
    },
    LEECH_RANGED(11, 64, 0.35, "Leech Ranged", 2236, 12575) {
        @Override public int getAffectedStatIndex() { return Skills.RANGE; }
        @Override public int getLeechBonusIndex() { return 3; }
    },
    LEECH_MAGIC(12, 66, 0.35, "Leech Magic", 2237, 12575) {
        @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
        @Override public int getLeechBonusIndex() { return 4; }
    },
    LEECH_DEFENCE(13, 68, 0.35, "Leech Defence", 2238, 12575) {
        @Override public int getAffectedStatIndex() { return Skills.DEFENCE; }
        @Override public int getLeechBonusIndex() { return 2; }
    },
    LEECH_STRENGTH(14, 70, 0.35, "Leech Strength", 2239, 12575) {
        @Override public int getAffectedStatIndex() { return Skills.STRENGTH; }
        @Override public int getLeechBonusIndex() { return 1; }
    },
    LEECH_ENERGY(15, 72, 0.35, "Leech Energy", 2240, 12575),
    LEECH_SPECIAL(16, 74, 0.35, "Leech Special Attack", 2241, 12575),

    // Other Prayers
    PROTECT_ITEM_CURSE(0, 50, 1.8, "Protect Item", 2213, 12567) {
        @Override public boolean isProtectItemPrayer() { return true; }
    },
    BERSERK(5, 99, 0.6, "Berserk", 2256, 12585),
    WRATH(6, 59, 1.8, "Wrath", 2266, 12589) {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.STANDARD_PROTECTION, PrayerConflictGroup.SPECIAL_PROTECTION};
        }
    },
    SOUL_SPLIT(17, 80, 0.33, "Soul Split", 2264, 12581) {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.STANDARD_PROTECTION, PrayerConflictGroup.SPECIAL_PROTECTION};
        }
        @Override public double getHealPercentage() { return 0.1; } // Heals 10% of damage dealt
    },
    TURMOIL(19, 95, 0.4, "Turmoil", 2226, 12565) {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.ATTACK, PrayerConflictGroup.STRENGTH, PrayerConflictGroup.DEFENSIVE_SKINS};
        }
        @Override public boolean hasSpecialRequirements(Player player) {
            return player.getSkills().getLevelForXp(Skills.DEFENCE) >= 30;
        }
        @Override public double getStrengthBoost() { return 0.15; }
        @Override public double getDefenceBoost() { return 0.15; }
        @Override public double getEnemyDrainPercentage() { return 0.10; }
    };

    private final int id;
    private final int level;
    private final double drainRate;
    private final String name;
    private final int graphicsId;
    private final int animationId;

    AncientPrayer(int id, int level, double drainRate, String name, int graphicsId, int animationId) {
        this.id = id;
        this.level = level;
        this.drainRate = drainRate;
        this.name = name;
        this.graphicsId = graphicsId;
        this.animationId = animationId;
    }

    public static AncientPrayer forId(int id) {
        for (AncientPrayer prayer : values()) {
            if (prayer.getId() == id) {
                return prayer;
            }
        }
        return null;
    }

    @Override public int getId() { return id; }
    @Override public int getRequiredLevel() { return level; }
    @Override public double getDrainRate() { return drainRate; }
    @Override public String getName() { return name; }
    @Override public PrayerBookType getBook() { return PrayerBookType.ANCIENT_CURSES; } // Changed from NORMAL to ANCIENT_CURSES

    @Override public Graphics getActivationGraphics() {
        return graphicsId == -1 ? null : new Graphics(graphicsId);
    }
    @Override public Animation getActivationAnimation() {
        return animationId == -1 ? null : new Animation(animationId);
    }

    // Default implementations
    @Override public boolean isProtectionPrayer() { return false; }
    @Override public double getHealPercentage() { return 0; }
    @Override public double getAttackBoost() { return 0; }
    @Override public double getStrengthBoost() { return 0; }
    @Override public double getDefenceBoost() { return 0; }
    @Override public double getRangedBoost() { return 0; }
    @Override public double getMagicBoost() { return 0; }
    @Override public double getEnemyDrainPercentage() { return 0; }
}