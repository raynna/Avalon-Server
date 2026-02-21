package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class MonkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7727 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (Utils.roll(1, 3) && npc.getHitpoints() < npc.getMaxHitpoints()) {
			npc.heal(20);
			npc.animate(805);
			return npc.getAttackSpeed();
		}
		npc.animate(npc.getAttackAnimation());
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}
}
