package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;

public class ArmouredZombie extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8149, 8150, 8153 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}
}