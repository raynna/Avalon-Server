package com.rs.java.game.player.prayer;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.rs.java.game.Hit.HitLook;

public class PrayerEffectHandler {

    public static void handleProtectionEffects(Entity target, Entity source, Hit hit) {
        if (target instanceof Player defender) {
            Prayer protectionPrayer = getProtectionPrayer(defender, hit.getLook());
            if (protectionPrayer != null && protectionPrayer.isProtectionPrayer() && defender.getPrayer().isActive(protectionPrayer)) {
                handleActualProtectionPrayer(source, target, hit, protectionPrayer);
            }
        }
    }

    private static void handleActualProtectionPrayer(Entity attacker, Entity defender, Hit hit, Prayer protectionPrayer) {
        double reduction = 1.0 - protectionPrayer.getDamageReduction();

        if (attacker instanceof NPC npc) {
            if (npc.getProtectionPrayerEffectiveness() != 1.0) {
                reduction *= npc.getProtectionPrayerEffectiveness();
            } else {
                reduction = 0.0;
            }
        }
        hit.setDamage((int)(hit.getDamage() * reduction));
        if (defender instanceof Player player) {
            if (protectionPrayer instanceof AncientPrayer && protectionPrayer.isDeflectPrayer() && player.getPrayer().isPrayerActive(protectionPrayer)) {
                handleDeflectEffect(defender, attacker, hit, (AncientPrayer)protectionPrayer);
            }
        }
    }

    private static void handleDeflectEffect(Entity defender, Entity attacker, Hit hit, AncientPrayer deflectPrayer) {
        if (Utils.randomDouble() >= deflectPrayer.getReflectChance()) return;

        int reflectDamage = (int)(hit.getDamage() * deflectPrayer.getReflectAmount());
        attacker.applyHit(new Hit(defender, reflectDamage, HitLook.REFLECTED_DAMAGE));
        defender.gfx(deflectPrayer.getActivationGraphics());
        defender.animate(deflectPrayer.getActivationAnimation());
    }

    public static void handleOffensiveEffects(Player attacker, Entity target, Hit hit) {
        if (attacker.getPrayer().isActive(NormalPrayer.SMITE) && target instanceof Player) {
            handleSmiteEffect((Player)target, hit);
        }

        if (attacker.getPrayer().isAncientCurses()) {
            handleTurmoilEffects(attacker, target);
            if (attacker.getPrayer().isActive(AncientPrayer.SOUL_SPLIT)) {
                handleSoulSplitEffect(attacker, target, hit);
            } else {
                handleLeechEffects(attacker, target);
            }
        }
    }

    private static void handleSmiteEffect(Player target, Hit hit) {
        int damage = Math.min(hit.getDamage(), target.getHitpoints());
        Prayer smite = NormalPrayer.SMITE;
        int prayerDrain = (int) (damage * smite.getEnemyDrainPercentage());
        if (prayerDrain > 0) {
            target.getPrayer().drainPrayer(prayerDrain);
        }
    }

