package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class LavaTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7342, 7341 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.animate(new Animation(7883));
			npc.gfx(new Graphics(1491));
			delayHit(npc, target, 1, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 140, NpcAttackStyle.CRUSH, target)));
			if (damage <= 4 && target instanceof Player) {
				Player player = (Player) target;
				player.getCombatDefinitions()
						.decreaseSpecialAttack((player.getCombatDefinitions().getSpecialAttackPercentage() / 10));
			}
		} else {
			damage = NpcCombatCalculations.getRandomMaxHit(npc, 140, NpcAttackStyle.CRUSH, target);
			npc.animate(new Animation(7980));
			npc.gfx(new Graphics(1490));
			delayHit(npc, target, 1, getMeleeHit(npc, damage));
		}
		if (Utils.getRandom(10) == 0)// 1/10 chance of happening
			delayHit(npc, target, 1, getMeleeHit(npc, Utils.getRandom(50)));
		return npc.getAttackSpeed();
	}
}
