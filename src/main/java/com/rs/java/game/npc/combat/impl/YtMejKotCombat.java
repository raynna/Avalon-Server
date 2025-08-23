package com.rs.java.game.npc.combat.impl;

import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;

public class YtMejKotCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Yt-MejKot" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackEmote()));
		delayHit(npc, target, 0,
                getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(), target)));
		if (npc.getHitpoints() < npc.getMaxHitpoints() / 2) {
			if (npc.temporaryAttribute().remove("Heal") != null) {
				npc.gfx(new Graphics(2980, 0, 100));
				List<Integer> npcIndexes = World.getRegion(npc.getRegionId()).getNPCsIndexes();
				if (npcIndexes != null) {
					for (int npcIndex : npcIndexes) {
						NPC n = World.getNPCs().get(npcIndex);
						if (n == null || n.isDead() || n.hasFinished())
							continue;
						n.heal(100);
					}
				}
			} else
				npc.temporaryAttribute().put("Heal", Boolean.TRUE);
		}
		return defs.getAttackDelay();
	}
}
