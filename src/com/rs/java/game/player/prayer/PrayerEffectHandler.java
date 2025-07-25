package com.rs.java.game.player.prayer;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

import java.util.Map;

import static com.rs.java.game.Hit.HitLook;

public class PrayerEffectHandler {

    private static final int SOUL_SPLIT_PROJECTILE = 2263;
    private static final int SOUL_SPLIT_GFX = 2264;
    private static final double HEAL_PERCENTAGE = 0.20; // 20% of damage
    private static final double PRAYER_DRAIN_PERCENTAGE = 0.20; // 20% of damage

    // Leech effect data
    private static final Map<AncientPrayer, LeechData> LEECH_DATA = Map.of(
            AncientPrayer.LEECH_ATTACK, new LeechData(0, 2231, 2232, "Attack"),
            AncientPrayer.LEECH_STRENGTH, new LeechData(1, 2248, 2250, "Strength"),
            AncientPrayer.LEECH_DEFENCE, new LeechData(2, 2244, 2246, "Defence"),
            AncientPrayer.LEECH_RANGED, new LeechData(3, 2236, 2238, "Ranged"),
            AncientPrayer.LEECH_MAGIC, new LeechData(4, 2240, 2242, "Magic"),
            AncientPrayer.LEECH_ENERGY, new LeechData(-1, 2252, 2254, "Run energy"),
            AncientPrayer.LEECH_SPECIAL, new LeechData(-1, 2256, 2258, "Special attack")
    );

    private record LeechData(int statIndex, int projectileId, int gfxId, String message) {}

    public static void handleAllPrayerEffects(Entity source, Entity target, Hit hit) {
        if (source == null || target == null) return;

        if (hit != null && hit.getDamage() > 0) {
            if (target instanceof Player playerTarget) {
                handleProtectionEffects(playerTarget, source, hit);
            }
        }
        if (target instanceof Player playerTarget) {
            handleOffensiveEffects(playerTarget, target, hit);
            handleTurmoilEffects(playerTarget, target);
        }
    }

    private static void handleProtectionEffects(Player defender, Entity attacker, Hit hit) {
        if (!defender.getPrayer().hasActivePrayers()) return;

        Prayer protectionPrayer = getProtectionPrayer(defender, hit.getLook());
        if (protectionPrayer == null) return;

        // Apply damage reduction (same for PvP and NPC attacks)
        double reduction = 1.0 - protectionPrayer.getDamageReduction();
        hit.setDamage((int)(hit.getDamage() * reduction));

        // Handle deflect effects (works against both Players and NPCs)
        if (protectionPrayer instanceof AncientPrayer && protectionPrayer.isDeflectPrayer()) {
            handleDeflectEffect(defender, attacker, hit, (AncientPrayer)protectionPrayer);
        }
    }

    private static void handleDeflectEffect(Player defender, Entity attacker, Hit hit, AncientPrayer deflectPrayer) {
        if (Utils.randomDouble() >= deflectPrayer.getReflectChance()) return;

        int reflectDamage = (int)(hit.getDamage() * deflectPrayer.getReflectAmount());
        attacker.applyHit(new Hit(defender, reflectDamage, HitLook.REFLECTED_DAMAGE));
        defender.gfx(deflectPrayer.getActivationGraphics());
        defender.animate(deflectPrayer.getActivationAnimation());
    }

    private static void handleOffensiveEffects(Player attacker, Entity target, Hit hit) {
        if (attacker.getPrayer().isActive(NormalPrayer.SMITE) && target instanceof Player) {
            handleSmiteEffect(attacker, (Player)target, hit);
        }

        if (attacker.getPrayer().isAncientCurses()) {
            if (attacker.getPrayer().isActive(AncientPrayer.SOUL_SPLIT)) {
                handleSoulSplitEffect(attacker, target, hit);
            } else {
                handleLeechEffects(attacker, target);
            }
        }
    }

    private static void handleSmiteEffect(Player attacker, Player target, Hit hit) {
        int damage = Math.min(hit.getDamage(), target.getHitpoints());
        int prayerDrain = damage / 4; // 25% of damage as prayer drain
        if (prayerDrain > 0) {
            target.getPrayer().drainPrayer(prayerDrain);
        }
    }

