package com.rs.java.game.npc.others;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;

@SuppressWarnings("serial")
public class Dummy extends NPC {

	public Dummy(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(0);
		setIntelligentRouteFinder(false);
		setForceAgressive(false);
		setCantFollowUnderCombat(true);
		setHitpoints(10000);
		setForceMultiAttacked(true);
	}

	 @Override
	 public void setTarget(Entity entity) {
		//do nothing
	 }

	@Override
	public void processNPC() {
		//if (isDead())
		//	return;
		//super.processNPC();
	}

	@Override
	public void sendDeath(Entity source) {
		heal(10000);
	}

}
