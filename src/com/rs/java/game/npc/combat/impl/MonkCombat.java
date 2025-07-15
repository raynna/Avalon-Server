package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.utils.Utils;

public class MonkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7727 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = 0;
		switch (attackStyle) {
		case 0: // melee
			if (Utils.getRandom(2) == 0 && npc.getHitpoints() < npc.getMaxHitpoints()) {
					npc.heal(20);
					npc.animate(new Animation(805));
				break;
			}
			delayHit(npc, 0, target,
					getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
			npc.animate(new Animation(defs.getAttackEmote()));
			break;
		}
		return defs.getAttackDelay();
	}
}
