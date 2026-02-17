package com.rs.java.game.npc.combat;

import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.CombatData;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.player.equipment.BonusType;

public class NpcCombatCalculations {


    public static boolean getAccuracyRoll(NPC npc, NpcAttackStyle attackStyle, Entity target) {
        CombatData data = npc.getCombatData();
        if (npc.getName().toLowerCase().contains("kalphite queen")) {//kq quaranteed hit with range&magic
            if (attackStyle == NpcAttackStyle.MAGIC || attackStyle == NpcAttackStyle.RANGED)
                return true;
        }
        int attackRoll = calculateAttackRoll(npc, attackStyle, data);
        int defenceRoll = calculateDefenceRoll(attackStyle, target);
        return calculateHitProbability(attackRoll, defenceRoll);
    }

    public static int getRandomMaxHit(NPC npc, int maxHit, NpcAttackStyle attackStyle, Entity target) {
        CombatData data = npc.getCombatData();
        int finalMaxHit = ceilToNextTenIfEnabled(target, maxHit);
        if (npc.getName().toLowerCase().contains("kalphite queen")) {//kq quaranteed hit with range&magic
            if (attackStyle == NpcAttackStyle.MAGIC || attackStyle == NpcAttackStyle.RANGED)
                return Utils.getRandom(finalMaxHit);
        }
        int attackRoll = calculateAttackRoll(npc, attackStyle, data);
        int defenceRoll = calculateDefenceRoll(attackStyle, target);
        System.out.println("attackRoll: " + attackRoll + ", defenceRoll: " + defenceRoll);
        if (target instanceof Player player) {
            double hitChance = calculateHitChance(attackRoll, defenceRoll);
            double percent = hitChance * 100.0;

            System.out.println("========== NPC Combat Debug ==========");
            System.out.println("NPC: " + npc.getName());
            System.out.println("Attack Style: " + attackStyle);
            System.out.println("Attack Roll: " + String.format("%,d", attackRoll));
            System.out.println("Defence Roll: " + String.format("%,d", defenceRoll));
            System.out.println("Hit Chance: " + String.format("%.2f", percent) + "%");
            System.out.println("======================================");
        }
        boolean hitChance = calculateHitProbability(attackRoll, defenceRoll);
        if (!hitChance) {
            return 0;
        }
        if (maxHit == 0) {
            if (npc.getCombat().getTarget() instanceof Player player) {
                if (!player.hasFinished() && player.isActive()) {
                    player.message("Npc " + npc.getName() + " has no max hit.");
                }
            }
        }
        int damage = Utils.getRandom(finalMaxHit);
        damage = ceilToNextTenIfEnabled(target, damage);
        return damage;
    }

    private static int calculateAttackRoll(NPC npc, NpcAttackStyle style, CombatData data) {
        if (data == null) return npc.getCombatLevel();

        switch (style) {
            case MAGIC -> {
                int mageLevel = data.magicLevel;
                int mageBonus = data.magicBonus;
                return effectiveRoll(mageLevel, mageBonus);
            }
            case RANGED -> {
                int rangeLevel = data.rangedLevel;
                int rangeBonus = data.rangedBonus;
                return effectiveRoll(rangeLevel, rangeBonus);
            }
            default -> {
                int atkLevel = data.attackLevel;
                int atkBonus = data.attackBonus;
                return effectiveRoll(atkLevel, atkBonus);
            }
        }
    }

    private static int effectiveRoll(int level, int bonus) {
        int effectiveLevel = level + 9;
        return effectiveLevel * (bonus + 64);
    }

    private static int calculateDefenceRoll(NpcAttackStyle style, Entity target) {
        if (target instanceof Player player) {
            return calculatePlayerDefenceRoll(player, style);
        }
        if (target instanceof NPC targetNpc) {
            return calculateNpcDefenceRoll(targetNpc, style);
        }
        return 1;
    }

    private static int calculatePlayerDefenceRoll(Player player, NpcAttackStyle style) {
        int[] playerBonuses = player.getCombatDefinitions().getBonuses();

        switch (style) {
            case MAGIC, MAGICAL_MELEE -> {
                int magicDef = (int) ((player.getSkills().getLevel(Skills.DEFENCE) * 0.3
                                                                        + player.getSkills().getLevel(Skills.MAGIC) * 0.7) * player.getPrayer().getMagicMultiplier());
                return effectiveDefRoll(magicDef, playerBonuses[BonusType.MagicDefence.getIndex()]);
            }
            case RANGED -> {
                int def = (int) (player.getSkills().getLevel(Skills.DEFENCE) * player.getPrayer().getDefenceMultiplier());
                return effectiveDefRoll(def, playerBonuses[BonusType.RangeDefence.getIndex()]);
            }
            default -> {
                int meleeDefBonus = getPlayerMeleeDefenceBonus(player, style);
                int def = (int) (player.getSkills().getLevel(Skills.DEFENCE) * player.getPrayer().getDefenceMultiplier());
                return effectiveDefRoll(def, meleeDefBonus);
            }
        }
    }

    private static int getPlayerMeleeDefenceBonus(Player player, NpcAttackStyle style) {
        int[] bonuses = player.getCombatDefinitions().getBonuses();
        return switch (style) {
            case STAB -> bonuses[BonusType.StabDefence.getIndex()];
            case SLASH -> bonuses[BonusType.SlashDefence.getIndex()];
            default -> bonuses[BonusType.CrushDefence.getIndex()];
        };
    }


    private static int calculateNpcDefenceRoll(NPC targetNpc, NpcAttackStyle style) {
        CombatData data = targetNpc.getCombatData();
        if (data == null) return targetNpc.getCombatLevel();

        int defLevel = data.defenceLevel;
        int defBonus = switch (style) {
            case MAGIC -> data.magicDefence.getMagic();
            case RANGED -> data.rangedDefence.getStandardBonus();
            case STAB -> data.meleeDefence.getStabBonus();
            case SLASH -> data.meleeDefence.getSlashBonus();
            default -> data.meleeDefence.getCrushBonus();
        };
        return effectiveDefRoll(defLevel, defBonus);
    }

    private static int effectiveDefRoll(int level, int bonus) {
        int effective = level + 9;
        return effective * (bonus + 64);
    }

    private static double calculateHitChance(int attackRoll, int defenceRoll) {
        if (attackRoll > defenceRoll) {
            return 1.0 - (defenceRoll + 2.0) / (2.0 * (attackRoll + 1.0));
        } else {
            return attackRoll / (2.0 * (defenceRoll + 1.0));
        }
    }

    private static boolean calculateHitProbability(int attackRoll, int defenceRoll) {
        double hitChance = calculateHitChance(attackRoll, defenceRoll);
        return Utils.randomDouble() < hitChance;
    }



    private static int ceilToNextTenIfEnabled(Entity target, int damage) {
        if (!(target instanceof Player player))
            return damage;

        if (player.getVarsManager().getBitValue(1485) == 0)
            return damage;

        if (damage <= 0)
            return damage;

        return ((damage + 9) / 10) * 10;
    }


}
