package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
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
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int attackStyle = 0;
		switch (attackStyle) {
		case 0: // melee
			if (Utils.getRandom(2) == 0 && npc.getHitpoints() < npc.getMaxHitpoints()) {
					npc.heal(20);
					npc.animate(new Animation(805));
				break;
			}
			delayHit(npc, target, 0,
                    getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target)));
			npc.animate(new Animation(defs.getAttackAnim()));
			break;
		}
		return npc.getAttackSpeed();
	}
}
