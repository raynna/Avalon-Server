package com.rs.java.game.npc.others;

import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.controlers.RunespanControler;
import com.rs.java.utils.Utils;

@SuppressWarnings("serial")
public class YellowWizard extends NPC {

	private RunespanControler controler;
	private long spawnTime;

	public YellowWizard(WorldTile tile, RunespanControler controler) {
		super(15430, tile, -1, true, true);
		spawnTime = Utils.currentTimeMillis();
		this.controler = controler;
	}

	@Override
	public void processNPC() {
		if (spawnTime + 300000 < Utils.currentTimeMillis())
			finish();
	}

	@Override
	public void finish() {
		controler.removeWizard();
		transformIntoNPC(-1);
		super.finish();
	}

	public static void giveReward(Player player) {

	}

	@Override
	public boolean withinDistance(Player tile, int distance) {
		return tile == controler.getPlayer() && super.withinDistance(tile, distance);
	}

}
