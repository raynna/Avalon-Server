package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.AttackMethod;
import com.rs.kotlin.game.npc.combatdata.AttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class DreadFowlCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6825, 6824 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			npc.animate(new Animation(7810));
			npc.gfx(new Graphics(1318));
			delayHit(npc, target, 1, getMagicHit(npc, 40));
			World.sendProjectileToTile(npc, target, 1376);
		} else {
			if (Utils.getRandom(10) == 0) {// 1/10 chance of random special
											// (weaker)
				npc.animate(new Animation(7810));
				npc.gfx(new Graphics(1318));
				Hit magicHit = getMagicHit(npc, 30);
				delayHit(npc, target, 1, magicHit);
				World.sendProjectileToTile(npc, target, 1376);
			} else {
				npc.animate(new Animation(7810));
				delayHit(npc, target, 1,
                        getMeleeHit(npc, 30));
			}
		}
		return npc.getAttackSpeed();
	}
}
