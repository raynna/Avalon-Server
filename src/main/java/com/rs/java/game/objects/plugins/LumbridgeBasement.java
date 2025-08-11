package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;

public class LumbridgeBasement extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 36687, 29355 };
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		if (object.getId() == 29355 && object.getX() == 3209 && object.getY() == 9616)
			player.useStairs(828, new WorldTile(3210, 3216, 0), 1, 2);
		else
			player.useStairs(828, new WorldTile(3208, 9616, 0), 1, 2);
		return true;
	}
}
