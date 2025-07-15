package com.rs.java.game.player.dialogues;

import com.rs.java.game.minigames.castlewars.CastleWars;

public class CastleWarsScoreboard extends Dialogue {

	@Override
	public void start() {
		CastleWars.viewScoreBoard(player);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();

	}

	@Override
	public void finish() {

	}

}
