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
        @Override public double getDamageReduction() { return 0.4; }
        @Override public double getReflectChance() { return 0.5; }
        @Override public double getReflectAmount() { return 0.1; }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_deflect_magic")); }
        @Override public Animation getAnimation() { return new Animation(Rscm.lookup("animation.curses_deflect")); }
        @Override public int getActivationSound() {return Rscm.lookup("sound.protect_from_magic");}
    },
    DEFLECT_MISSILES(8, 68, 12, "Deflect Missiles") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECTION, PrayerConflictGroup.OVERHEAD};
        }
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public double getDamageReduction() { return 0.4; }
        @Override public double getReflectChance() { return 0.5; }
        @Override public double getReflectAmount() { return 0.1; }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_deflect_ranged")); }
        @Override public Animation getAnimation() { return new Animation(Rscm.lookup("animation.curses_deflect")); }
        @Override public int getActivationSound() {return Rscm.lookup("sound.protect_from_missiles");}
    },
    DEFLECT_MELEE(9, 71, 12, "Deflect Melee") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.PROTECTION, PrayerConflictGroup.OVERHEAD};
        }
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public double getDamageReduction() { return 0.4; }
        @Override public double getReflectChance() { return 0.5; }
        @Override public double getReflectAmount() { return 0.1; }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_deflect_melee")); }
        @Override public Animation getAnimation() { return new Animation(Rscm.lookup("animation.curses_deflect")); }
        @Override public int getActivationSound() {return Rscm.lookup("sound.protect_from_melee");}
    },
    DEFLECT_SUMMONING(6, 62, 12, "Deflect Summoning") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER}; }
        @Override public boolean isProtectionPrayer() { return true; }
        @Override public boolean isDeflectPrayer() { return true; }
        @Override public boolean isSummoningProtection() { return true; }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_deflect_summoning")); }
        @Override public Animation getAnimation() { return new Animation(Rscm.lookup("animation.curses_deflect")); }
        @Override public int getActivationSound() {return Rscm.lookup("sound.protect_from_melee");}

    },

    SAP_WARRIOR(1, 50, 6, "Sap Warrior") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.SPECIAL_DRAIN, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.ATTACK; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_sap_warrior_projectile")); }
        @Override public Graphics getStartGraphics() { return new Graphics(Rscm.lookup("graphic.curses_sap_warrior_gfx")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_sap_warrior_drain")); }
    },
    SAP_RANGER(2, 52, 6, "Sap Ranger") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.SPECIAL_DRAIN, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.RANGE; }
        @Override public int getLeechBonusIndex() { return 3; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_sap_ranger_projectile")); }
        @Override public Graphics getStartGraphics() { return new Graphics(Rscm.lookup("graphic.curses_sap_ranger_gfx")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_sap_ranger_drain")); }
    },
    SAP_MAGE(3, 54, 6, "Sap Mage") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.SPECIAL_DRAIN, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
        @Override public int getLeechBonusIndex() { return 4; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_sap_mage_projectile")); }
        @Override public Graphics getStartGraphics() { return new Graphics(Rscm.lookup("graphic.curses_sap_mage_gfx")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_sap_mage_drain")); }
    },
    SAP_SPIRIT(4, 56, 6, "Sap Spirit") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.SPECIAL_DRAIN, PrayerConflictGroup.MELEE}; }//TODO LEECH SPEC GROUP
        @Override public int getAffectedStatIndex() { return Skills.PRAYER; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_sap_spirit_projectile")); }
        @Override public Graphics getStartGraphics() { return new Graphics(Rscm.lookup("graphic.curses_sap_spirit_gfx")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_sap_spirit_drain")); }
    },

    LEECH_ATTACK(10, 74, 9, "Leech Attack") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.ATTACK; }
        @Override public double getAttackBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 0; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_attack_projectile")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_leech_attack_gfx")); }
    },
    LEECH_RANGED(11, 76, 9, "Leech Ranged") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.RANGE; }
        @Override public double getRangedBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 3; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_ranged_projectile")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_leech_ranged_gfx")); }
    },
    LEECH_MAGIC(12, 78, 9, "Leech Magic") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.MAGIC; }
        @Override public double getMagicBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 4; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_magic_projectile")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_leech_magic_gfx")); }
    },
    LEECH_DEFENCE(13, 80, 9, "Leech Defence") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.DEFENCE; }
        @Override public double getDefenceBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 2; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_defence_projectile")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_leech_defence_gfx")); }
    },
    LEECH_STRENGTH(14, 82, 9, "Leech Strength") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.MELEE}; }
        @Override public int getAffectedStatIndex() { return Skills.STRENGTH; }
        @Override public double getStrengthBoost() { return 0.05; }
        @Override public int getLeechBonusIndex() { return 1; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_leech_strength_projectile")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_leech_strength_gfx")); }
    },
    LEECH_ENERGY(15, 84, 9, "Leech Energy") {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.SPECIAL_DRAIN, PrayerConflictGroup.MELEE}; }//TODO LEECH SPEC GROUP
    },
    LEECH_SPECIAL(16, 86, 9, "Leech Special Attack", Rscm.lookup("graphic.curses_leech_special_activation"), Rscm.lookup("animation.curses_activation_prayer")) {
        @Override public PrayerConflictGroup[] getConflictGroups() { return new PrayerConflictGroup[]{PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.ENERGY_DRAIN, PrayerConflictGroup.SPECIAL_DRAIN, PrayerConflictGroup.MELEE}; }//TODO LEECH SPEC GROUP
    },

    // Other Prayers
    PROTECT_ITEM_CURSE(0, 50, 2, "Protect Item", Rscm.lookup("graphic.curses_protect_item"), Rscm.lookup("animation.curses_protect_item")) {
        @Override public boolean isProtectItemPrayer() { return true; }
        @Override public int getActivationSound() {return Rscm.lookup("sound.protect_from_item");}
    },
    BERSERK(5, 59, 2, "Berserk", Rscm.lookup("graphic.curses_berserker"), Rscm.lookup("animation.curses_berserker")),

    WRATH(17, 89, 6, "Wrath") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER, PrayerConflictGroup.OVERHEAD};
        }
        @Override public int getActivationSound() {return Rscm.lookup("sound.retribution");}
    },
    SOUL_SPLIT(18, 92, 18, "Soul Split") {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.OTHER, PrayerConflictGroup.OVERHEAD};
        }
        @Override public double getHealPercentage() { return 0.20; }
        @Override public double getEnemyDrainPercentage() { return 0.20; }
        @Override public Graphics getProjectile() { return new Graphics(Rscm.lookup("graphic.curses_soulsplit_projectile")); }
        @Override public Graphics getHitGraphics() { return new Graphics(Rscm.lookup("graphic.curses_soulsplit_gfx")); }
    },
    TURMOIL(19, 95, 18, "Turmoil", Rscm.lookup("graphic.curses_turmoil"), Rscm.lookup("animation.curses_turmoil")) {
        @Override public PrayerConflictGroup[] getConflictGroups() {
            return new PrayerConflictGroup[]{PrayerConflictGroup.LEECH_CURSES, PrayerConflictGroup.SAP_CURSES, PrayerConflictGroup.ENERGY_DRAIN, PrayerConflictGroup.SPECIAL_DRAIN};
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

    @Override public double getHealPercentage() { return 0; }
    @Override public double getAttackBoost() { return 0; }
    @Override public double getStrengthBoost() { return 0; }
    @Override public double getDefenceBoost() { return 0; }
    @Override public double getRangedBoost() { return 0; }
    @Override public double getMagicBoost() { return 0; }
    @Override public double getEnemyDrainPercentage() { return 0; }

}