package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

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
