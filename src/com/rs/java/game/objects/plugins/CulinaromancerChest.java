package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.utils.ShopsHandler;

public class CulinaromancerChest extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 12309 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getBank().openBank();
		return true;
	}
	
	@Override
	public boolean processObject2(Player player, WorldObject object) {
		ShopsHandler.openShop(player, 34);
		return true;
	}
	
	@Override
	public boolean processObject3(Player player, WorldObject object) {
		ShopsHandler.openShop(player, 34);
		return true;
	}
}
