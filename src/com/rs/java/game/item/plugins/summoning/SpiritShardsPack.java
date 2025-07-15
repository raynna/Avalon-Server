package com.rs.java.game.item.plugins.summoning;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;

public class SpiritShardsPack extends ItemPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 12183 };
	}
	@Override
	public boolean processItem(Player player, Item item, int slotId, String option) {
		switch (option) {
			case "open":
				if (player.getInventory().getNumberOf(item.getId()) > 1 && !player.getInventory().containsOneItem(12183) && !player.getInventory().hasFreeSlots()) {
					player.message("You don't have enough inventory space to open this pack.");
					return true;
				}
				if (player.getInventory().getNumberOf(15262) + 5000 < 0) {
					player.getPackets().sendGameMessage("You don't have enough inventory space to open this pack.");
					return true;
				}
				player.getInventory().deleteItem(item.getId(), 1);
				player.getInventory().addItem(12183, 5000);
				return true;
			case "open-all":
				int packs = player.getInventory().getAmountOf(15262);
				int amount = packs * 5000;
				int shards = player.getInventory().getAmountOf(12183);
				if (shards + 5000 == Integer.MAX_VALUE || (!player.getInventory().containsOneItem(12183) && !player.getInventory().hasFreeSlots() && packs > 1)) {
					player.getPackets().sendGameMessage("You don't have enough inventory space to open any packs.");
					return true;
				}
				if (shards + amount < 0) {
					packs = (Integer.MAX_VALUE - shards) / 5000;
					amount = packs * 5000;
					player.getPackets().sendGameMessage("You don't have enough inventory space to open all packs.");
				}
				player.getInventory().deleteItem(15262, packs);
				player.getInventory().addItem(12183, amount);
				return true;
		}
		return false;
	}
}