    private static void handleSoulSplitEffect(Player attacker, Entity target, Hit hit) {
        int damage = Math.min(hit.getDamage(), target.getHitpoints());
        if (damage <= 0) return;

        // Initial projectile
        World.sendSoulsplitProjectile(attacker, target, SOUL_SPLIT_PROJECTILE);

        // Calculate and apply healing
        int healAmount = (int)(damage * HEAL_PERCENTAGE);
        if (healAmount > 0 && attacker.getHitpoints() < attacker.getMaxHitpoints()) {
            attacker.heal(healAmount, true, true);
        }

        // Drain prayer from player targets
        if (target instanceof Player targetPlayer) {
            int prayerDrain = (int)(damage * PRAYER_DRAIN_PERCENTAGE);
            if (prayerDrain > 0) {
                targetPlayer.getPrayer().drainPrayer(prayerDrain);
            }
        }

        // Delayed return projectile effect
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                if (!isValidForSoulSplitEffect(attacker, target)) return;

                target.gfx(new Graphics(SOUL_SPLIT_GFX));
                World.sendSoulsplitProjectile(target, attacker, SOUL_SPLIT_PROJECTILE);
            }
        }, 1);
    }

    private static boolean isValidForSoulSplitEffect(Player attacker, Entity target) {
        return attacker != null && target != null &&
                !attacker.isDead() && !attacker.hasFinished() &&
                !target.isDead() && !target.hasFinished();
    }

    private static void handleLeechEffects(Player attacker, Entity target) {
        if (!(target instanceof Player p2)) return;
        if (Utils.getRandom(4) != 0) return; // 25% chance

        AncientPrayer activeLeech = getActiveLeechPrayer(attacker);
        if (activeLeech == null) return;

        LeechData data = LEECH_DATA.get(activeLeech);
        playLeechEffects(attacker, p2, data);
        applyLeechDrain(attacker, p2, activeLeech, data);
    }

    private static AncientPrayer getActiveLeechPrayer(Player player) {
        return LEECH_DATA.keySet().stream()
                .filter(player.getPrayer()::isActive)
                .findFirst()
                .orElse(null);
    }

    private static void playLeechEffects(Player attacker, Player target, LeechData data) {
        attacker.setNextAnimation(12575);
        target.gfx(new Graphics(data.gfxId()));
        World.sendLeechProjectile(attacker, target, data.projectileId());

        attacker.getPackets().sendGameMessage(
                "Your curse drains " + data.message() + " from the enemy, boosting your " + data.message(), true);
        target.getPackets().sendGameMessage("Your " + data.message() + " has been drained by an enemy curse.", true);
    }

    private static void applyLeechDrain(Player attacker, Player target, AncientPrayer prayer, LeechData data) {
        if (data.statIndex() == -1) {
            handleSpecialLeechCases(attacker, target, prayer);
            return;
        }

        PrayerBook attackerPrayer = attacker.getPrayer();
        PrayerBook targetPrayer = target.getPrayer();

        if (attackerPrayer.reachedMax(data.statIndex()) || attackerPrayer.reachedMin(data.statIndex())) {
            attacker.getPackets().sendGameMessage(
                    "You are boosted so much that your leech curse has no effect.", true);
            return;
        }

        attackerPrayer.increaseLeechBonus(data.statIndex());
        targetPrayer.decreaseLeechBonus(data.statIndex());
    }

    private static void handleSpecialLeechCases(Player attacker, Player target, AncientPrayer prayer) {
        if (prayer == AncientPrayer.LEECH_ENERGY) {
            handleRunEnergyDrain(attacker, target);
        } else if (prayer == AncientPrayer.LEECH_SPECIAL) {
            handleSpecialAttackDrain(attacker, target);
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
                    "You are boosted so much that your leech curse has no effect.", true);
            return;
        }
        attacker.getCombatDefinitions().increaseSpecialAttack(10);
        targetCombat.decrease(Math.min(10, targetCombat.getSpecialAttackPercentage()));
    }

    private static void handleTurmoilEffects(Player player, Entity target) {
        if (!player.getPrayer().isActive(AncientPrayer.TURMOIL)) return;
        if (player.getPrayer().isBoostedLeech()) return;

        if (target instanceof Player p2) {
            //TODO player.getPrayer().increaseTurmoilBonus(player, p2);
        } else if (target instanceof NPC) {
            //TODO player.getPrayer().increaseTurmoilBonusNPC(player);
        }
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