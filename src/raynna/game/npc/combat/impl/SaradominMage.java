package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class SaradominMage extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 1264 };
	}

	enum SaradominMageAttack { MELEE, MAGIC }

	@Override
	public int attack(final NPC npc, final Entity target) {
		SaradominMageAttack attack = Utils.randomWeighted(SaradominMageAttack.MELEE, 50, SaradominMageAttack.MAGIC, 50);
		boolean inMeleeDistance = npc.isWithinMeleeRange(target);
		if (!inMeleeDistance) {
			attack = SaradominMageAttack.MAGIC;
		}
		switch (attack) {
			case MELEE -> {
				npc.animate(npc.getAttackAnimation());
				Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
				delayHit(npc, target, 0, meleeHit);
			}
			case MAGIC -> {
				npc.animate(811);
				Hit magicHit = npc.magicHit(target, npc.getMaxHit());
				delayHit(npc, target, 2, magicHit);

			}
		}
		return npc.getAttackSpeed();
	}
}
