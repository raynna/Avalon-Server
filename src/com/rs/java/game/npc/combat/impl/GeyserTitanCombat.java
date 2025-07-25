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

public class GeyserTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7340, 7339 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		boolean distant = false;
		int size = npc.getSize();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
			distant = true;
		if (usingSpecial) {// priority over regular attack
			npc.animate(new Animation(7883));
			npc.gfx(new Graphics(1373));
			if (distant) {// range hit
				if (Utils.getRandom(2) == 0)
					delayHit(npc, 1, target,
							getRangeHit(npc, getRandomMaxHit(npc, 300, NPCCombatDefinitions.RANGE, target)));
				else
					delayHit(npc, 1, target,
							getMagicHit(npc, getRandomMaxHit(npc, 300, NPCCombatDefinitions.MAGE, target)));
			} else {// melee hit
				delayHit(npc, 1, target,
						getMeleeHit(npc, getRandomMaxHit(npc, 300, NPCCombatDefinitions.MELEE, target)));
			}
			World.sendElementalProjectile(npc, target, 1376);
		} else {
			if (distant) {// range
				damage = getRandomMaxHit(npc, 244, NPCCombatDefinitions.RANGE, target);
				npc.animate(new Animation(7883));
				npc.gfx(new Graphics(1375));
				World.sendElementalProjectile(npc, target, 1374);
				delayHit(npc, 2, target, getRangeHit(npc, damage));
			} else {// melee
				damage = getRandomMaxHit(npc, 244, NPCCombatDefinitions.MELEE, target);
				npc.animate(new Animation(7879));
				delayHit(npc, 1, target, getMeleeHit(npc, damage));
			}
		}
		return defs.getAttackDelay();
	}
}
