package com.rs.java.game.npc.dagannoth;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;

@SuppressWarnings("serial")
public class DagannothKings extends NPC {

	public DagannothKings(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, true, spawned);
		setIntelligentRouteFinder(false);
		setForceTargetDistance(32);
		setCantFollowUnderCombat(false);
		setForceAgressiveDistance(8);
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		super.processNPC();
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}

	@Override
	public double getProtectionPrayerEffectiveness() {
		return 0.1;
	}

}
