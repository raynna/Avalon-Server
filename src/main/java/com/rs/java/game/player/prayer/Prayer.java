package com.rs.java.game.player.prayer;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;



public interface Prayer {

    /**
     * The graphics to display when activated
     */
    default Graphics getActivationGraphics() {
        return null;
    }

    /**
     * The animation to play when activated
     */
    default Animation getActivationAnimation() {
        return null;
    }
    int getId();
    default int getConfigValue() { return -1; }
    int getRequiredLevel();
    double getDrainRate();
    String getName();
    PrayerBookType getBook();
    default boolean isMembersOnly() { return false; }
    default boolean hasSpecialRequirements(Player player) { return true; }
    default boolean isProtectionPrayer() { return false; }
    default boolean isProtectItemPrayer() { return false; };
    default boolean isDeflectPrayer() { return false; }
    default int getAffectedStatIndex() { return -1; }
    default int getLeechBonusIndex() { return -1; }
    default double getDamageReduction() { return 1.0; };
    default double getReflectChance() { return 0.0; };
    default double getReflectAmount() { return 0.0; };
    default double getHealPercentage() { return 0.0; };
    default double getMagicBoost() { return 0.0; }
    default double getRangedBoost() { return 0.0; }
    default double getAttackBoost() { return 0.0; }
    default double getStrengthBoost() { return 0.0; }
    default double getDefenceBoost() { return 0.0; }
    default double getEnemyDrainPercentage() { return 0.0; }
    default boolean isSummoningProtection() { return false; }
    default Animation getAnimation() { return new Animation(-1); }
    default Graphics getProjectile() { return new Graphics(-1); }
    default Graphics getGraphic() { return new Graphics(-1); }
    default PrayerConflictGroup[] getConflictGroups() {
        return new PrayerConflictGroup[PrayerConflictGroup.NONE.getGroupId()];
    }
}
