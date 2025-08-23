package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;

public class BloodveldCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bloodveld" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		Hit mageHit = npc.magicHit(target, npc.getMaxHit());//roll magic accuracy but hit with melee
		delayHit(npc, target, 0, mageHit);
		return npc.getAttackSpeed();
	}
}
