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
		boolean hit = NpcCombatCalculations.getRandomMaxHit(npc, 50, NpcAttackStyle.MAGIC, target) != 0;
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
