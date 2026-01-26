package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class FakeNomadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 8529 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(12697));
		Hit magicHit = npc.magicHit(target, 50);
		boolean hit = magicHit.getDamage() != 0;
		delayHit(npc, target, 2, getRegularHit(npc, hit ? 50 : 0));
		World.sendElementalProjectile(npc, target, 1657);
		if (hit) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.gfx(new Graphics(2278, 0, 100));
				}
			}, 1);
		}
		return npc.getAttackSpeed();
	}

}
