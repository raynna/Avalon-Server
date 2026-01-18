package com.rs.java.game.npc.combat;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.itemdegrading.ItemDegrade;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.*;
import com.rs.java.game.player.prayer.PrayerEffectHandler;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.AttackMethod;
import com.rs.kotlin.game.npc.combatdata.AttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.player.combat.CombatAction;
import com.rs.kotlin.game.player.combat.CombatStyle;
import com.rs.kotlin.game.player.combat.EntityUtils;
import com.rs.kotlin.game.world.projectile.ProjectileManager;
import com.rs.kotlin.game.world.pvp.PvpManager;

public abstract class CombatScript {

    /*
     * Returns ids and names
     */
    public abstract Object[] getKeys();

    /*
     * Returns Move Delay
     */
    public abstract int attack(NPC npc, Entity target);

    public static void hit(int delay, NPC attacker, Entity defender, NpcAttackStyle style) {
        Hit hit = attacker.meleeHit(defender, attacker.getMaxHit());
        HitLook look;
        switch (style) {
            case STAB: case SLASH: case CRUSH:
                look = HitLook.MELEE_DAMAGE;
                break;
            case RANGED:
                look = HitLook.RANGE_DAMAGE;
                break;
            case MAGIC:
                look = HitLook.MAGIC_DAMAGE;
                break;
            default:
                look = HitLook.REGULAR_DAMAGE;
        }
        delayHit(attacker, defender, delay, hit);
    }

    public static Hit registerHit(NPC npc, Entity target, Hit hit) {

        if (target instanceof Player player)
            player.handleIncommingHit(hit);

        if (target instanceof Player playerTarget) {
            PrayerEffectHandler.handleProtectionEffects(npc, playerTarget, hit);
            PvpManager.onPlayerDamagedByNpc(playerTarget);
        }

        handleAbsorb(target, hit);
        handleStaffOfLightReduction(target, hit);
        handleDivine(target, hit);
        handleElysian(target, hit);

        if (npc.getId() == 13448)
            sendSoulSplit(hit, npc, target);

        if (npc.getId() == 2027 && hit.getDamage() > 0 && Utils.random(3) == 0) {
            target.gfx(new Graphics(398));
            npc.heal(hit.getDamage());
        }

        if (npc.getId() == 6367 &&
                hit.getLook() == HitLook.MAGIC_DAMAGE &&
                hit.getDamage() > 0) {
            target.addFreezeDelay(20000, false);
        }
        return hit;
    }


    public static void applyRegisteredHit(NPC npc, Entity target, Hit hit) {
        if (npc.isDead() || npc.hasFinished() || target.isDead() || target.hasFinished())
            return;
        npc.getCombat().performBlockAnimation(target);
        if (hit.getLook() == HitLook.MAGIC_DAMAGE && hit.getDamage() == 0) {
            target.gfx(85, 100);//splash for npc magic
            return;
        }
        NpcCombatDefinition combatDefinitions = npc.getCombatDefinitions();
        if (combatDefinitions != null) {
            if (combatDefinitions.getAttackGfx() != -1) {
                int srcAngle = target.getDirection();

                int dstAngle = Utils.getAngle(
                        npc.getX() - target.getX(),
                        npc.getY() - target.getY()
                );
                //TODO get angle a better way
                int rel = (dstAngle - srcAngle) & 0x3FFF;
                rel = (rel + 8192) & 0x3FFF;
                int gfxRot = ((rel + 1024) / 2048) & 7;

                target.gfx(combatDefinitions.getAttackGfx(), 100, gfxRot);
            }
        }
        target.applyHit(hit);

        if (target instanceof Player defender) {
            defender.getChargeManager().processHit(hit);
            CombatStyle.handleRingOfRecoil(npc, defender, hit);
        }

        handleVengHit(target, hit);

        if (npc.getId() >= 912 && npc.getId() <= 914) {
            target.gfx(hit.getDamage() == 0
                    ? new Graphics(85, 0, 96)
                    : new Graphics(npc.getCombatDefinitions().getAttackProjectile(), 0, 0));
        }

        if (npc.getId() == 6367) {
            if (hit.getDamage() == 0)
                target.gfx(new Graphics(85, 0, 96));
            else
                target.gfx(new Graphics(target.isFrozen() ? 1677 : 369));
        }

        if (npc.getId() == 1007 && hit.getLook() == HitLook.MAGIC_DAMAGE) {
            target.gfx(hit.getDamage() == 0
                    ? new Graphics(85, 0, 96)
                    : new Graphics(78, 0, 0));
        }

        if (npc.getId() == 1264 && hit.getLook() == HitLook.MAGIC_DAMAGE) {
            target.gfx(hit.getDamage() == 0
                    ? new Graphics(85, 0, 96)
                    : new Graphics(76, 0, 0));
        }
        if (hit.getDamage() == 0 && npc.getId() == 9172) {
            target.gfx(new Graphics(2122));
        }
    }

