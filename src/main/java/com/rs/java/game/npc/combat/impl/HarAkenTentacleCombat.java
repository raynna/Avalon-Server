package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class HarAkenTentacleCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 15209, 15210 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		int attackStyle = Utils.random(2);
		if (attackStyle == 0 && (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)) {
			attackStyle = 1;
		}
		switch (attackStyle) {
		case 0:
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0,
                    getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit() - 36, NpcAttackStyle.STAB, target)));
			break;
		case 1:
			npc.animate(new Animation(npc.getId() == 15209 ? 16253 : 16242));
			World.sendElementalProjectile(npc, target, npc.getId() == 15209 ? 3004 : 2922);
			if (npc.getId() == 15209)
				delayHit(npc, target, 2,
                        getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.RANGED, target)));
			else
				delayHit(npc, target, 2,
                        getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.MAGIC, target)));
			break;
		}
		return npc.getAttackSpeed();
	}
}
