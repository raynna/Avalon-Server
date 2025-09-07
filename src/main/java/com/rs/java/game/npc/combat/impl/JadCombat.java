package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class JadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 2745, 15208 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(3);
		if (attackStyle == 2) { // melee
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
				attackStyle = Utils.random(2); // set mage
			else {
				npc.animate(new Animation(defs.getAttackAnim()));
				delayHit(npc, target, 1,
                        getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target)));
				return npc.getAttackSpeed();
			}
		}
		if (attackStyle == 1) {
			npc.animate(new Animation(16202));
			npc.gfx(new Graphics(2994));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					delayHit(npc, target, 2, getRangeHit(npc,
							NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit() - 2,NpcAttackStyle.RANGED, target)));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							target.gfx(new Graphics(3000));
						}
					}, 1);
				}
			}, 3);
		} else {
			npc.animate(new Animation(16195));
			npc.gfx(new Graphics(2995));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					delayHit(npc, target, 2, getMagicHit(npc,
							NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit() - 2, NpcAttackStyle.MAGIC, target)));
							World.sendJadProjectile(npc, target, 2996);
				}
			}, 3);
		}

		return npc.getAttackSpeed() + 2;
	}

}
