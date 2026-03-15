package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.kotlin.game.player.transportation.FairyRings;

public class FairyRingObject extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { "object_group.fairy_ring_use" }; }

	@Override
	public boolean processObject(Player player, WorldObject object) {
		FairyRings.Companion.openRingInterface(player, object);
		return true;
	}
}
