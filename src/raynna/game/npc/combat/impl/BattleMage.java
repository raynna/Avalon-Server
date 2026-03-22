package raynna.game.npc.combat.impl;

import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;

public class BattleMage extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 912, 913, 914 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		Hit mageHit = npc.magicHit(target, npc.getMaxHit());
		delayHit(npc, target, 2, mageHit);
		return npc.getAttackSpeed();
	}
}
