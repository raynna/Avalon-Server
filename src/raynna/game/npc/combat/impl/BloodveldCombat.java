package raynna.game.npc.combat.impl;

import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;

public class BloodveldCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bloodveld" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		Hit magicalMelee = npc.magicalMelee(target, npc.getMaxHit());//roll magic accuracy but hit with melee
		delayHit(npc, target, 0, magicalMelee);
		return npc.getAttackSpeed();
	}
}
