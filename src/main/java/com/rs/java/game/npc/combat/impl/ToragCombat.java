package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

import static com.rs.java.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class ToragCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2029 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target);
		int damage2 = getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target);
		if ((damage != 0  || damage2 != 0) && target instanceof Player targetPlayer && Utils.random(3) == 0) {
			target.gfx(new Graphics(399));
            targetPlayer.setRunEnergy(targetPlayer.getRunEnergy() > 4 ? targetPlayer.getRunEnergy() - 4 : 0);
		}
		delayHit(npc, target, 0, getMeleeHit(npc, damage));
		delayHit(npc, target, 0, getMeleeHit(npc, damage2));
		return npc.getAttackSpeed();
	}
}
