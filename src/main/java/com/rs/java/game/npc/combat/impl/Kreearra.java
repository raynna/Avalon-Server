package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class Kreearra extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6222 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (!npc.isUnderCombat()) {
			if (!npc.withinDistance(target, 1)) {
				npc.addWalkStepsInteract(target.getX(), target.getY(), 32, npc.getSize(), false);
				return 0;
			}
			npc.animate(new Animation(6997));
			Hit hit = getMeleeHit(npc,
					NpcCombatCalculations.getRandomMaxHit(npc, 260, NpcAttackStyle.CRUSH, target));
			delayHit(npc, target, 1, hit);
			return npc.getCombatData().attackSpeedTicks;
		}
		npc.animate(new Animation(6976));
		for (Entity t : npc.getPossibleTargets()) {
			if (Utils.getRandom(2) == 0)
				sendMagicAttack(npc, t);
			else {
				Hit rangeHit = getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 720, NpcAttackStyle.RANGED, target));
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
		return npc.getCombatData().attackSpeedTicks;
	}

	private void sendMagicAttack(NPC npc, Entity target) {
		for (Entity t : npc.getPossibleTargets()) {
			Hit magicHit = getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 210, NpcAttackStyle.MAGIC, target));
			delayHit(npc, t, 1, magicHit);
			ProjectileManager.sendSimple(Projectile.STORM_OF_ARMADYL, 1197, npc, t);
		}
	}
}
