package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class EvilChickenCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Evil Chicken" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		switch (Utils.getRandom(5)) {
		case 0:
			npc.setNextForceTalk(new ForceTalk("Bwuk"));
			break;
		case 1:
			npc.setNextForceTalk(new ForceTalk("Bwuk bwuk bwuk"));
			break;
		case 2:
			String name = "";
			if (target instanceof Player)
				name = ((Player) target).getDisplayName();
			npc.setNextForceTalk(new ForceTalk("Flee from me, " + name));
			break;
		case 3:
			name = "";
			if (target instanceof Player)
				name = ((Player) target).getDisplayName();
			npc.setNextForceTalk(new ForceTalk("Begone, " + name));
			break;
		case 4:
			npc.setNextForceTalk(new ForceTalk("Bwaaaauuuuk bwuk bwuk"));
			break;
		case 5:
			npc.setNextForceTalk(new ForceTalk("MUAHAHAHAHAAA!"));
			break;
		}
		target.gfx(new Graphics(337));
		Hit magicHit = npc.magicHit(target, defs.getMaxHit());
		delayHit(npc, target, 0, magicHit);
		return npc.getAttackSpeed();
	}
}
