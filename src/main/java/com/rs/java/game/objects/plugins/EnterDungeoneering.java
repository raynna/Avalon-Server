package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;

public class EnterDungeoneering extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 48496 };
	}
	
	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getDungManager().enterDungeon(true, false);
		return true;
	}
}
