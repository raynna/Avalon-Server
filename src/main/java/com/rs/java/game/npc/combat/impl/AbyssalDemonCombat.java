package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.CombatData;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;

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
		npc.animate(npc.getAttackAnimation());
		CombatData data = npc.getCombatData();
		hit(0, npc, target, NpcAttackStyle.STAB);
		if (Utils.getRandom(2) == 0)
			teleport(npc);
		return data.attackSpeedTicks;
	}

	private void teleport(Entity character) {
		final int maxAttempts = 10;
		int plane = character.getPlane();
		int x = character.getX();
		int y = character.getY();

		for (int i = 0; i < maxAttempts; i++) {
			int dir = Utils.random(Utils.DIRECTION_DELTA_X.length);
			int newX = x + Utils.DIRECTION_DELTA_X[dir];
			int newY = y + Utils.DIRECTION_DELTA_Y[dir];

			if (World.checkWalkStep(plane, x, y, dir, 1)) {
				character.setNextWorldTile(new WorldTile(newX, newY, plane));
				character.gfx(409);
				return;
			}
		}
	}
}