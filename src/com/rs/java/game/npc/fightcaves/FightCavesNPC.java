package com.rs.java.game.npc.fightcaves;

import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;

@SuppressWarnings("serial")
public class FightCavesNPC extends NPC {

	public FightCavesNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
		setForceAgressive(true);
		setForceTargetDistance(64);
		setForceAgressiveDistance(64);
	}

	@Override
	public void sendDeath(Entity source) {
		gfx(new Graphics(2924 + getSize()));
		super.sendDeath(source);
	}
}
