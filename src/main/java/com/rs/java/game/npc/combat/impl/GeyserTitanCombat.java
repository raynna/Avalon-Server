package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class GeyserTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7340, 7339 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
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
					delayHit(npc, target, 1,
                            getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 300, NpcAttackStyle.RANGED, target)));
				else
					delayHit(npc, target, 1,
                            getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 300, NpcAttackStyle.MAGIC, target)));
			} else {// melee hit
				delayHit(npc, target, 1,
                        getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 300, NpcAttackStyle.CRUSH, target)));
			}
			World.sendElementalProjectile(npc, target, 1376);
		} else {
			if (distant) {// range
				damage = NpcCombatCalculations.getRandomMaxHit(npc, 244, NpcAttackStyle.RANGED, target);
				npc.animate(new Animation(7883));
				npc.gfx(new Graphics(1375));
				World.sendElementalProjectile(npc, target, 1374);
				delayHit(npc, target, 2, getRangeHit(npc, damage));
			} else {// melee
				damage = NpcCombatCalculations.getRandomMaxHit(npc, 244, NpcAttackStyle.CRUSH, target);
				npc.animate(new Animation(7879));
				delayHit(npc, target, 1, getMeleeHit(npc, damage));
			}
		}
		return npc.getAttackSpeed();
	}
}
