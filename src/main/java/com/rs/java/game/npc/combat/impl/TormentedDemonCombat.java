package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.others.TormentedDemon;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.npc.worldboss.TormentedDemonWorldBoss;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class TormentedDemonCombat extends CombatScript {

    //GFX
    private static final int HIT_GFX = 2883;
    private static final int MAGIC_PROJECTILE_ID = 1884;
    private static final int SHIELD_GFX = 1885;
    private static final Graphics MELEE_GFX = new Graphics(1886, 2, 0);
    private static final int RANGE_PROJECTILE_ID = 1887;


    // Melee
    private static final int MELEE_MAX_HIT = 189;
    private static final Animation MELEE_ANIMATION = new Animation(10922);

    // Magic
    private static final int MAGIC_MAX_HIT = 269;
    private static final Animation MAGIC_ANIMATION = new Animation(10918);

    // Ranged
    private static final int RANGED_MAX_HIT = 269;
    private static final Animation RANGED_ANIMATION = new Animation(10919);

    @Override
    public Object[] getKeys() {
        return new Object[]{"Tormented demon"};
    }

    @Override
    public int attack(NPC npc, Entity target) {
        final NpcCombatDefinition defs = npc.getCombatDefinitions();

        int attackStyle;
        int previousStyle;

        if (npc instanceof TormentedDemon tormentedDemon) {
            attackStyle = tormentedDemon.getCurrentCombatType();
            previousStyle = tormentedDemon.getPreviousCombatType();
        } else if (npc instanceof TormentedDemonWorldBoss worldTorm) {
            // World boss version
            attackStyle = worldTorm.getCurrentCombatType();
            previousStyle = worldTorm.getPreviousCombatType();
        } else {
            // Fallback for other NPCs
            attackStyle = 0;
            previousStyle = -1;
        }

        // Switch combat style if melee can't reach
        if (attackStyle == 0 && !npc.withinDistance(target, 1)) {
            int random = Utils.random(1, 2);
            while (random == previousStyle) {
                random = Utils.random(1, 2);
            }
            attackStyle = random;

            // Update the combat style
            if (npc instanceof TormentedDemon) {
                ((TormentedDemon) npc).setCurrentCombatType(attackStyle);
            } else if (npc instanceof TormentedDemonWorldBoss) {
                ((TormentedDemonWorldBoss) npc).setCurrentCombatType(attackStyle);
            }
        }

        switch (attackStyle) {
            case 0 -> attackMelee(npc, target);
            case 1 -> attackMagic(npc, target);
            case 2 -> attackRanged(npc, target);
        }

        return npc.getAttackSpeed() + 2;
    }

    private void attackMelee(NPC npc, Entity target) {
        int damage = NpcCombatCalculations.getRandomMaxHit(npc, MELEE_MAX_HIT, NpcAttackStyle.SLASH, target);
        npc.animate(MELEE_ANIMATION);
        npc.gfx(MELEE_GFX);
        delayHit(npc, target, 0, getMeleeHit(npc, damage));
    }

    private void attackMagic(NPC npc, Entity target) {
        if (npc instanceof TormentedDemonWorldBoss) {
            attackMagicAoE(npc, target);
        } else {
            attackMagicSingle(npc, target);
        }
    }

    private void attackRanged(NPC npc, Entity target) {
        if (npc instanceof TormentedDemonWorldBoss) {
            attackRangedAoE(npc, target);
        } else {
            attackRangedSingle(npc, target);
        }
    }

    private void attackMagicSingle(NPC npc, Entity target) {
        int damage = NpcCombatCalculations.getRandomMaxHit(npc, MAGIC_MAX_HIT, NpcAttackStyle.MAGIC, target);
        npc.animate(MAGIC_ANIMATION);
        ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, MAGIC_PROJECTILE_ID, npc, target);
        delayHit(npc, target, 2, getMagicHit(npc, damage));
    }

    private void attackRangedSingle(NPC npc, Entity target) {
        int damage = NpcCombatCalculations.getRandomMaxHit(npc, RANGED_MAX_HIT, NpcAttackStyle.RANGED, target);
        npc.animate(RANGED_ANIMATION);
        ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, RANGE_PROJECTILE_ID, npc, target);
        delayHit(npc, target, 2, getRangeHit(npc, damage));
    }

    private void attackMagicAoE(NPC npc, Entity mainTarget) {
        npc.animate(MAGIC_ANIMATION);

        for (Entity target : npc.getPossibleTargets()) {
            if (target == null || target.hasFinished() || !target.withinDistance(npc, npc.getForceTargetDistance())) {
                continue;
            }

            int damage = NpcCombatCalculations.getRandomMaxHit(npc, MAGIC_MAX_HIT, NpcAttackStyle.MAGIC, target);
            ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, MAGIC_PROJECTILE_ID, npc, target);
            delayHit(npc, target, 2, getMagicHit(npc, damage));
        }
    }

    private void attackRangedAoE(NPC npc, Entity mainTarget) {
        npc.animate(RANGED_ANIMATION);

        for (Entity target : npc.getPossibleTargets()) {
            if (target == null || target.hasFinished() || !target.withinDistance(npc, npc.getForceTargetDistance())) {
                continue;
            }

            int damage = NpcCombatCalculations.getRandomMaxHit(npc, RANGED_MAX_HIT, NpcAttackStyle.RANGED, target);
            ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, RANGE_PROJECTILE_ID, npc, target);
            delayHit(npc, target, 2, getRangeHit(npc, damage));
        }
    }
}
