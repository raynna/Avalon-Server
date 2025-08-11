package com.rs.java.game.player.dialogues;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.item.Item;

public class ClaimClanItem extends Dialogue {

	@Override
	public void start() {
		int npcId = (Integer) this.parameters[0];
		int itemId = (Integer) this.parameters[1];
		if (player.getClanManager() == null) {
			sendNPCDialogue(npcId, 9828, "Talk to me once you have a clan.");
			return;
		}
		sendNPCDialogue(npcId, 9828,
				"Enjoy your " + ItemDefinitions.getItemDefinitions(itemId).getName().toLowerCase() + "!");
		player.getInventory().addItem(new Item(itemId));
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}
}
