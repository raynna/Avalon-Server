package com.rs.java.game.npc.others;

import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

public class Rocks extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = -6635229612876021731L;

	public Rocks(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		if (getId() == 1265 || getId() == 1267)
			this.transformIntoNPC(getId() + 1);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		for (Player players : World.getPlayers()) {
			if (Utils.inCircle(players, this, 1) && players.getAttackedByDelay() < Utils.currentTimeMillis()) {
				if (getId() == 1266 || getId() == 1268) {
					NPC crab = new NPC(getId() - 1, this, -1, true);
					finish();
					crab.setTarget(players);
					crab.setBonuses();
					crab.setHitpoints(crab.getMaxHitpoints());
					return;
				}
			}
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}
}
