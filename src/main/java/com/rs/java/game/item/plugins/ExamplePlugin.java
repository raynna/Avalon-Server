package com.rs.java.game.item.plugins;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemId;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;

public class ExamplePlugin extends ItemPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] {ItemId.A_KEY_293 };
	}
	@Override
	public boolean processItem(Player player, Item item, int slotId, String option) {
		switch (option) {
			case "activate":
				//do activate
				return true;
			case "drop":
				//do drop
				return true;
		}
		return false;
	}
	@Override
	public boolean processItemOnItem(Player player, Item item, Item item2, int itemUsed, int usedWith) {
		return true;
	}
	@Override
	public boolean processDestroy(Player player, Item item, int slotId) {
		return true;
	}
}