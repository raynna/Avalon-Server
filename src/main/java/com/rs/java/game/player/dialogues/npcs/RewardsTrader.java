package com.rs.java.game.player.dialogues.npcs;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.core.cache.defintions.NPCDefinitions;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.itemdegrading.ItemDegrade.DegradeData;
import com.rs.java.game.player.dialogues.Dialogue;
import com.rs.java.utils.Utils;

/**
 * @author -Andreas 2 feb. 2020 14:40:02
 * @project 1. Avalon
 * 
 */

public class RewardsTrader extends Dialogue {

	private int npcId;
	private int chatType;
	private Item item;
	//TODO REGULAR & RECHARGE DIALOGUE
	private int /*REGULAR = 0, RECHARGE = 1, */USE_ITEM = 2, FULLY_CHARGED = 3, WRONG_ITEM = 4;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		item = (Item) parameters[1];
		chatType = (Integer) parameters[2];
		String npcName = NPCDefinitions.getNPCDefinitions(npcId).getName();
		String itemName = ItemDefinitions.getItemDefinitions(item.getId()).getName();
		if (chatType == WRONG_ITEM) {
			stageName = "end";
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { npcName, "You can't recharge " + itemName + "." },
					IS_NPC, npcId, 9827);
		}
		if (chatType == FULLY_CHARGED) {
			stageName = "end";
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { npcName, itemName + " is already fully charged." },
					IS_NPC, npcId, 9827);
		}
		if (chatType == USE_ITEM) {
			stageName = "USE_ITEM";
			sendEntityDialogue(SEND_2_TEXT_CHAT,
					new String[] { npcName, "Fixing it is possible, yes, The cost is up to you." }, IS_NPC, npcId,
					9827);
		}
		// TODO REGULAR CHATDIALOGUE
		// TODO RECHARGE CHATDIALOGUE
	}

	private DegradeData[] data = DegradeData.values();

	@Override
	public void run(int interfaceId, int componentId) {
		String npcName = NPCDefinitions.getNPCDefinitions(npcId).getName();
		if (stageName == "USE_ITEM") {
			stageName = "USE_ITEM_2";
			sendOptionsDialogue("Pick your chosen price to pay:",
					Utils.getFormattedNumber(getPrice(item, false), ',') + " gp.",
					Utils.getFormattedNumber(getPrice(item, true), ',') + " tokens.", "No way!");
		}
		if (stageName == "USE_ITEM_2") {
			if (componentId == OPTION_1) {
				stageName = "end";
				if (player.canBuy(getPrice(item, false))) {
					sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { npcName, "All done. Moveon. Please..." },
							IS_NPC, npcId, 9827);
					for (DegradeData data : data) {
						if (data.getCurrentItem().getId() == item.getId()
								|| (data.getDegradedItem() != null && data.getDegradedItem().getId() == item.getId())) {
							player.getInventory().deleteItem(item.getId(), 1);
							player.getChargeManager().setCharges(data.getCurrentItem().getId(), data.getHits());
							player.getInventory().addItem(data.getCurrentItem().getId(), 1);
						}
					}
				} else {
					sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { npcName, "You can't afford that." }, IS_NPC,
							npcId, 9827);
				}
			} else if (componentId == OPTION_2) {
				stageName = "end";
				if (player.getAvalonPoints() >= getPrice(item, true)) {
					player.setAvalonPoints(player.getAvalonPoints() - getPrice(item, true));
					sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { npcName, "All done. Moveon. Please..." },
							IS_NPC, npcId, 9827);
					for (DegradeData data : data) {
						if (data.getCurrentItem().getId() == item.getId()
								|| (data.getDegradedItem() != null && data.getDegradedItem().getId() == item.getId())) {
							player.getInventory().deleteItem(item.getId(), 1);
							player.getChargeManager().setCharges(data.getCurrentItem().getId(), data.getHits());
							player.getInventory().addItem(data.getCurrentItem().getId(), 1);
						}
					}
				} else {
					sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { npcName, "You can't afford that." }, IS_NPC,
							npcId, 9827);
				}
			} else if (componentId == OPTION_3) {
				stageName = "end";
			}
		} else if (stageName == "end")
			end();
	}

	private int getPrice(Item item, boolean tokens) {
		return player.getChargeManager().getPercentage(item, true) * (tokens ? 200 : 20000);
	}

	@Override
	public void finish() {

	}
}