package com.rs.java.game.player.content;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.player.Player;

public final class SkillsDialogue {

	public static final int MAKE = 0, MAKE_SETS = 1, COOK = 2, ROAST = 3, OFFER = 4, SELL = 5, BAKE = 6, CUT = 7,
			DEPOSIT = 8, MAKE_NO_ALL_NO_CUSTOM = 9, TELEPORT = 10, SELECT = 11, TAKE = 13;

	public static interface ItemNameFilter {

		public String rename(String name);
	}

	public static void sendSkillsDialogue(Player player, int option, String explanation, int maxQuantity, int[] items,
			ItemNameFilter filter) {
		sendSkillsDialogue(player, option, explanation, maxQuantity, items, filter, true);
	}

	public static void sendSkillsDialogue(Player player, int option,
			String explanation, int maxQuantity, int[] items,
			ItemNameFilter filter, boolean sendQuantitySelector) {
		player.getInterfaceManager().sendChatBoxInterface(905);
		player.getPackets().sendInterface(true, 905, 4, 916);
		if (!sendQuantitySelector) {
			maxQuantity = -1;
			player.getPackets().sendHideIComponent(916, 4, true);
			player.getPackets().sendHideIComponent(916, 9, true);
		} else {
			if (option != MAKE_SETS && option != MAKE_NO_ALL_NO_CUSTOM)
				player.getPackets().sendUnlockIComponentOptionSlots(916, 8, -1,
						0, 0); // unlocks all option
		}
		player.getPackets().sendIComponentText(916, 6, explanation);
		player.getPackets().sendGlobalConfig(754, option);
		for (int i = 0; i < 10; i++) {
			if (i >= items.length) {
				player.getPackets().sendGlobalConfig(
						i >= 6 ? (1139 + i - 6) : 755 + i, -1);
				continue;
			}
			player.getPackets().sendGlobalConfig(
					i >= 6 ? (1139 + i - 6) : 755 + i, items[i]);
			String name = ItemDefinitions.getItemDefinitions(items[i])
					.getName();
			if (filter != null)
				name = filter.rename(name);
			player.getPackets().sendGlobalString(
					i >= 6 ? (280 + i - 6) : 132 + i, name);
		}
		setMaxQuantity(player, maxQuantity);
		setQuantity(player, maxQuantity);
	}
	
	public static void sendStoreDialogue(Player player, String explanation, int[] items,
			ItemNameFilter filter) {
		sendStoreDialogue(player, explanation, items, filter, true);
	}
	
	public static void sendStoreDialogue(Player player,
			String explanation, int[] items,
			ItemNameFilter filter, boolean sendQuantitySelector) {
		player.getInterfaceManager().sendChatBoxInterface(905);
		player.getPackets().sendInterface(true, 905, 4, 916);
		player.getPackets().sendHideIComponent(916, 4, true);
		player.getPackets().sendHideIComponent(916, 9, true);
		for (int i = 15; i < 27; i++)
		player.getPackets().sendHideIComponent(916, i, true);
		player.getPackets().sendIComponentText(916, 6, explanation);
		player.getPackets().sendGlobalConfig(754, -1);
		for (int i = 0; i < 10; i++) {
			if (i >= items.length) {
				player.getPackets().sendGlobalConfig(
						i >= 6 ? (1139 + i - 6) : 755 + i, -1);
				continue;
			}
			player.getPackets().sendGlobalConfig(
					i >= 6 ? (1139 + i - 6) : 755 + i, items[i]);
			String name = ItemDefinitions.getItemDefinitions(items[i])
					.getName();
			if (filter != null)
				name = filter.rename(name);
			player.getPackets().sendGlobalString(
					i >= 6 ? (280 + i - 6) : 132 + i, name);
		}
	}

	public static void handleSetQuantityButtons(Player player, int componentId) {
		if (componentId == 10)
			setQuantity(player, 1, false);
		else if (componentId == 11)
			setQuantity(player, 5, false);
		else if (componentId == 12)
			setQuantity(player, 10, false);
		else if (componentId == 8)//TODO Unlock All Button
			setQuantity(player, getMaxQuantity(player), false);
		else if (componentId == 24)
			setQuantity(player, getQuantity(player) + 1, false);
		else if (componentId == 25)
			setQuantity(player, getQuantity(player) - 1, false);
		player.temporaryAttribute().put("SkillsDialogueMaxQuantity", getQuantity(player));
	}

	public static void setMaxQuantity(Player player, int maxQuantity) {
		player.temporaryAttribute().put("SkillsDialogueMaxQuantity", maxQuantity);
		player.getPackets().sendConfigByFile(8094, maxQuantity);
	}

	public static void setQuantity(Player player, int quantity) {
		setQuantity(player, quantity, true);
	}

	public static void setQuantity(Player player, int quantity, boolean refresh) {
		int maxQuantity = getMaxQuantity(player);
		if (quantity > maxQuantity)
			quantity = maxQuantity;
		else if (quantity < 0)
			quantity = 0;
		player.temporaryAttribute().put("SkillsDialogueQuantity", quantity);
		if (refresh)
			player.getPackets().sendConfigByFile(8095, quantity);
	}

	public static int getMaxQuantity(Player player) {
		Integer maxQuantity = (Integer) player.temporaryAttribute().get("SkillsDialogueMaxQuantity");
		if (maxQuantity == null)
			return 0;
		return maxQuantity;
	}

	public static int getQuantity(Player player) {
		Integer quantity = (Integer) player.temporaryAttribute().get("SkillsDialogueQuantity");
		if (quantity == null)
			return 0;
		return quantity;
	}

	public static int getItemSlot(int componentId) {
		if (componentId < 14)
			return 0;
		return componentId - 14;
	}

	private SkillsDialogue() {

	}
}
