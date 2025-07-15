package com.rs.java.game.player.dialogues.dungeoneering;

import com.rs.java.game.player.controlers.DungeonControler;
import com.rs.java.game.player.dialogues.Dialogue;

public class DungeonLeave extends Dialogue {

	private DungeonControler dungeon;

	@Override
	public void start() {
		dungeon = (DungeonControler) parameters[0];
		sendOptionsDialogue("Leave the dungeon permanently?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1)
			dungeon.leaveDungeonPermanently();
		end();
	}

	@Override
	public void finish() {

	}

}
