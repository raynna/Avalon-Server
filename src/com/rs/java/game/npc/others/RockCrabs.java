package com.rs.java.game.npc.others;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;

public class RockCrabs extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 1776392517680641886L;

	public RockCrabs(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}
}
