package com.rs.java.game.npc.dungeonnering;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;

@SuppressWarnings("serial")
public class FleshspoilerSpawn extends DungeonNPC {

	private FleshspoilerHaasghenahk boss;

	public FleshspoilerSpawn(FleshspoilerHaasghenahk boss, int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
		this.boss = boss;
		setForceAgressive(true);
		setLureDelay(Integer.MAX_VALUE);
		setForceFollowClose(true);
		setRun(true);
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.0;//Fully block it.
	}

	@Override
	public void drop() {

	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		boss.removeFleshCreature(this);
	}
}
