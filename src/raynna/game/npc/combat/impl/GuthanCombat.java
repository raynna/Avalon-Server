package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class GuthanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2027 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		Hit meleeHit = npc.meleeHit(target, defs.getMaxHit(), NpcAttackStyle.STAB);
		//TODO ADD SPECIAL HEAL EFFECT
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}
}
