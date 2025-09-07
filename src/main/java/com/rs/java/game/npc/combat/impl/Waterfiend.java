package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

import static com.rs.java.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class Waterfiend extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 5361 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(2);
		if (attackStyle == 0) { // Range
			npc.animate(new Animation(defs.getAttackAnim()));
			World.sendFastBowProjectile(npc, target, 12);
			delayHit(npc, target, 1,
                    getRangeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.RANGED, target)));
			return npc.getAttackSpeed();
		} else {//Magic
			npc.animate(new Animation(defs.getAttackAnim()));
			World.sendFastBowProjectile(npc, target, 2706);
			delayHit(npc, target, 1,
                    getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.MAGIC, target)));
			return npc.getAttackSpeed();
		}
	}
}
