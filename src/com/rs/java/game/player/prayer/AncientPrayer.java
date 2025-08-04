package com.rs.java.game.player.prayer;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.kotlin.Rscm;

public enum AncientPrayer implements Prayer {

    DEFLECT_MAGIC(7, 65, 12, "Deflect Magic") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECTION, PrayerConflictGroup.OVERHEAD};
        }
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public double getDamageReduction() { return 0.6; }
        @Override public double getReflectChance() { return 0.5; }
        @Override public double getReflectAmount() { return 0.1; }
    },
    DEFLECT_MISSILES(8, 65, 12, "Deflect Missiles") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECTION, PrayerConflictGroup.OVERHEAD};
        }
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public double getDamageReduction() { return 0.6; }
        @Override public double getReflectChance() { return 0.5; }
        @Override public double getReflectAmount() { return 0.1; }
    },
    DEFLECT_MELEE(9, 65, 12, "Deflect Melee") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECTION, PrayerConflictGroup.OVERHEAD};
        }
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public double getDamageReduction() { return 0.6; }
        @Override public double getReflectChance() { return 0.5; }
        @Override public double getReflectAmount() { return 0.1; }
    },
    DEFLECT_SUMMONING(6, 85, 12, "Deflect Summoning") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER}; }
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public boolean isSummoningProtection() { return true; }
    },

    SAP_WARRIOR(1, 50, 6, "Sap Warrior") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.ATTACK; }
    },
    SAP_RANGER(2, 52, 6, "Sap Ranger") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.RANGE; }
    },
    SAP_MAGE(3, 54, 6, "Sap Mage") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
    },
    SAP_SPIRIT(4, 56, 6, "Sap Spirit") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.MELEE}; }//TODO LEECH SPEC GROUP
        @Override public int getAffectedStatIndex() { return Skills.PRAYER; }
    },

    LEECH_ATTACK(10, 62, 9, "Leech Attack") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.ATTACK; }
        @Override public double getAttackBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 0; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_attack_projectile")); }
        @Override public Graphics getGraphic() { return new Graphics(Rscm.lookup("graphic.curses_leech_attack_gfx")); }
    },
    LEECH_RANGED(11, 64, 9, "Leech Ranged") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.RANGE; }
        @Override public double getRangedBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 3; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_ranged_projectile")); }
        @Override public Graphics getGraphic() { return new Graphics(Rscm.lookup("graphic.curses_leech_ranged_gfx")); }
    },
    LEECH_MAGIC(12, 66, 9, "Leech Magic") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
        @Override public double getMagicBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 4; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_magic_projectile")); }
        @Override public Graphics getGraphic() { return new Graphics(Rscm.lookup("graphic.curses_leech_magic_gfx")); }
    },
    LEECH_DEFENCE(13, 68, 9, "Leech Defence") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.DEFENCE; }
        @Override public double getDefenceBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 2; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_defence_projectile")); }
        @Override public Graphics getGraphic() { return new Graphics(Rscm.lookup("graphic.curses_leech_defence_gfx")); }
    },
    LEECH_STRENGTH(14, 70, 9, "Leech Strength") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.STRENGTH; }
        @Override public double getStrengthBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 1; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_strength_projectile")); }
        @Override public Graphics getGraphic() { return new Graphics(Rscm.lookup("graphic.curses_leech_strength_gfx")); }
    },
    LEECH_ENERGY(15, 72, 9, "Leech Energy") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.SPECIAL_DRAIN, PrayerConflictGroup.MELEE}; }//TODO LEECH SPEC GROUP
    },
    LEECH_SPECIAL(16, 74, 9, "Leech Special Attack", Rscm.lookup("graphic.curses_leech_special_activation"), Rscm.lookup("animation.curses_activation_prayer")) {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.ENERGY_DRAIN, PrayerConflictGroup.SPECIAL_DRAIN, PrayerConflictGroup.MELEE}; }//TODO LEECH SPEC GROUP
    },

    // Other Prayers
    PROTECT_ITEM_CURSE(0, 50, 2, "Protect Item", Rscm.lookup("graphic.curses_protect_item"), Rscm.lookup("animation.curses_protect_item")) {
        @Override public boolean isProtectItemPrayer() { return true; }
    },
    BERSERK(5, 99, 2, "Berserk", Rscm.lookup("graphic.curses_berserker"), Rscm.lookup("animation.curses_berserker")),
    WRATH(17, 59, 6, "Wrath") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER, PrayerConflictGroup.OVERHEAD};
        }
    },
    SOUL_SPLIT(18, 80, 18, "Soul Split") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER, PrayerConflictGroup.OVERHEAD};
        }
        @Override public double getHealPercentage() { return 0.20; }
        @Override public double getEnemyDrainPercentage() { return 0.20; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_soulsplit_projectile")); }
        @Override public Graphics getGraphic() { return new Graphics(Rscm.lookup("graphic.curses_soulsplit_gfx")); }
    },
    TURMOIL(19, 95, 18, "Turmoil", Rscm.lookup("graphic.curses_turmoil"), Rscm.lookup("animation.curses_turmoil")) {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.SAP_CURSES};
        }
        @Override public boolean hasSpecialRequirements(Player player) {
            return player.getSkills().getLevelForXp(Skills.DEFENCE) >= 30;
        }
        @Override public double getAttackBoost() { return 0.15; }
        @Override public double getStrengthBoost() { return 0.23; }
        @Override public double getDefenceBoost() { return 0.15; }
    };

    private final int id;
    private final int level;
    private final double drainRate;
    private final String name;
    private int graphicsId = -1;
    private int animationId = -1;

    AncientPrayer(int id, int level, double drainRate, String name) {
        this(id, level, drainRate, name, -1, -1);
    }

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
    @Override public Animation getAnimation() { return new Animation(-1); }
    @Override public Graphics getGraphic() { return new Graphics(-1); }
    @Override public Graphics getProjectile() { return new Graphics(-1); }
}