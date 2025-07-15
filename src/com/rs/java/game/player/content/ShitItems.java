package com.rs.java.game.player.content;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.player.Player;

public class ShitItems {

	public enum bannedItems {

		STAFF_OF_LIGHT(15486), BANDOS_CHESTPLATE(11724);

		private int itemId;

		private bannedItems(int itemId) {
			this.itemId = itemId;
		}

		public int getItemId() {
			return itemId;
		}

		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

	}

	public static boolean checkSpawnable(Player player, String name) {
		ItemDefinitions item = ItemDefinitions.forName(name.toLowerCase());
		for (bannedItems bannedItems : bannedItems.values()) {
			if (item.getId() == bannedItems.getItemId())
				return false;
		}
		return true;
	}
}
