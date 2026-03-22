package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.util.Utils;
import raynna.game.npc.combatdata.Npc;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

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