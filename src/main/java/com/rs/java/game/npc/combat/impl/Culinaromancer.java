package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class Culinaromancer extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 3491 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(2);
		if (attackStyle == 0 || attackStyle == 1) { // Melee
			npc.animate(new Animation(defs.getAttackEmote()));
			delayHit(npc, target, 0,
                    getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
			return defs.getAttackDelay();
		}
		if (attackStyle == 2) {
			World.sendCBOWProjectile(npc, target, 362);
			npc.animate(new Animation(1979));
			target.addFreezeDelay(5000, false);
			delayHit(npc, target, 1,
                    getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.gfx(new Graphics(369, 0, 0));
				}
			}, 1);
			return defs.getAttackDelay() + 2;
		}
		return attackStyle;
	}
}
