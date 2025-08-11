package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.PartyRoom;

public class PartyRoomChest extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2418 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		PartyRoom.openPartyChest(player);
		return true;
	}
}

