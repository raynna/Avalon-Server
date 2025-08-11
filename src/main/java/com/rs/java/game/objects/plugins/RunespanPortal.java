package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;

public class RunespanPortal extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 38279 };
	}
	
	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getDialogueManager().startDialogue("RunespanPortalD");
		return true;
	}
}
