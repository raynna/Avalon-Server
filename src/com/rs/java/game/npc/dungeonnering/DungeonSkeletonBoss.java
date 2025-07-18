package com.rs.java.game.npc.dungeonnering;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;

@SuppressWarnings("serial")
public class DungeonSkeletonBoss extends DungeonNPC {

	private DivineSkinweaver boss;

	public DungeonSkeletonBoss(int id, WorldTile tile, DungeonManager manager, RoomReference reference, double multiplier) {
		super(id, tile, manager, multiplier);
		setForceAgressive(true);
		setIntelligentRouteFinder(true);
		setLureDelay(0);
		boss = (DivineSkinweaver) getNPC(10058);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		boss.removeSkeleton(this);
	}

	@Override
	public void drop() {

	}

}
