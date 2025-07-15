package com.rs.java.game.npc.pest;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.minigames.pest.PestControl;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

@SuppressWarnings("serial")
public class Shifter extends PestMonsters {

	public Shifter(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
			int index, PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, index, manager);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		Entity target = this.getPossibleTargets().get(0);
		if (this.getCombat().process() && !this.withinDistance(target, 10) || Utils.random(15) == 0)
			teleportSpinner(target);
	}

	private void teleportSpinner(WorldTile tile) { // def 3902, death 3903
		setNextWorldTile(tile);
		animate(new Animation(3904));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				gfx(new Graphics(654));// 1502
			}
		});
	}
}
