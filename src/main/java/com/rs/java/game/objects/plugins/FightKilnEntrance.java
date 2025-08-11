package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.controlers.FightKiln;

public class FightKilnEntrance extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 68107 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		FightKiln.enterFightKiln(player, false);
		return true;
	}
	
	@Override
	public boolean processObject2(Player player, WorldObject object) {
		FightKiln.enterFightKiln(player, true);
		return true;
	}
}
