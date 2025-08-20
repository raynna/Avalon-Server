package com.rs.java.game.npc.combat;

import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.NpcBonusType;
import com.rs.kotlin.game.player.equipment.BonusType;

public class NpcCombatCalculations {

    public static int getRandomMaxHit(NPC npc, int maxHit, int attackStyle, Entity target) {
        npc.setBonuses(); // Ensure NPC bonuses are loaded
        int[] bonuses = npc.getBonuses();

        // --- Attacker (NPC) Accuracy Roll ---
        double attackRoll = calculateAttackRoll(npc, attackStyle, bonuses);
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

    private static double calculateAttackRoll(NPC npc, int style, int[] bonuses) {
        if (bonuses == null) return npc.getCombatLevel();

        switch (style) {
            case NPCCombatDefinitions.MAGE -> {
                int mageLevel = bonuses[NpcBonusType.MagicLevel.getIndex()];
                int mageBonus = bonuses[NpcBonusType.MagicAttack.getIndex()];
                //System.out.println(npc.getName() + " used magic attack, MagicLevel: " + mageLevel + ", MageAttack: " + mageBonus);
                return effectiveRoll(mageLevel, mageBonus);
            }
            case NPCCombatDefinitions.RANGE -> {
                int rangeLevel = bonuses[NpcBonusType.RangeLevel.getIndex()];
                int rangeBonus = bonuses[NpcBonusType.RangeAttack.getIndex()];
                //System.out.println(npc.getName() + " used ranged attack, RangeLevel: " + rangeLevel + ", RangeAttack: " + rangeBonus);
                return effectiveRoll(rangeLevel, rangeBonus);
            }
            default -> {
                int atkLevel = bonuses[NpcBonusType.AttackLevel.getIndex()];
                int atkBonus = (int) getMeleeAttackBonus(npc, bonuses);
                //System.out.println(npc.getName() + " used melee attack, AttackLevel: " + atkLevel + ", AttackBonus: " + atkBonus);
                return effectiveRoll(atkLevel, atkBonus);
            }
        }
    }

    private static double getMeleeAttackBonus(NPC npc, int[] bonuses) {
        int type = npc.getCombatDefinitions().getAttackType();
        return switch (type) {
            case NPCCombatDefinitions.STAB -> bonuses[NpcBonusType.StabAttack.getIndex()];
            case NPCCombatDefinitions.SLASH -> bonuses[NpcBonusType.SlashAttack.getIndex()];
            default -> bonuses[NpcBonusType.CrushAttack.getIndex()];
        };
    }

    private static double effectiveRoll(int level, int bonus) {
        double effective = Math.round(level) + 8;
        return Math.round(effective * (1 + bonus + 64.0));
    }

    private static double calculateDefenceRoll(NPC npc, int style, Entity target) {
        if (target instanceof Player player) {
            return calculatePlayerDefenceRoll(player, style, npc);
        }
        if (target instanceof NPC targetNpc) {
            return calculateNpcDefenceRoll(targetNpc, style);
        }
        return 1; // fallback
    }

    private static double calculatePlayerDefenceRoll(Player player, int style, NPC npc) {
        int[] playerBonuses = player.getCombatDefinitions().getBonuses();

        switch (style) {
            case NPCCombatDefinitions.MAGE -> {
                double magicDef = player.getSkills().getLevel(Skills.DEFENCE) * 0.3
                        + player.getSkills().getLevel(Skills.MAGIC) * 0.7 * player.getPrayer().getMagicMultiplier();
                return effectiveDefRoll(magicDef, playerBonuses[BonusType.MagicDefence.getIndex()]);
            }
            case NPCCombatDefinitions.RANGE -> {
                double def = player.getSkills().getLevel(Skills.DEFENCE) * player.getPrayer().getDefenceMultiplier();
                return effectiveDefRoll(def, playerBonuses[BonusType.RangeDefence.getIndex()]);
            }
            default -> {
                int meleeDefBonus = getPlayerMeleeDefenceBonus(player, npc);
                double def = player.getSkills().getLevel(Skills.DEFENCE) * player.getPrayer().getDefenceMultiplier();
                return effectiveDefRoll(def, meleeDefBonus);
            }
        }
    }

    private static int getPlayerMeleeDefenceBonus(Player player, NPC npc) {
        int type = npc.getCombatDefinitions().getAttackType();
        int[] bonuses = player.getCombatDefinitions().getBonuses();
        return switch (type) {
            case NPCCombatDefinitions.STAB -> bonuses[BonusType.StabDefence.getIndex()];
            case NPCCombatDefinitions.SLASH -> bonuses[BonusType.SlashDefence.getIndex()];
            default -> bonuses[BonusType.CrushDefence.getIndex()];
        };
    }

    private static double calculateNpcDefenceRoll(NPC targetNpc, int style) {
        int[] bonuses = targetNpc.getBonuses();
        if (bonuses == null) return targetNpc.getCombatLevel();

        int defLevel = bonuses[CombatDefinitions.NPC_DEFENCE_LEVEL];
        int defBonus = switch (style) {
            case NPCCombatDefinitions.MAGE -> bonuses[CombatDefinitions.NPC_MAGIC_BONUS];
            case NPCCombatDefinitions.RANGE -> bonuses[CombatDefinitions.NPC_RANGE_BONUS];
            default -> bonuses[CombatDefinitions.NPC_STAB_BONUS];
        };

        return effectiveDefRoll(defLevel, defBonus);
    }

    private static double effectiveDefRoll(double level, int bonus) {
        double effective = Math.round(level) + 8;
        return Math.round(effective * (1 + bonus + 64.0));
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
