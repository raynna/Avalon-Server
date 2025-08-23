package com.rs.java.game.npc.combat;

import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.CombatData;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.player.equipment.BonusType;

public class NpcCombatCalculations {

    public static int getRandomMaxHit(NPC npc, int maxHit, NpcAttackStyle attackStyle, Entity target) {
        npc.setBonuses(); // Ensure NPC bonuses are loaded
        CombatData data = npc.getCombatData();

        // --- Attacker (NPC) Accuracy Roll ---
        double attackRoll = calculateAttackRoll(npc, attackStyle, data);
        //System.out.println(npc.getName() + " attack roll: " + attackRoll);
        // --- Defender (Target) Defence Roll ---
        double defenceRoll = calculateDefenceRoll(npc, attackStyle, target);
        //if (target instanceof Player player)
            //System.out.println(player.getUsername() + " defence roll: " + defenceRoll);
        // --- Hit chance ---
        boolean hitChance = calculateHitProbability(attackRoll, defenceRoll);
        //System.out.println("HitChance: " + hitChance);
        // --- Roll damage ---
        if (!hitChance) {
            return 0; // Missed
        }

        return Utils.getRandom(maxHit); // Random damage up to max
    }

    private static double calculateAttackRoll(NPC npc, NpcAttackStyle style, CombatData data) {
        if (data == null) return npc.getCombatLevel();

        switch (style) {
            case MAGIC -> {
                int mageLevel = data.magicLevel;
                int mageBonus = data.magicBonus;
                //System.out.println(npc.getName() + " used magic attack, MagicLevel: " + mageLevel + ", MageAttack: " + mageBonus);
                return effectiveRoll(mageLevel, mageBonus);
            }
            case RANGED -> {
                int rangeLevel = data.rangedLevel;
                int rangeBonus = data.rangedBonus;
                //System.out.println(npc.getName() + " used ranged attack, RangeLevel: " + rangeLevel + ", RangeAttack: " + rangeBonus);
                return effectiveRoll(rangeLevel, rangeBonus);
            }
            default -> {
                int atkLevel = data.attackLevel;
                int atkBonus = data.attackBonus;
                //System.out.println(npc.getName() + " used melee attack, AttackLevel: " + atkLevel + ", AttackBonus: " + atkBonus);
                return effectiveRoll(atkLevel, atkBonus);
            }
        }
    }

    private static int effectiveRoll(int level, int bonus) {
        int effectiveLevel = level + 9;
        return effectiveLevel * (bonus + 64);
    }

    private static double calculateDefenceRoll(NPC npc, NpcAttackStyle style, Entity target) {
        if (target instanceof Player player) {
            return calculatePlayerDefenceRoll(player, style, npc);
        }
        if (target instanceof NPC targetNpc) {
            return calculateNpcDefenceRoll(targetNpc, style);
        }
        return 1; // fallback
    }

    private static double calculatePlayerDefenceRoll(Player player, NpcAttackStyle style, NPC npc) {
        int[] playerBonuses = player.getCombatDefinitions().getBonuses();

        switch (style) {
            case MAGIC -> {
                int magicDef = (int) (player.getSkills().getLevel(Skills.DEFENCE) * 0.3
                                        + player.getSkills().getLevel(Skills.MAGIC) * 0.7 * player.getPrayer().getMagicMultiplier());
                return effectiveDefRoll(magicDef, playerBonuses[BonusType.MagicDefence.getIndex()]);
            }
            case RANGED -> {
                int def = (int) (player.getSkills().getLevel(Skills.DEFENCE) * player.getPrayer().getDefenceMultiplier());
                return effectiveDefRoll(def, playerBonuses[BonusType.RangeDefence.getIndex()]);
            }
            default -> {
                int meleeDefBonus = getPlayerMeleeDefenceBonus(player, npc);
                int def = (int) (player.getSkills().getLevel(Skills.DEFENCE) * player.getPrayer().getDefenceMultiplier());
                return effectiveDefRoll(def, meleeDefBonus);
            }
        }
    }

    private static int getPlayerMeleeDefenceBonus(Player player, NPC npc) {
        NpcAttackStyle type = NpcAttackStyle.fromList(npc.getCombatData().attackStyles);
        int[] bonuses = player.getCombatDefinitions().getBonuses();
        return switch (type) {
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
        int effective = level + 8;
        return effective * (bonus + 64);
    }

    private static boolean calculateHitProbability(double attackRoll, double defenceRoll) {
        double random = Math.random();

        double probability;
        if (attackRoll > defenceRoll) {
            probability = 1.0 - (defenceRoll + 2.0) / (2.0 * (attackRoll + 1.0));
        } else {
            probability = attackRoll / (2.0 * (defenceRoll + 1.0));
        }
        //System.out.println("random: " + random + " vs probability: " + probability);
        return random < probability;
    }

}
