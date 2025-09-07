package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class BloodragerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
				{ 11106, 11108, 11110, 11112, 11114, 11116, 11118, 11120, 11122, 11124, 11126 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NpcCombatDefinition def = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int tier = (npc.getId() - 11106) / 2;

		int damage = 0;
		if (usingSpecial) {
			npc.gfx(new Graphics(2444));
			damage = (int) (npc.getMaxHit() * (1.05 * tier));
		} else
			damage = def.getMaxHit();
		delayHit(npc, target, usingSpecial ? 1 : 0, npc.meleeHit(npc, damage));
		npc.animate(new Animation(13617));
		return npc.getAttackSpeed();
	}
}
