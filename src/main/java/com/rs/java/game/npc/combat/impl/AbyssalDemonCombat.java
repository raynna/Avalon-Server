package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.utils.Utils;

/**
 * @author Savions Sw
 *
 * @since December 2012
 */
public class AbyssalDemonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 1615 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), -1, target)));
		if (Utils.getRandom(2) == 0)
			teleport(npc);
		return defs.getAttackDelay();
	}

	private void teleport(Entity character) {
		for (int c = 0; c < 10; c++) {
			int dir = 1 + Utils.random(1);
			if (World.checkWalkStep(character.getPlane(), character.getX(), character.getY(), dir, 1)) {
				character.setNextWorldTile(new WorldTile(character.getX() + Utils.DIRECTION_DELTA_X[dir],
						character.getY() + Utils.DIRECTION_DELTA_Y[dir], character.getPlane()));
				character.gfx(new Graphics(409));
				break;
			}
		}
	}
}