package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;

public class KalphiteLairEntrance extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 48802, 48803 };
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		if (object.getId() == 48802)
			player.setNextWorldTile(new WorldTile(3484, 9510, 2));
		else
			player.setNextWorldTile(new WorldTile(3508, 9493, 0));
		return true;
	}

	@Override
	public boolean processItemOnObject(Player player, WorldObject object, Item item) {
		if (item.getId() != 954)
			return false;
		player.getInventory().deleteItem(954, 1);
		if (object.getId() == 48803)
			player.getVarsManager().sendVarBit(7263, 1, true);
		else
			player.getVarsManager().sendVarBit(7262, 1, true);
		return true;

	}
}
