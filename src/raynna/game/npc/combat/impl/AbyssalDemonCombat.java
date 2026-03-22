package raynna.game.npc.combat.impl;

import raynna.game.Entity;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.util.Utils;
import raynna.game.npc.combatdata.CombatData;
import raynna.game.npc.combatdata.NpcAttackStyle;

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