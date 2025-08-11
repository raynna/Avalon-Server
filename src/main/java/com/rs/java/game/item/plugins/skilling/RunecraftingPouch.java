package com.rs.java.game.item.plugins.skilling;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemId;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.runecrafting.RunecraftingPouches;

public class RunecraftingPouch extends ItemPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] {ItemId.SMALL_POUCH_5509, ItemId.MEDIUM_POUCH_5510, ItemId.LARGE_POUCH_5512, ItemId.GIANT_POUCH_5514 };
	}

	public int getPouchSize(Item item) {
		int size = -1;
		if (item.getId() == ItemId.SMALL_POUCH_5509)
			size = 0;
		if (item.getId() == ItemId.MEDIUM_POUCH_5510)
			size = 1;
		if (item.getId() == ItemId.LARGE_POUCH_5512)
			size = 2;
		if (item.getId() == ItemId.GIANT_POUCH_5514)
			size = 3;
		return size;
	}

	@Override
	public boolean processItem(Player player, Item item, int slotId, String option) {
		int pouch = getPouchSize(item);
		switch (option) {
			case "check":
				RunecraftingPouches.checkPouch(player, pouch);
				return true;
			case "fill":
				RunecraftingPouches.fillPouch(player, pouch);
				return true;
			case "empty":
				RunecraftingPouches.emptyPouch(player, pouch);
				return true;
		}
		return false;
	}
}
