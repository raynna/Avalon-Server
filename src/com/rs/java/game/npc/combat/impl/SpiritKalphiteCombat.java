package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.familiar.Familiar;

public class SpiritKalphiteCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6995, 6994 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// TODO find special
			npc.animate(new Animation(8519));
			npc.gfx(new Graphics(8519));
			damage = getRandomMaxHit(npc, 20, NPCCombatDefinitions.MELEE, target);
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		} else {
			npc.animate(new Animation(8519));
			damage = getRandomMaxHit(npc, 50, NPCCombatDefinitions.MELEE, target);
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		return defs.getAttackDelay();
	}
}
