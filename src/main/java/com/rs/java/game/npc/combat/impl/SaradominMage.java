package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

import static com.rs.java.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

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
