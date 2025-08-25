package com.rs.java.game.player.prayer;

import com.rs.core.thread.CoresManager;
import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.rs.java.game.Hit.HitLook;

public class PrayerEffectHandler {

    public static void handleProtectionEffects(Entity source, Entity target, Hit hit) {
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
        if (reflectDamage > 0) {
            attacker.applyHit(new Hit(defender, reflectDamage, HitLook.REFLECTED_DAMAGE));
            defender.gfx(deflectPrayer.getGraphic());
            CoresManager.getSlowExecutor().schedule(() -> {
                defender.animate(deflectPrayer.getAnimation());
            }, 60, TimeUnit.MILLISECONDS);
        }
    }

    public static void handleOffensiveEffects(Player attacker, Entity target, Hit hit) {
        if (attacker.getPrayer().isActive(NormalPrayer.SMITE) && target instanceof Player) {
            handleSmiteEffect((Player)target, hit);
        }

        if (attacker.getPrayer().isAncientCurses()) {
            handleTurmoilEffects(attacker, target);
            if (attacker.getPrayer().isActive(AncientPrayer.SOUL_SPLIT)) {
                handleSoulSplitEffect(attacker, target, hit);
            }
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    handleLeechEffects(attacker, target);
                }
            });
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
        ProjectileManager.sendSimple(Projectile.SOULSPLIT, soulSplit.getProjectile().getId(), attacker, target);
        int healAmount = (int)(damage * soulSplit.getHealPercentage());
        if (healAmount > 0 && attacker.getHitpoints() < attacker.getMaxHitpoints()) {
            attacker.heal(healAmount, true, false);
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

                target.gfx(soulSplit.getGraphic());
                if (soulSplit.getProjectile() != null)
                    ProjectileManager.sendSimple(Projectile.SOULSPLIT, soulSplit.getProjectile().getId(), target, attacker);
            }
        }, Utils.getDistance(attacker, target) > 2 ? 2 : 1);
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
                if (activeLeech.getLeechBonusIndex() == -1) {
                    handleSpecialLeechCases(attacker, defender, activeLeech);
                    return;
                }
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
        attacker.animateNoCheck(new Animation(Rscm.lookup("animation.curses_leech")));
        ProjectileManager.sendWithGraphic(Projectile.LEECH, prayer.getProjectile().getId(), attacker, target, prayer.getGraphic());
        String statAffected = prayer.getName().replace("Leech ", "");
        attacker.getPackets().sendGameMessage("Your curse drains " + statAffected + " from the enemy, boosting your " + statAffected + ".", true);
        if (target instanceof Player p2) {
            p2.getPackets().sendGameMessage("Your " + statAffected + " has been drained by an enemy curse.", true);
        }
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
            attacker.animateNoCheck(new Animation(Rscm.lookup("animation.curses_leech")));
            World.sendLeechProjectile(attacker, defender, prayer.getProjectile().getId());
            ProjectileManager.sendSimple(Projectile.LEECH, prayer.getProjectile().getId(), attacker, defender);
            if (prayer == AncientPrayer.LEECH_ENERGY) {
                handleRunEnergyDrain(attacker, defender, prayer);
            } else if (prayer == AncientPrayer.LEECH_SPECIAL) {
                handleSpecialAttackDrain(attacker, defender, prayer);
            }
        }
    }

    private static void handleRunEnergyDrain(Player attacker, Player defender, AncientPrayer prayer) {
        int defenderEnergy = defender.getRunEnergy();
        if (defenderEnergy <= 0) {
            attacker.message(
                    "Your opponent has too little run energy for your curse to take effect.", true);
            return;
        }
        int maxLeech = 10, maxEnergy = 100;
        int leechedFromDefender = Math.min(maxLeech, defenderEnergy);
        int attackerCanGain = maxEnergy - attacker.getRunEnergy();
        int actualLeech = Math.min(leechedFromDefender, attackerCanGain);

        attacker.message("You leech some run energy from your enemy.", true);
        if (actualLeech > 0) {
            attacker.setRunEnergy(attacker.getRunEnergy() + actualLeech);
        }
        defender.setRunEnergy(defenderEnergy - leechedFromDefender);
        defender.message("Your run energy has been drained by an enemy curse!", true);

        int delay = Utils.getDistance(attacker, defender) > 2 ? 2 : 1;
        defender.delayGfx(prayer.getGraphic(), delay);
    }

    private static void handleSpecialAttackDrain(Player attacker, Player defender, AncientPrayer prayer) {
        CombatDefinitions attackerCombat = attacker.getCombatDefinitions();
        CombatDefinitions defenderCombat = defender.getCombatDefinitions();
        int defenderEnergy = defenderCombat.getSpecialAttackPercentage();
        int attackerEnergy = attackerCombat.getSpecialAttackPercentage();
        if (defenderEnergy <= 0) {
            attacker.message(
                    "Your opponent has too little Special Attack energy for your curse to take effect.", true);
            return;
        }
        int leechedAmount = Math.min(10, defenderEnergy);
        int actualLeech = Math.min(leechedAmount, 100 - attackerEnergy);
        attackerCombat.setSpecialAttack(attackerEnergy + actualLeech);
        attacker.message("You leech some special attack from your enemy.", true);

        defenderCombat.setSpecialAttack(defenderEnergy - actualLeech);
        defender.message("Your Special Attack energy has been drained by an enemy curse!", true);

        int delay = Utils.getDistance(attacker, defender) > 2 ? 2 : 1;
        defender.delayGfx(prayer.getGraphic(), delay);
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