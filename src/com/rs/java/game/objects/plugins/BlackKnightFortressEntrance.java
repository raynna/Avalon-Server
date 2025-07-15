package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;

public class BlackKnightFortressEntrance extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2337, 2341, 2338 };
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		DoorsAndGates.handleDoorTemporary(player, object, 1200);
		return true;
	}
}
