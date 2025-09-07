package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

import static com.rs.java.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

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
