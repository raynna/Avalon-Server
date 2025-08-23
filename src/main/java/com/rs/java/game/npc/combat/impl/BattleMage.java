package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;

public class BattleMage extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 912, 913, 914 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		Hit mageHit = npc.magicHit(target, npc.getMaxHit());
		delayHit(npc, target, 2, mageHit);
		return npc.getAttackSpeed();
	}
}
