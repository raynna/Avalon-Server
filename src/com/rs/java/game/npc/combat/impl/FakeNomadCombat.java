package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;

public class FakeNomadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 8529 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.animate(new Animation(12697));
		boolean hit = getRandomMaxHit(npc, 50, NPCCombatDefinitions.MAGE, target) != 0;
		delayHit(npc, 2, target, getRegularHit(npc, hit ? 50 : 0));
		World.sendElementalProjectile(npc, target, 1657);
		if (hit) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.gfx(new Graphics(2278, 0, 100));
				}
			}, 1);
		}
		return defs.getAttackDelay();
	}

}
