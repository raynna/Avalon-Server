package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.prayer.AncientPrayer;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.java.utils.Utils;

public class JadinkoCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 13820, 13821, 13822 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MAGIC) || player.getPrayer().isActive(AncientPrayer.DEFLECT_MAGIC)) {
				npc.setForceFollowClose(true);
				meleeAttack(npc, target);
				return defs.getAttackDelay();
			} else {
				npc.setForceFollowClose(false);
				if ((distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)) {
					magicAttack(npc, target);
					return defs.getAttackDelay();
				} else {
					switch (Utils.random(2)) {
					case 0:
						magicAttack(npc, target);
						break;
					case 1:
					default:
						meleeAttack(npc, target);
					}
				}
				return defs.getAttackDelay();
			}
		} else
			return defs.getAttackDelay();
	}

	private void magicAttack(NPC npc, Entity target) {
		npc.animate(new Animation(npc.getId() == 13820 ? 3031 : 3215));
		delayHit(npc, 2, target, getMagicHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
	}

	private void meleeAttack(NPC npc, Entity target) {
		npc.animate(new Animation(npc.getId() == 13820 ? 3009 : 3214));
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
	}
}
