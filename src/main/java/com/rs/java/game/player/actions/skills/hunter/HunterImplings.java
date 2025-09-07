package com.rs.java.game.player.actions.skills.hunter;

import com.rs.Settings;
import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.Drop;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

public class HunterImplings {

	public static void sendDrop(Player player, Drop drop) {
		Item item = ItemDefinitions.getItemDefinitions(drop.getItemId()).isStackable()
				? new Item(drop.getItemId(),
						(drop.getMinAmount() * Settings.DROP_RATE)
								+ Utils.getRandom(drop.getExtraAmount() * Settings.DROP_RATE))
				: new Item(drop.getItemId(), (drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount())));
		World.updateGroundItem(new Item(item.getId(), item.getAmount()),
				new WorldTile(player.getX() + 1, player.getY(), player.getPlane()), player, 1, 0);
	}
}