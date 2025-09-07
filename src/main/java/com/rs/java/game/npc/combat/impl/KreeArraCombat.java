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

    @Override
    public Object[] getKeys() {
        return new Object[]{6222};
    }

    @Override
    public int attack(NPC npc, Entity target) {
        if (npc.getTickManager().getTicksLeft(TickManager.TickKeys.LAST_ATTACKED_TICK) == 0) {
            if (!npc.withinDistance(target, 1)) {
                npc.calcFollow(target, 2, true, false);
				getRandomAttack(npc, target);
                return 4;
            }
            npc.animate(new Animation(6997));
            Hit hit = npc.meleeHit(npc, 260);
            delayHit(npc, target, 1, hit);
            return npc.getAttackSpeed();
        }
		getRandomAttack(npc, target);
        return npc.getAttackSpeed();
    }

    private void getRandomAttack(NPC npc, Entity target) {
		npc.animate(new Animation(6976));
        if (Utils.roll(1, 2)) {
            sendRangedAttack(npc, target);
        } else {
            sendMagicAttack(npc, target);
        }
    }

    private void sendRangedAttack(NPC npc, Entity target) {
        for (Entity t : npc.getPossibleTargets()) {
            Hit rangeHit = npc.rangedHit(npc, 720);
            delayHit(npc, target, 1, rangeHit);
            ProjectileManager.sendSimple(Projectile.STORM_OF_ARMADYL, 1197, npc, target);
            for (int c = 0; c < 10; c++) {
                int dir = Utils.random(Utils.DIRECTION_DELTA_X.length);
                if (World.checkWalkStep(target.getPlane(), target.getX(), target.getY(), dir, 1)) {
                    t.setNextWorldTile(new WorldTile(target.getX() + Utils.DIRECTION_DELTA_X[dir],
                            target.getY() + Utils.DIRECTION_DELTA_Y[dir], target.getPlane()));
                    break;
                }
            }
        }
    }

    private void sendMagicAttack(NPC npc, Entity target) {
        for (Entity t : npc.getPossibleTargets()) {
            Hit magicHit = npc.magicHit(npc, 210);
            delayHit(npc, t, 1, magicHit);
            ProjectileManager.sendSimple(Projectile.STORM_OF_ARMADYL, 1197, npc, t);
        }
    }
}
