package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class LivingRockStrickerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 8833 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
			// TODO add projectile
			npc.animate(new Animation(12196));
			delayHit(npc, target, 1,
                    getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.RANGED, target)));
		} else {
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 84, NpcAttackStyle.CRUSH, target)));
			return npc.getAttackSpeed();
		}

		return npc.getAttackSpeed();
	}

}
