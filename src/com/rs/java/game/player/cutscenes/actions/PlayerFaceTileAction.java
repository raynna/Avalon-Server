package com.rs.java.game.player.cutscenes.actions;

import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.cutscenes.Cutscene;

public class PlayerFaceTileAction extends CutsceneAction {

	private int x, y;

	public PlayerFaceTileAction(int x, int y, int actionDelay) {
		super(-1, actionDelay);
		this.x = x;
		this.y = y;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		player.setNextFaceWorldTile(new WorldTile(scene.getBaseX() + x, scene.getBaseY() + y, player.getPlane()));
	}

}
