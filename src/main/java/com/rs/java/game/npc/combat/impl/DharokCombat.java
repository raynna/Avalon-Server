package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class DharokCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2026 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		Hit hit = npc.meleeHit(npc, defs.getMaxHit());
		if (hit.getDamage() > 0) {
			double percMissing = 1.0 - ((double) npc.getHitpoints() / (double) npc.getMaxHitpoints());
			int bonus = (int) Math.round(percMissing * 380);
			hit.setDamage(hit.getDamage() + bonus);
		}
		delayHit(npc, target, 0, hit);
		return npc.getAttackSpeed();
	}
}
