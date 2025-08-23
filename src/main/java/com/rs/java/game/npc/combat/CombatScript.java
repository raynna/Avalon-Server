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
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.prayer.PrayerEffectHandler;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.player.combat.CombatAction;

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
        Hit damage = attacker.meleeHit(defender, attacker.getMaxHit());
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
        Hit hit = new Hit(attacker, damage.getDamage(), look);
        delayHit(attacker, defender, delay, hit);
    }

    public static void delayHit(NPC npc, final Entity target, int delay, final Hit... hits) {
        for (Hit hit : hits) {
            if (target instanceof Player p2)
                p2.handleIncommingHit(hit);
            int attackSpeed = npc.getAttackSpeed() * 600;
            npc.setAttackDelay((attackSpeed / 2) + 4800);
            if (target instanceof Player playerTarget) {
                PrayerEffectHandler.handleProtectionEffects(npc, playerTarget, hit);
            }
            handleAbsorb(target, hit);
            handleStaffOfLightReduction(target, hit);
            handleDivine(target, hit);
            handleElysian(target, hit);
            if (npc.getId() == 13448) sendSoulSplit(hit, npc, target);
            if (npc.getId() == 2027) {
                if (hit.getDamage() != 0 && Utils.random(3) == 0) {
                    target.gfx(new Graphics(398));
                    npc.heal(hit.getDamage());
                }
            }
            if (npc.getId() == 6367) {
                if (hit.getLook() == HitLook.MAGIC_DAMAGE && hit.getDamage() > 0) target.addFreezeDelay(20000, false);
            }
        }
        WorldTasksManager.schedule(new WorldTask() {

            @Override
            public void run() {
                for (Hit hit : hits) {
                    NPC npc = (NPC) hit.getSource();
                    if (npc.isDead() || npc.hasFinished() || target.isDead() || target.hasFinished()) return;
                    target.applyHit(hit);
                    if (target instanceof Player defender) {
                        defender.getChargeManager().processHit(hit);
                    }
                    handleRingOfRecoil(npc, target, hit);
                    handleVengHit(target, hit);
                    if (npc.getId() >= 912 && npc.getId() <= 914) {
                        if (hit.getDamage() == 0) target.gfx(new Graphics(85, 0, 96));
                        else target.gfx(new Graphics(npc.getCombatDefinitions().getAttackProjectile(), 0, 0));
                    }
                    if (npc.getId() == 6367) {
                        if (hit.getDamage() == 0) target.gfx(new Graphics(85, 0, 96));
                        if (hit.getDamage() > 0 && target.isFrozen())
                            target.gfx(new Graphics(1677));
                        if (hit.getDamage() > 0 && !target.isFrozen())
                            target.gfx(new Graphics(369));
                    }
                    if (npc.getId() == 1007 && hit.getLook() == HitLook.MAGIC_DAMAGE) {
                        if (hit.getDamage() == 0) target.gfx(new Graphics(85, 0, 96));
                        if (hit.getDamage() > 0) target.gfx(new Graphics(78, 0, 0));
                    }
                    if (npc.getId() == 1264 && hit.getLook() == HitLook.MAGIC_DAMAGE) {
                        if (hit.getDamage() == 0) target.gfx(new Graphics(85, 0, 96));
                        if (hit.getDamage() > 0) target.gfx(new Graphics(76, 0, 0));
                    }
                    if (hit.getDamage() == 0) {
                        if (npc.getId() == 9172)// aquanite splash
                            target.gfx(new Graphics(2122));
                    }
                    npc.getCombat().doDefenceEmote(target);
                    if (target instanceof Player player) {
                        player.closeInterfaces();
                        if (player.getCombatDefinitions().isAutoRelatie() && !player.getActionManager().hasSkillWorking() && !player.hasWalkSteps())
                            player.getNewActionManager().setAction(new CombatAction(npc));
                        if (player.familiarAutoAttack) {
                            if (player.getFamiliar() != null && !player.getFamiliar().getCombat().hasTarget() && player.isAtMultiArea()) {
                                player.getFamiliar().setTarget(npc);
                            }
                        }
                    } else {
                        NPC n = (NPC) target;
                        if (!n.isUnderCombat() || n.canBeAttackedByAutoRelatie()) n.setTarget(npc);
                    }

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

    public static void handleRingOfRecoil(NPC attacker, Entity target, Hit incomingHit) {
        final int RING_OF_RECOIL_ID = 2550;
        final int MIN_DAMAGE_FOR_RECOIL = 10;
        final int MAX_RECOIL_DAMAGE = 60;
        final double RECOIL_DAMAGE_PERCENT = 0.1;

        if (attacker == null || target == null || incomingHit == null) {
            return;
        }

        HitLook hitType = incomingHit.getLook();
        if (hitType != HitLook.MELEE_DAMAGE &&
                hitType != HitLook.RANGE_DAMAGE &&
                hitType != HitLook.MAGIC_DAMAGE) {
            return;
        }

        if (incomingHit.getDamage() < MIN_DAMAGE_FOR_RECOIL) {
            return;
        }

        if (target instanceof Player player) {
            // Check if wearing ring
            int ringSlot = Equipment.SLOT_RING;
            Item ring = player.getEquipment().getItem(ringSlot);
            if (ring == null || ring.getId() != RING_OF_RECOIL_ID) {
                return;
            }

            // Get or initialize charges
            int remainingCharges = player.getChargeManager().getCharges(RING_OF_RECOIL_ID);
            if (remainingCharges <= 0) {
                degradeRing(player, ringSlot, ring); // Handle ring breaking
                return;
            }

            // Calculate recoil damage
            int recoilDamage = (int) (incomingHit.getDamage() * RECOIL_DAMAGE_PERCENT);
            recoilDamage = Math.min(recoilDamage, MAX_RECOIL_DAMAGE);
            recoilDamage = Math.min(recoilDamage, remainingCharges);

            if (recoilDamage > 0) {
                // Apply recoil damage
                Hit recoilHit = new Hit(player, recoilDamage, HitLook.REFLECTED_DAMAGE);
                attacker.applyHit(recoilHit);

                // Update charges
                int newCharges = remainingCharges - recoilDamage;
                player.getChargeManager().setCharges(RING_OF_RECOIL_ID, newCharges);

                // Break ring if no charges left
                if (newCharges <= 0) {
                    degradeRing(player, ringSlot, ring);
                }
            }
        }
    }

    private static void degradeRing(Player player, int slot, Item ring) {
        ItemDegrade.DegradeData data = player.getChargeManager().getDegradeData(ring.getId());
        if (data == null) return;

        Item nextItem = null;
        if (data.getDegradedItem() == null && data.getBrokenItem() != null) {
            nextItem = data.getBrokenItem();
        }
        if (data.getDegradedItem() != null) {
            if (ring.getId() != data.getDegradedItem().getId()) {
                nextItem = data.getDegradedItem();
            }
        }

        String ringName = ItemDefinitions.getItemDefinitions(ring.getId()).getName();
        if (nextItem != null) {
            player.message("Your " + ringName + " has degraded.");
        } else {
            player.message("Your " + ringName + " turned into dust.");
        }

        // Remove the ring or replace with degraded version
        player.getEquipment().getItems().set(slot, nextItem);
        player.getEquipment().refresh(slot);
        player.getAppearence().generateAppearenceData();

        // Clear charges
        player.getChargeManager().resetCharges(ring.getId());
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

    public static int getRandomMaxHit(NPC npc, int maxHit, int attackStyle, Entity target) {
        int[] bonuses = npc.getBonuses();
        double mage = 0;
        double M = 0;
        double mageBonus = 0;
        double attack = 0;
        double A = 0;
        double attackBonus = 0;
        double range = 0;
        double R = 0;
        double rangeBonus = 0;
        npc.setBonuses();
        if (attackStyle == NPCCombatDefinitions.MAGE) {
            mageBonus = bonuses != null ? bonuses[CombatDefinitions.NPC_MAGIC_BONUS] : 0;
            mage = bonuses != null ? bonuses[CombatDefinitions.NPC_MAGIC_LEVEL] * 1.5 : npc.getCombatLevel();
            mage = Math.round(mage);
            mage += 8;
            M = Math.round(mage);
            mage = mage * (1 + mageBonus / 64);
            M = Math.round(mage);
        } else if (attackStyle == NPCCombatDefinitions.RANGE) {
            rangeBonus = (bonuses != null ? bonuses[CombatDefinitions.NPC_RANGE_BONUS] : 0);
            range = bonuses != null ? bonuses[CombatDefinitions.NPC_RANGE_LEVEL] * 1.5 : npc.getCombatLevel();
            range = Math.round(range);
            range += 8;
            R = Math.round(range);
            range = range * (1 + rangeBonus / 64);
            R = Math.round(range);
        } else {
            attackBonus = npc.getCombatDefinitions().getAttackType() == NPCCombatDefinitions.STAB ? bonuses != null ? bonuses[CombatDefinitions.NPC_STAB_BONUS] : 0 : npc.getCombatDefinitions().getAttackType() == NPCCombatDefinitions.SLASH ? bonuses != null ? bonuses[CombatDefinitions.NPC_SLASH_BONUS] : 0 : bonuses != null ? bonuses[CombatDefinitions.NPC_CRUSH_BONUS] : 0;
            attack += bonuses != null ? bonuses[CombatDefinitions.NPC_ATTACK_LEVEL] * 1.5 : npc.getCombatLevel();
            attack = Math.round(attack);
            attack += 8;
            attack = attack * (1 + attackBonus / 64);
            A = Math.round(attack);
        }
        double defence = 0;
        double D = 0;
        double rangedefence = 0;
        double RD = 0;
        double magedefence = 0;
        double MD = 0;
        if (target instanceof Player) {
            Player p2 = (Player) target;
            double defenceBonus = (p2.getCombatDefinitions().getBonuses()[attackStyle == NPCCombatDefinitions.RANGE ? CombatDefinitions.RANGE_DEF : CombatDefinitions.MAGIC_DEF]);
            double meleeBonus = (p2.getCombatDefinitions().getBonuses()[npc.getCombatDefinitions().getAttackType() == NPCCombatDefinitions.STAB ? CombatDefinitions.STAB_DEF : npc.getCombatDefinitions().getAttackType() == NPCCombatDefinitions.SLASH ? CombatDefinitions.SLASH_DEF : CombatDefinitions.CRUSH_DEF]);
            if (attackStyle == NPCCombatDefinitions.MAGE) {
                magedefence = (p2.getSkills().getLevel(Skills.DEFENCE) * 0.3) + (p2.getSkills().getLevel(Skills.MAGIC) * 0.7);
                magedefence = Math.round(magedefence);
                magedefence += 8;
                magedefence = magedefence * (1 + defenceBonus / 64);
                MD = Math.round(magedefence);
            } else if (attackStyle == NPCCombatDefinitions.RANGE) {
                rangedefence = p2.getSkills().getLevel(Skills.DEFENCE);
                rangedefence *= p2.getPrayer().getDefenceMultiplier();
                rangedefence = Math.round(rangedefence);
                rangedefence += 8;
                rangedefence = rangedefence * (1 + defenceBonus / 64);
                RD = Math.round(rangedefence);
            } else {
                defence = p2.getSkills().getLevel(Skills.DEFENCE);
                defence *= p2.getPrayer().getDefenceMultiplier();
                defence = Math.round(defence);
                defence += 8;
                defence = defence * (1 + meleeBonus / 64);
                D = Math.round(defence);
            }
        } else if (target instanceof NPC || target instanceof Familiar) {
            NPC n = (NPC) target;
            defence = n.getBonuses()[attackStyle == NPCCombatDefinitions.RANGE ? CombatDefinitions.RANGE_DEF : attackStyle == NPCCombatDefinitions.MAGE ? CombatDefinitions.MAGIC_DEF : CombatDefinitions.STAB_DEF];
            double defenceBonus = (attackStyle == NPCCombatDefinitions.RANGE ? n.getBonuses()[CombatDefinitions.NPC_RANGE_BONUS] : attackStyle == NPCCombatDefinitions.MAGE ? n.getBonuses()[CombatDefinitions.NPC_MAGIC_BONUS] : n.getBonuses()[CombatDefinitions.NPC_STAB_BONUS]);
            if (attackStyle == NPCCombatDefinitions.MAGE) {
                magedefence = n.getBonuses()[CombatDefinitions.NPC_DEFENCE_LEVEL];
                magedefence = Math.round(magedefence);
                magedefence += 8;
                magedefence = magedefence * (1 + defenceBonus);
                MD = Math.round(magedefence);
            } else if (attackStyle == NPCCombatDefinitions.RANGE) {
                rangedefence = n.getBonuses()[CombatDefinitions.NPC_DEFENCE_LEVEL];
                rangedefence = Math.round(rangedefence);
                rangedefence += 8;
                rangedefence = rangedefence * (1 + defenceBonus);
                RD = Math.round(rangedefence);
            } else {
                defence = n.getBonuses()[CombatDefinitions.NPC_DEFENCE_LEVEL];
                defence = Math.round(defence);
                defence += 8;
                defence = defence * (1 + defenceBonus);
                D = Math.round(defence);
            }
        }
        if (attackStyle == NPCCombatDefinitions.MAGE) {
            double prob = M / MD;
            double random = Utils.getRandomDouble(100);
            if (M <= MD) prob = (M - 1) / (MD * 2);
            else if (M > MD) prob = 1 - (MD + 1) / (M * 2);
            if (npc.getId() == 1158 || npc.getId() == 1160) prob = 100;
            if (prob < random / 100) return 0;
        } else if (attackStyle == NPCCombatDefinitions.RANGE) {
            double prob = R / RD;
            double random = Utils.getRandomDouble(100);
            if (R <= RD) prob = (R - 1) / (RD * 2);
            else if (R > RD) prob = 1 - (RD + 1) / (R * 2);
            ;
            if (npc.getId() == 1158 || npc.getId() == 1160) prob = 100;
            if (prob < random / 100) return 0;
        } else {
            double prob = A / D;
            double random = Utils.getRandomDouble(100);
            if (A <= D) prob = (A - 1) / (D * 2);
            else if (A > D) prob = 1 - (D + 1) / (A * 2);
            if (prob < random / 100) return 0;
        }
        return Utils.getRandom(maxHit);
    }

}
