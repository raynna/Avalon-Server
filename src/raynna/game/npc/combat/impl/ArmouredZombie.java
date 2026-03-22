package raynna.game.npc.combat.impl;

import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;

public class ArmouredZombie extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8149, 8150, 8153 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}
}