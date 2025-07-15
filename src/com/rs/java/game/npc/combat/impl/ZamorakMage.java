package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;

public class ZamorakMage extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 1007 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackEmote()));
		delayHit(npc, 1, target,
				getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
		return defs.getAttackDelay();
	}
}
