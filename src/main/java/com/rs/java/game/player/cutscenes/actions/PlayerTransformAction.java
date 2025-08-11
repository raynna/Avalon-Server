package com.rs.java.game.player.cutscenes.actions;

import com.rs.java.game.player.Player;

public class PlayerTransformAction extends CutsceneAction {

	private int npcId;

	public PlayerTransformAction(int npcId, int actionDelay) {
		super(-1, actionDelay);
		this.npcId = npcId;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.getAppearence().transformIntoNPC(npcId);
	}

}
