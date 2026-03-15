package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.DwarfMultiCannon;

public class DwarfCannon extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { "object.dwarf_multicannon", "object.gold_dwarf_multicannon", "object.royale_dwarf_multicannon"};
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		DwarfMultiCannon.fire(player, object);
		return true;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		DwarfMultiCannon.pickupCannon(player, 4, object, 0);
		return true;
	}
}
