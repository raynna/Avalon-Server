package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.ProjectileManager;
import com.rs.kotlin.game.world.projectile.Projectile;

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
				Hit meleeHit = npc.meleeHit(target, defs.getMaxHit());
				delayHit(npc, target, 1, meleeHit);
				return npc.getAttackSpeed();
			}
		}
		if (attackStyle == 1) {
			npc.animate(new Animation(16202));
			npc.gfx(new Graphics(2994));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					Hit rangeHit = npc.rangedHit(target, defs.getMaxHit() - 2);
					delayHit(npc, target, 1, rangeHit);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							target.gfx(new Graphics(3000));
						}
					}, 1);
				}
			}, 2);
		} else {
			npc.animate(new Animation(16195));
			npc.gfx(new Graphics(2995));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					Hit magicHit = npc.magicHit(target, defs.getMaxHit() - 2);
					ProjectileManager.send(Projectile.ELEMENTAL_SPELL, 2996, npc, target, () -> {
						applyRegisteredHit(npc, target, magicHit);
					});
				}
			}, 2);
		}

		return npc.getAttackSpeed() + 2;
	}

}
