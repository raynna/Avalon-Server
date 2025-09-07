package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class MonkeyGuard extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 1459, 1460 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int timesHealed = 0;
		int attackStyle = 0;
		switch (attackStyle) {
		case 0: // melee
			if (npc.getHitpoints() < Math.ceil(npc.getMaxHitpoints() * 0.20) && timesHealed < 15) {
				timesHealed++;
				npc.heal(250);
				npc.animate(new Animation(1405));
				return npc.getAttackSpeed();
			}
			delayHit(npc, target, 0,
                    getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target)));
			npc.animate(new Animation(defs.getAttackAnim()));
			break;
		}
		return npc.getAttackSpeed();
	}
}
