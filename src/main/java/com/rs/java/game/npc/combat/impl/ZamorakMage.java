package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

import static com.rs.java.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class ZamorakMage extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 1007 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		delayHit(npc, target, 1,
                getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.MAGIC, target)));
		return npc.getAttackSpeed();
	}
}
