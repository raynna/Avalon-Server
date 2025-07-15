package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;

public class BrokenStairsDungeoneering extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 50552 };
	}
	
	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.setNextWorldTile(new WorldTile(player.getX(), player.getY() + 2, player.getPlane() - 1));
		return true;
	}
}
