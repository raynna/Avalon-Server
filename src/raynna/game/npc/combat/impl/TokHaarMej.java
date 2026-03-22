package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class TokHaarMej extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 15203, 2596 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		int hit = 0;
		int attackStyle = Utils.random(2);
		if (attackStyle == 0 && (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)) {
			attackStyle = 1;
		}
		switch (attackStyle) {
		case 0:
			hit = getRandomMaxHit(npc, defs.getMaxHit() - 36, NpcAttackStyle.CRUSH, target);
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0, getMeleeHit(npc, hit));
			break;
		case 1:
			hit = getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.MAGIC, target);
			npc.animate(new Animation(16122));
			World.sendElementalProjectile(npc, target, 2991);
			delayHit(npc, target, 2, getMagicHit(npc, hit));
			break;
		}
		return npc.getAttackSpeed();
	}
}
