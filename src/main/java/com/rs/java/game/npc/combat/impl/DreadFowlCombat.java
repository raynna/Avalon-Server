package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.utils.Utils;

public class DreadFowlCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6825, 6824 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			npc.animate(new Animation(7810));
			npc.gfx(new Graphics(1318));
			delayHit(npc, target, 1, getMagicHit(npc, getRandomMaxHit(npc, 40, NPCCombatDefinitions.MAGE, target)));
			World.sendProjectileToTile(npc, target, 1376);
		} else {
			if (Utils.getRandom(10) == 0) {// 1/10 chance of random special
											// (weaker)
				npc.animate(new Animation(7810));
				npc.gfx(new Graphics(1318));
				delayHit(npc, target, 1, getMagicHit(npc, getRandomMaxHit(npc, 30, NPCCombatDefinitions.MAGE, target)));
				World.sendProjectileToTile(npc, target, 1376);
			} else {
				npc.animate(new Animation(7810));
				delayHit(npc, target, 1,
                        getMeleeHit(npc, getRandomMaxHit(npc, 30, NPCCombatDefinitions.MELEE, target)));
			}
		}
		return defs.getAttackDelay();
	}
}
