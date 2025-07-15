package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;

public class CruicibleHatch extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 67051, "Hatch" };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		player.getDialogueManager().startDialogue("Marv", true);
		return true;
	}
}

