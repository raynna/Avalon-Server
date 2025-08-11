package com.rs.java.game.objects.plugins;

import com.rs.java.game.Animation;
import com.rs.java.game.World;
import com.rs.java.game.WorldObject;
import com.rs.java.game.item.Item;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.CrystalChest;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;

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
		player.animate(new Animation(536));
		player.lock(2);
		player.getPackets().sendGameMessage("You attempt to unlock the chest...");
		WorldObject openedChest = new WorldObject(173, object.getType(), object.getRotation(), object);
		if (World.removeObjectTemporary(object, 1200))
			World.spawnObject(openedChest);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getInventory().deleteItem(CRYSTAL_KEY, 1);
				CrystalChest.sendRewards(false, player);
			}
		}, 1);
	}
}
