package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.familiar.Familiar;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class ThornySnailCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6807, 6806 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			npc.animate(new Animation(8148));
			npc.gfx(new Graphics(1385));
			World.sendElementalProjectile(npc, target, 1386);
			delayHit(npc, target, 1, getRangeHit(npc, getRandomMaxHit(npc, 80, NpcAttackStyle.RANGED, target)));
			npc.gfx(new Graphics(1387));
		} else {
			npc.animate(new Animation(8143));
			delayHit(npc, target, 1, getRangeHit(npc, getRandomMaxHit(npc, 40, NpcAttackStyle.RANGED, target)));
		}
		return npc.getAttackSpeed();
	}

}
