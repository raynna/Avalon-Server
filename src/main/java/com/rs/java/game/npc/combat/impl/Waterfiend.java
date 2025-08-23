package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.utils.Utils;

public class Waterfiend extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 5361 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(2);
		if (attackStyle == 0) { // Range
			npc.animate(new Animation(defs.getAttackEmote()));
			World.sendFastBowProjectile(npc, target, 12);
			delayHit(npc, target, 1,
                    getRangeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.RANGE, target)));
			return defs.getAttackDelay();
		} else {//Magic
			npc.animate(new Animation(defs.getAttackEmote()));
			World.sendFastBowProjectile(npc, target, 2706);
			delayHit(npc, target, 1,
                    getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
			return defs.getAttackDelay();
		}
	}
}
