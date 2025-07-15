package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;

public class ArmouredZombie extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8149, 8150, 8153 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), -1, target)));
		return defs.getAttackDelay();
	}
}