package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;

public class AbbysalTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7350, 7349 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(7980);
		npc.gfx(1490);
		Hit meleeHit = npc.meleeHit(target, 140);
		if (target instanceof Player player) {
            if (meleeHit.getDamage() > 0 && player.getPrayer().getPrayerPoints() > 0)
				player.getPrayer().drainPrayer(meleeHit.getDamage()  / 2);
		}
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}
}