    public static void delayHit(NPC npc, Entity target, int delay, Hit... hits) {
        npc.getTickManager().addTicks(TickManager.TickKeys.LAST_ATTACK_TICK, 10);
        target.getTickManager().addTicks(TickManager.TickKeys.LAST_ATTACKED_TICK, 10);
        target.setAttackedBy(npc);
        target.getTickManager().addTicks(TickManager.TickKeys.PJ_TIMER, 10);
        if (target instanceof Player player) {
            if (player.getCombatDefinitions().isAutoRelatie()
                    && !player.hasWalkSteps()) {

                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        if (player.isDead() || npc.isDead() || player.isLocked())
                            return;

                        if (player.getNewActionManager().hasActionWorking())
                            return;

                        player.closeInterfaces();

                        int retaliateDelay =
                                EntityUtils.getAutoRetaliateDelay(player, npc);

                        int currentDelay =
                                player.getNewActionManager().getActionDelay();

                        int finalDelay = Math.max(currentDelay, retaliateDelay);

                        player.getNewActionManager().setAction(new CombatAction(npc));
                        player.getNewActionManager().setActionDelay(finalDelay);
                    }
                }, 1);
            }
        }


        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                if (target instanceof Player player) {
                    if (player.familiarAutoAttack && player.getFamiliar() != null
                            && !player.getFamiliar().getCombat().hasTarget()
                            && player.isAtMultiArea()) {
                        player.getFamiliar().setTarget(npc);
                    }
                } else {
                    NPC n = (NPC) target;
                    if (!n.isUnderCombat() || n.canBeAttackedByAutoRelatie())
                        n.setTarget(npc);
                }
                for (Hit hit : hits) {
                    applyRegisteredHit(npc, target, hit);
                }
            }
        }, delay);
    }

    public static void sendSoulSplit(final Hit hit, final NPC npc, final Entity target) {
        Player p2 = (Player) target;
        if (target != null) {
            World.sendSoulsplitProjectile(npc, target, 2263);
            if (npc.getHitpoints() > 0 && npc.getHitpoints() <= npc.getMaxHitpoints()) {
                npc.heal(hit.getDamage() / 5);
                p2.getPrayer().drainPrayer(hit.getDamage() / 5);
            }
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    target.gfx(new Graphics(2264));
                    World.sendSoulsplitProjectile(target, npc, 2263);
                }
            }, 1);
        }
    }

    private static void handleVengHit(Entity target, Hit hit) {
        if (target instanceof NPC) {
            return;
        }
        Player p2 = (Player) target;
        if (p2.castedVeng && hit.getDamage() >= 4) {
            p2.castedVeng = false;
            p2.setNextForceTalk(new ForceTalk("Taste vengeance!"));
            hit.getSource().applyHit(new Hit(target, (int) (hit.getDamage() * 0.75), HitLook.REGULAR_DAMAGE));
        }
    }

    public static void handleAbsorb(Entity target, Hit incommingHit) {
        final int MINIMUM_DAMAGE_THRESHOLD = 200;
        final int MINIMUM_HP_THRESHOLD = 200;

        if (!(target instanceof Player player)) {
            return;
        }
        HitLook hitType = incommingHit.getLook();
        if (hitType != HitLook.MELEE_DAMAGE &&
                hitType != HitLook.RANGE_DAMAGE &&
                hitType != HitLook.MAGIC_DAMAGE) {
            return;
        }
        int absorptionBonus = getAbsorptionBonus(player, hitType);
        int reducibleDamage = incommingHit.getDamage() - MINIMUM_DAMAGE_THRESHOLD;
        int reducedDamage = (reducibleDamage * absorptionBonus) / 100;
        if (absorptionBonus == 0 || reducibleDamage <= 0 || reducedDamage <= 0) {
            return;
        }
        if (player.getHitpoints() <= MINIMUM_HP_THRESHOLD) {
            return;
        }
        incommingHit.setDamage(incommingHit.getDamage() - reducedDamage);
        incommingHit.setSoaking(new Hit(target, reducedDamage, HitLook.ABSORB_DAMAGE));
    }

    private static int getAbsorptionBonus(Player player, HitLook hitType) {
        CombatDefinitions combatDefs = player.getCombatDefinitions();

        return switch (hitType) {
            case MELEE_DAMAGE -> combatDefs.getBonuses()[CombatDefinitions.ABSORVE_MELEE_BONUS];
            case RANGE_DAMAGE -> combatDefs.getBonuses()[CombatDefinitions.ABSORVE_RANGE_BONUS];
            case MAGIC_DAMAGE -> combatDefs.getBonuses()[CombatDefinitions.ABSORVE_MAGE_BONUS];
            default -> 0;
        };
    }

    public static void handleDivine(Entity target, Hit incommingHit) {
        final int DIVINE_SHIELD_ID = 13740;
        final double DAMAGE_REDUCTION_MULTIPLIER = 0.7;
        final double PRAYER_DRAIN_PERCENT = 0.3;
        final int PRAYER_DRAIN_DIVISOR = 2;
        if (!(target instanceof Player player)) {
            return;
        }
        if (player.getEquipment().getShieldId() != DIVINE_SHIELD_ID) {
            return;
        }
        int prayerDrain = (int) Math.ceil(incommingHit.getDamage() * PRAYER_DRAIN_PERCENT) / PRAYER_DRAIN_DIVISOR;
        if (player.getPrayer().getPrayerPoints() < prayerDrain) {
            return;
        }
        incommingHit.setDamage((int) (incommingHit.getDamage() * DAMAGE_REDUCTION_MULTIPLIER));
        player.getPrayer().drainPrayer(prayerDrain);
        player.gfx(93, 0);
    }

    public static void handleElysian(Entity target, Hit incommingHit) {
        final int ELYSIAN_SHIELD_ID = 13742;
        final double DAMAGE_REDUCTION_MULTIPLIER = 0.75;
        final int CHANCE_NUMERATOR = 7;
        final int CHANCE_DENOMINATOR = 10;
        if (!(target instanceof Player player)) {
            return;
        }
        if (player.getEquipment().getShieldId() != ELYSIAN_SHIELD_ID) {
            return;
        }
        if (Utils.getRandom(CHANCE_DENOMINATOR) < CHANCE_NUMERATOR) {
            incommingHit.setDamage((int) (incommingHit.getDamage() * DAMAGE_REDUCTION_MULTIPLIER));
            player.gfx(93, 0);
        }
    }

    public static void handleStaffOfLightReduction(Entity target, Hit hit) {
        final int STAFF_OF_LIGHT_ID = 15486;
        final double DAMAGE_REDUCTION_MULTIPLIER = 0.5;
        final int POL_GFX_ID = 2320;
        final int POL_GFX_HEIGHT = 100;
        if (!(target instanceof Player player)) {
            return;
        }
        if (hit.getLook() != HitLook.MELEE_DAMAGE) {
            return;
        }
        if (player.staffOfLightSpecial <= Utils.currentTimeMillis()) {
            player.setStaffOfLightSpecial(0);
            return;
        }
        if (player.getEquipment().getWeaponId() != STAFF_OF_LIGHT_ID) {
            player.setStaffOfLightSpecial(0);
            return;
        }
        player.gfx(new Graphics(POL_GFX_ID, 0, POL_GFX_HEIGHT));
        hit.setDamage((int) (hit.getDamage() * DAMAGE_REDUCTION_MULTIPLIER));
    }

    public static Hit getRangeHit(NPC npc, int damage) {
        return new Hit(npc, damage, HitLook.RANGE_DAMAGE);
    }

    public static Hit getMagicHit(NPC npc, int damage) {
        return new Hit(npc, damage, HitLook.MAGIC_DAMAGE);
    }

    public static Hit getRegularHit(NPC npc, int damage) {
        return new Hit(npc, damage, HitLook.REGULAR_DAMAGE);
    }

    public static Hit getMeleeHit(NPC npc, int damage) {
        return new Hit(npc, damage, HitLook.MELEE_DAMAGE);
    }

}
