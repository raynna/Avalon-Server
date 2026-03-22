package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.familiar.Familiar;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class SpiritKalphiteCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6995, 6994 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// TODO find special
			npc.animate(new Animation(8519));
			npc.gfx(new Graphics(8519));
			damage = getRandomMaxHit(npc, 20, NpcAttackStyle.CRUSH, target);
			delayHit(npc, target, 1, getMeleeHit(npc, damage));
		} else {
			npc.animate(new Animation(8519));
			damage = getRandomMaxHit(npc, 50, NpcAttackStyle.CRUSH, target);
			delayHit(npc, target, 1, getMeleeHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}
}
