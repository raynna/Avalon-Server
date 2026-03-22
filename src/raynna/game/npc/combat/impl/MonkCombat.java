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

public class MonkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7727 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (Utils.roll(1, 3) && npc.getHitpoints() < npc.getMaxHitpoints()) {
			npc.heal(20);
			npc.animate(709);
			npc.gfx(84);
			npc.playSound(166, 1);
			return npc.getAttackSpeed();
		}
		npc.animate(npc.getAttackAnimation());
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}
}
