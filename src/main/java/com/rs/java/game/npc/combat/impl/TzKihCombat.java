package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.Player;

public class TzKihCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tz-Kih", 7361, 7362 };
	}

	@Override
	public int attack(NPC npc, Entity target) {// yoa
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage = 0;
		if (npc instanceof Familiar) {// TODO get anim and gfx
			Familiar familiar = (Familiar) npc;
			boolean usingSpecial = familiar.hasSpecialOn();
			if (usingSpecial) {
				for (Entity entity : npc.getPossibleTargets()) {
					damage = getRandomMaxHit(npc, 70, NPCCombatDefinitions.MELEE, target);
					if (target instanceof Player)
						((Player) target).getPrayer().drainPrayer(damage);
					delayHit(npc, entity, 0, getMeleeHit(npc, damage));
				}
			}
			return defs.getAttackDelay();
		}
		npc.animate(new Animation(defs.getAttackEmote()));
		damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		if (target instanceof Player)
			((Player) target).getPrayer().drainPrayer(damage + 10);
		delayHit(npc, target, 0, getMeleeHit(npc, damage));
		return defs.getAttackDelay();
	}
}
