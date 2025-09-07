package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.Npc;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

import static com.rs.java.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

/**
 * 
 * @author Jae <jae@xiduth.com>
 * 
 *         Last modified: <Oct 17, 2013>
 *
 */
public class PestQueenCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6358 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		if (Utils.getRandom(4) == 0) {
			switch (Utils.getRandom(0)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk("BEGONE!"));
				break;
			}
		} else if (Utils.getRandom(5) == 1) {
			npc.animate(new Animation(1344));
			npc.setNextForceTalk(new ForceTalk("BLURGH"));
			for (Entity targets : npc.getPossibleTargets()) {
				delayHit(npc, targets, 1, getMagicHit(npc, getRandomMaxHit(npc, 320, NpcAttackStyle.MAGIC, target)));
			}
		}

		if (Utils.getRandom(2) == 0) {
			npc.animate(new Animation(14801));
			for (Entity targets : npc.getPossibleTargets()) {
				delayHit(npc, targets, 1, getMeleeHit(npc, getRandomMaxHit(npc, 500, NpcAttackStyle.CRUSH, target)));
			}
		} else {
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0,
                    getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target)));
		}
		return npc.getAttackSpeed();
	}
}