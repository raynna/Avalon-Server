package com.rs.java.game.objects.plugins;

import com.rs.java.game.Animation;
import com.rs.java.game.World;
import com.rs.java.game.WorldObject;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ground.GroundItems;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.kotlin.game.npc.drops.Drop;
import com.rs.kotlin.game.npc.drops.tables.object.CrystalChestTable;

import java.util.List;

public class CrystalChestObject extends ObjectPlugin {

	private final int CRYSTAL_CHEST = 172, CRYSTAL_KEY = 989;

	@Override
	public Object[] getKeys() {
		return new Object[] { CRYSTAL_CHEST };
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		if (!player.getInventory().containsItem(CRYSTAL_KEY, 1)) {
			player.getPackets().sendGameMessage("You need a crystal key to open this chest.");
			return false;
		}
		openChest(player, object);
		return true;
	}

	@Override
	public boolean processItemOnObject(Player player, WorldObject object, Item item) {
		if (item.getId() != CRYSTAL_KEY)
			return false;
		openChest(player, object);
		return true;
	}

	public void openChest(Player player, WorldObject object) {
		if (!player.getInventory().hasFreeSlots()) {
			player.message("You don't have any space in your inventory.");
			return;
		}
		player.animate(new Animation(536));
		player.lock(2);
		player.getPackets().sendGameMessage("You attempt to unlock the chest...");
		WorldObject openedChest = new WorldObject(173, object.getType(), object.getRotation(), object);
		World.replaceObjectTemporary(object, openedChest, 1, () -> {
			player.getInventory().deleteItem(CRYSTAL_KEY, 1);
			player.getKillcount().increment("Crystal chest");
			List<Drop> drops = CrystalChestTable.INSTANCE.getTable().rollDrops(player, 0);
			for (Drop drop : drops) {
				if (!player.inventory.canHold(drop.itemId, drop.amount)) {
					GroundItems.updateGroundItem(new Item(drop.itemId, drop.amount), player.getLocation(), player, 60);
					continue;
				}
				player.getInventory().addItem(drop.itemId, drop.amount);
			}
		});
	}
}
