package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.thieving.Thieving;

public class ThievingStalls extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 34384, 34383, 14011, 7053, 34387, 34386, 34385, null };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		Thieving.handleStalls(player, object);
		return true;
	}
}
