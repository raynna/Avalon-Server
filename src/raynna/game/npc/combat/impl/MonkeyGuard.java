package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class MonkeyGuard extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 1459, 1460 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (npc.getHitpoints() < Math.ceil(npc.getMaxHitpoints() * 0.20)) {
			npc.heal(250);
			npc.animate(1405);
			return npc.getAttackSpeed();
		}
		npc.animate(npc.getAttackAnimation());
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}
}
