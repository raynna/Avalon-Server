package com.rs.java.game.npc.combat.impl;

import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

import static com.rs.java.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class YtMejKotCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Yt-MejKot" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		delayHit(npc, target, 0,
                getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target)));
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
		return npc.getAttackSpeed();
	}
}
