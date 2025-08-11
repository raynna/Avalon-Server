package com.rs.java.game.player.dialogues;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPluginLoader;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.utils.Logger;

public class DestroyItemOption extends Dialogue {

	int slotId;
	Item item;

	@Override
	public void start() {
		slotId = (Integer) parameters[0];
		item = (Item) parameters[1];
		player.getInterfaceManager().sendChatBoxInterface(1183);
		player.getPackets().sendTextOnComponent(1183, 22,
				"Are you sure you want to destroy your " + item.getName() + "?");
		player.getPackets().sendTextOnComponent(1183, 7, item.getName());
		player.getPackets().sendItemOnIComponent(1183, 13, item.getId(), 1);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == 1183 && componentId == 9) {
			ItemPlugin plugin = ItemPluginLoader.getPlugin(item);
			if (plugin != null) {
				boolean pluginExecuted = plugin.processDestroy(player, item, slotId);
				if (!pluginExecuted) {
					Logger.log("ItemPlugin", "Destroy - Class: " + plugin.getClass().getSimpleName() + ".java, Failed: " + item.getName() + "(" + item.getId() + ") plugin does not have this option.");
				}
				if (pluginExecuted) {
					Logger.log("ItemPlugin", "Destroy - Class: " + plugin.getClass().getSimpleName() + ".java, Executed: " + item.getName() + "(" + item.getId() + ")");
					end();
					return;
				}
			}
			player.getInventory().deleteItem(slotId, item);
			player.getChargeManager().breakItem(item);
			player.getPackets().sendSound(4500, 0, 1);
		}
		end();
	}

	@Override
	public void finish() {

	}

}
