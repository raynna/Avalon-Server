package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.TickManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class KreeArraCombat extends CombatScript {

    private final static int MELEE_ANIMATION = 6977;
    private final static int RANGE_ANIMATION = 6976;

    enum KreeArraAttack { MELEE, RANGE, MAGE }

    @Override
    public Object[] getKeys() {
        return new Object[]{6222};
    }

    @Override
    public int attack(NPC npc, Entity target) {
        if (npc.getTickManager().getTicksLeft(TickManager.TickKeys.LAST_ATTACKED_TICK) == 0) {
            boolean inMelee = npc.isWithinMeleeRange(target);
            if (!inMelee) {
                npc.calcFollow(target, 2, true, false);
                return 1;
            }
            npc.animate(MELEE_ANIMATION);
            Hit hit = npc.meleeHit(npc, 260);
            delayHit(npc, target, 1, hit);
            return npc.getAttackSpeed();
        }
		getRandomAttack(npc, target);
        return npc.getAttackSpeed();
    }

    private void getRandomAttack(NPC npc, Entity target) {
		npc.animate(new Animation(RANGE_ANIMATION));
        KreeArraAttack attack;
        attack = Utils.randomOf(KreeArraAttack.RANGE, KreeArraAttack.MAGE);
        switch (attack) {
            case RANGE -> sendRangedAttack(npc, target);
            case MAGE -> sendMagicAttack(npc, target);
        }
    }

    private void sendRangedAttack(NPC npc, Entity target) {
        for (Entity t : npc.getPossibleTargets()) {
            Hit rangeHit = npc.rangedHit(npc, 720);
            ProjectileManager.send(Projectile.STORM_OF_ARMADYL, 1197, npc, t, () -> {
                applyRegisteredHit(npc, target, rangeHit);
                for (int c = 0; c < 10; c++) {
                    int dir = Utils.random(Utils.DIRECTION_DELTA_X.length);
                    if (World.checkWalkStep(target.getPlane(), target.getX(), target.getY(), dir, 1)) {
                        t.setNextWorldTile(new WorldTile(target.getX() + Utils.DIRECTION_DELTA_X[dir],
                                target.getY() + Utils.DIRECTION_DELTA_Y[dir], target.getPlane()));
                        break;
                    }
                }
            });
        }
    }

    private void sendMagicAttack(NPC npc, Entity target) {
        for (Entity t : npc.getPossibleTargets()) {
            Hit magicHit = npc.magicHit(npc, 210);
            ProjectileManager.send(Projectile.STORM_OF_ARMADYL, 1197, npc, t, () -> {
                applyRegisteredHit(npc, target, magicHit);
            });
        }
    }
}
