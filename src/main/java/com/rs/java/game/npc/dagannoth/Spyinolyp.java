package com.rs.java.game.npc.dagannoth;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;

@SuppressWarnings("serial")
public class Spyinolyp extends NPC {

	public Spyinolyp(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, true, spawned);
		setForceTargetDistance(12);
		setCantFollowUnderCombat(true);
		setForceAgressiveDistance(8);
		setRandomWalk(0);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (getId() == 2892)
			return;

	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}

}