    private static void handleSoulSplitEffect(Player attacker, Entity target, Hit hit) {
        int damage = Math.min(hit.getDamage(), target.getHitpoints());
        if (damage <= 0) return;
        Prayer soulSplit = AncientPrayer.SOUL_SPLIT;
        if (soulSplit.getProjectile() != null)
            World.sendSoulsplitProjectile(attacker, target, soulSplit.getProjectile().getId());
        int healAmount = (int)(damage * soulSplit.getHealPercentage());
        if (healAmount > 0 && attacker.getHitpoints() < attacker.getMaxHitpoints()) {
            attacker.heal(healAmount, true, true);
        }
        if (target instanceof Player targetPlayer) {
            int prayerDrain = (int)(damage * soulSplit.getEnemyDrainPercentage());
            if (prayerDrain > 0) {
                targetPlayer.getPrayer().drainPrayer(prayerDrain);
            }
        }
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                if (!isValidForSoulSplitEffect(attacker, target)) return;

                attacker.gfx(soulSplit.getGraphic());
                if (soulSplit.getProjectile() != null)
                    World.sendSoulsplitProjectile(target, attacker, soulSplit.getProjectile().getId());
            }
        }, 1);//TODO base this by distance and projectile
    }

    private static boolean isValidForSoulSplitEffect(Player attacker, Entity target) {
        return attacker != null && target != null &&
                !attacker.isDead() && !attacker.hasFinished() &&
                !target.isDead() && !target.hasFinished();
    }

    private static void handleLeechEffects(Player attacker, Entity defender) {
        List<AncientPrayer> activeLeeches = getActiveLeechPrayers(attacker);
        if (activeLeeches.isEmpty()) return;

        for (AncientPrayer activeLeech : activeLeeches) {
            if (Utils.roll(1, 4)) {
                playLeechEffects(attacker, defender, activeLeech);
                applyLeechDrain(attacker, defender, activeLeech);
            }
        }
    }

    private static List<AncientPrayer> getActiveLeechPrayers(Player player) {
        return Arrays.stream(AncientPrayer.values())
                .filter(p -> player.getPrayer().isActive(p))
                .filter(p -> p.getName().startsWith("Leech"))
                .collect(Collectors.toList());
    }

    private static void playLeechEffects(Player attacker, Entity target, AncientPrayer prayer) {
        attacker.setNextAnimation(Rscm.lookup("animation.curses_leech"));
        target.gfx(prayer.getGraphic());
        World.sendLeechProjectile(attacker, target, prayer.getProjectile().getId());
        String statAffected = prayer.getName().replace("Leech ", "");
        attacker.getPackets().sendGameMessage("Your curse drains " + statAffected + " from the enemy, boosting your " + statAffected, true);
        if (target instanceof Player p2)
            p2.getPackets().sendGameMessage("Your " + statAffected + " has been drained by an enemy curse.", true);
    }

    private static void applyLeechDrain(Player attacker, Entity target, AncientPrayer prayer) {
        if (prayer.getLeechBonusIndex() == -1) {
            handleSpecialLeechCases(attacker, target, prayer);
            return;
        }

        PrayerBook attackerPrayer = attacker.getPrayer();

        if (attackerPrayer.reachedMax(prayer.getLeechBonusIndex()) || attackerPrayer.reachedMin(prayer.getLeechBonusIndex())) {
            attacker.getPackets().sendGameMessage("You are boosted so much that your leech curse has no effect.", true);
            return;
        }

        attackerPrayer.increaseLeechBonus(prayer.getLeechBonusIndex());
        if (target instanceof Player p2) {
            PrayerBook targetPrayer = p2.getPrayer();
            targetPrayer.decreaseLeechBonus(prayer.getLeechBonusIndex());
        }
    }

    private static void handleSpecialLeechCases(Player attacker, Entity target, AncientPrayer prayer) {
        if (target instanceof Player defender) {
            if (prayer == AncientPrayer.LEECH_ENERGY) {
                handleRunEnergyDrain(attacker, defender);
            } else if (prayer == AncientPrayer.LEECH_SPECIAL) {
                handleSpecialAttackDrain(attacker, defender);
            }
        }
    }

    private static void handleRunEnergyDrain(Player attacker, Player target) {
        if (target.getRunEnergy() <= 9) {
            attacker.getPackets().sendGameMessage(
                    "Your opponent has been weakened so much that your leech curse has no effect.", true);
            return;
        }
        attacker.setRunEnergy(Math.min(100, attacker.getRunEnergy() + 10));
        target.setRunEnergy(Math.max(0, target.getRunEnergy() - 10));
    }

    private static void handleSpecialAttackDrain(Player attacker, Player target) {
        CombatDefinitions targetCombat = target.getCombatDefinitions();
        if (targetCombat.getSpecialAttackPercentage() <= 0) {
            attacker.getPackets().sendGameMessage(
                    "Your opponent has been weakened so much that your leech curse has no effect.", true);
            return;
        }
        attacker.getCombatDefinitions().increaseSpecialAttack(10);
        targetCombat.decrease(Math.min(10, targetCombat.getSpecialAttackPercentage()));
    }

    private static void handleTurmoilEffects(Player player, Entity target) {
        if (!player.getPrayer().isActive(AncientPrayer.TURMOIL)) return;

        player.getPrayer().updateTurmoilBonus(target);
    }

    private static Prayer getProtectionPrayer(Player player, HitLook hitType) {
        if (player.getPrayer().isAncientCurses()) {
            return getDeflectPrayer(hitType);
        }
        return switch (hitType) {
            case MAGIC_DAMAGE -> NormalPrayer.PROTECT_FROM_MAGIC;
            case RANGE_DAMAGE -> NormalPrayer.PROTECT_FROM_MISSILES;
            case MELEE_DAMAGE -> NormalPrayer.PROTECT_FROM_MELEE;
            default -> null;
        };
    }

    private static AncientPrayer getDeflectPrayer(HitLook hitType) {
        return switch (hitType) {
            case MAGIC_DAMAGE -> AncientPrayer.DEFLECT_MAGIC;
            case RANGE_DAMAGE -> AncientPrayer.DEFLECT_MISSILES;
            case MELEE_DAMAGE -> AncientPrayer.DEFLECT_MELEE;
            default -> null;
        };
    }
}