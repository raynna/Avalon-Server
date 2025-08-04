package com.rs.java.game.player;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.rs.Settings;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemsContainer;
import com.rs.java.game.item.meta.MetaDataType;
import com.rs.java.game.player.content.ItemConstants;
import com.rs.java.game.player.content.grandexchange.GrandExchange;
import com.rs.java.utils.*;
import com.rs.kotlin.Rscm;

public final class Inventory implements Serializable {

	private static final long serialVersionUID = 8842800123753277093L;

	public ItemsContainer<Item> items;

	private transient Player player;

	public static final int INVENTORY_INTERFACE = 679;

	public static final int RUNE_POUCH = 24497;

	public Inventory() {
		items = new ItemsContainer<Item>(28, false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	

	public void init() {
		Item[] finalised = new Item[32];
		for (int i = 0; i < 28; i++) {
			finalised[i] = items.get(i);
		}
		if (player.getInventory().containsOneItem(RUNE_POUCH)) {
			ItemsContainer<Item> pouchRunes = player.getRunePouch();
			for (int i = 0; i < 3; i++) {
				Item rune = pouchRunes.get(i);
				finalised[29 + i] = (rune != null) ? rune.clone() : null;
			}
		} else {
			Arrays.fill(finalised, 29, 32, null);
		}
		player.getPackets().sendItems(93, finalised);
	}

	public void unlockInventoryOptions() {
		player.getPackets().sendComponentSettings(INVENTORY_INTERFACE, 0, 0, 27, 4554126);
		player.getPackets().sendComponentSettings(INVENTORY_INTERFACE, 0, 28, 55, 2097152);
	}

	public void reset() {
		items.reset();
		init();
	}

	public void refresh(int... slots) {
		Item[] finalised = new Item[32];
		for (int i = 0; i < 28; i++) {
			finalised[i] = items.get(i);
		}
		if (player.getInventory().containsOneItem(RUNE_POUCH)) {
			ItemsContainer<Item> pouchRunes = player.getRunePouch();
			for (int i = 0; i < 3; i++) {
				Item rune = pouchRunes.get(i);
				finalised[29 + i] = (rune != null) ? rune.clone() : null;
				player.getPackets().sendUpdateItems(93, finalised, 29 + i);
			}
		} else {
			Arrays.fill(finalised, 29, 32, null);
		}
		player.getPackets().sendUpdateItems(93, finalised, slots);
	}

	public long getInventoryValue() {
		long value = 0;
		for (Item inventory : player.getInventory().getItems().toArray()) {
			if (inventory == null)
				continue;
			long amount = inventory.getAmount();
			value += inventory.getDefinitions().getTipitPrice() * amount;
		}
		return value;
	}

	public boolean addItemDrop(final int itemId, final int amount, final WorldTile tile) {
		if (itemId < 0 || amount < 1 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount))) {
			World.updateGroundItem(new Item(itemId, amount), player, player, 60, 0);
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItemDrop(int itemId, int amount) {
		return addItemDrop(itemId, amount, new WorldTile(player));
	}

	public boolean addItemFromEquipment(final int itemId, final int amount) {
		if (itemId < 0 || amount < 0 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItemFromEquipment(Item item) {
		if (item == null || item.getId() < 0 || item.getAmount() <= 0 || !Utils.itemExists(item.getId())
				|| !player.getControlerManager().canAddInventoryItem(item.getId(), item.getAmount()))
			return false;

		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			item.setAmount(items.getFreeSlot());
			items.add(item);
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItemFromBob(final int itemId, final int amount) {
		if (itemId < 0 || amount < 0 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItem(final String name, final int amount) {
		int itemId = Rscm.lookup(name);
		if (itemId < 0 || amount < 1 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItem(final int itemId, final int amount) {
		if (itemId < 0 || amount < 1 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean containsItemToolBelt(int id) {
		return containsOneItem(id);
	}

	public boolean containsItemToolBelt(int id, int amount) {
		return containsItem(id, amount);
	}

	public boolean addItem(Item item) {
		if (item.getId() < 0 || item.getAmount() < 0 || !Utils.itemExists(item.getId())
				|| !player.getControlerManager().canAddInventoryItem(item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			items.add(new Item(item.getId(), items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public void deleteItem(int slot, Item item) {
		if (!player.getControlerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(slot, item);
		refreshItems(itemsBefore);
	}

	public void dropItem(int slot, Item item, boolean addToGround) {
		if (!player.getControlerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(slot, item);
		if (addToGround)
			World.updateGroundItem(item, new WorldTile(player), player, player.isAtWild() && ItemConstants.isTradeable(item) ? 0 : 60, 0);
		refreshItems(itemsBefore);
		player.getPackets().sendSound(4500, 0, 1);
	}

	public boolean removeItems(Item... list) {
		for (Item item : list) {
			if (item == null)
				continue;
			deleteItem(item);
		}
		player.getPackets().sendSound(4500, 0, 1);
		return true;
	}

	public boolean dropItems(boolean addToGround, Item... list) {
		for (Item item : list) {
			if (item == null)
				continue;
			if (addToGround)
				World.updateGroundItem(item, new WorldTile(player), player, player.isAtWild() ? 0 : 60, 0);
			deleteItem(item);
		}
		player.getPackets().sendSound(4500, 0, 1);
		return true;
	}

	public boolean removeItems(List<Item> list) {
		for (Item item : list) {
			if (item == null)
				continue;
			deleteItem(item);
		}
		return true;
	}

	public void deleteItem(String name, int amount) {
		int itemId = Rscm.lookup(name);
		if (!player.getControlerManager().canDeleteInventoryItem(itemId, amount))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
		if (itemId == 995)
			player.getPackets().sendGameMessage((amount == 1 ? "One coin" : Utils.getFormattedNumber(amount, ',') + " coins") + " have been removed from your inventory.");
	}

	public void deleteItem(int itemId, int amount) {
		if (!player.getControlerManager().canDeleteInventoryItem(itemId, amount))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
		if (itemId == 995)
			player.getPackets().sendGameMessage((amount == 1 ? "One coin" : Utils.getFormattedNumber(amount, ',') + " coins") + " have been removed from your inventory.");
	}

	public void deleteItems(Item[] item) {
		Item[] itemsBefore = items.getItemsCopy();
		for (int index = 0; index < item.length; index++) {
			items.remove(item[index]);
		}
		refreshItems(itemsBefore);

	}

	public void deleteItem(Item item) {
		if (!player.getControlerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(item);
		refreshItems(itemsBefore);
	}

	/*
	 * No refresh needed its client to who does it :p
	 */
	public void switchItem(int fromSlot, int toSlot) {
		Item[] itemsBefore = items.getItemsCopy();
		Item fromItem = items.get(fromSlot);
		Item toItem = items.get(toSlot);
		items.set(fromSlot, toItem);
		items.set(toSlot, fromItem);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getContainerItems()[index])
				changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		Weights.updateWeight(player);
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public boolean hasFreeSlots() {
		return items.getFreeSlot() != -1;
	}

	public int getFreeSlots() {
		return items.getFreeSlots();
	}

	public int getNumberOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public int getNumberOf(String name) {
		return items.getNumberOf(Rscm.lookup(name));
	}

	public int getAmountOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public int getItemsContainerSize() {
		return items.getSize();
	}

	public boolean containsItems(List<Item> list) {
		for (Item item : list)
			if (!items.contains(item))
				return false;
		return true;
	}

	public boolean containsItems(Item[] item) {
		for (int i = 0; i < item.length; i++)
			if (!items.contains(item[i]))
				return false;
		return true;
	}

	public boolean containsItems(int[] itemIds, int[] ammounts) {
		int size = itemIds.length > ammounts.length ? ammounts.length : itemIds.length;
		for (int i = 0; i < size; i++)
			if (!items.contains(new Item(itemIds[i], ammounts[i])))
				return false;
		return true;
	}

	public boolean containsItem(int itemId, int ammount) {
		return items.contains(new Item(itemId, ammount));
	}

	public int getCoinsAmount() {
		int coins = items.getNumberOf(995);
		return coins < 0 ? Integer.MAX_VALUE : coins;
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1)))
				return true;
		}
		return false;
	}

	public void sendExamine(int slotId) {
		if (slotId >= getItemsContainerSize())
			return;
		Item item = items.get(slotId);
		if (item == null)
			return;
		long price = EconomyPrices.getPrice(item.getId());
		long amount = item.getAmount();
		long totalPrice = EconomyPrices.getPrice(item.getId()) * amount;
		boolean isNoted = item.getDefinitions().isNoted();
		boolean isStackable = item.getDefinitions().isStackable();
		boolean isTradeable = ItemConstants.isTradeable(item);
		boolean isFree = price == 1;
		if (isNoted)
			item = new Item(item.getDefinitions().getCertId(), item.getAmount());
		if (item.getDefinitions().isMembersOnly() && Settings.FREE_TO_PLAY) {
			player.getPackets().sendGameMessage("This is a members object.");
			return;
		}
		StringBuilder builder;
		if (!isTradeable) {
			builder = new StringBuilder();
			builder.append(item.getDefinitions().getName()).append(" is untradeable.");
			player.message(builder.toString());
		}
		/*
		Grand exchange price
		 */
		if (isTradeable && !isFree) {
			builder = new StringBuilder();
			builder.append("Ge Price: ");
			if ((isNoted || isStackable)) {
				if (item.getAmount() > 1)
					builder.append(Utils.getFormattedNumber(item.getAmount(), ',')).append(" x ");
				builder.append(item.getDefinitions().getName());
				builder.append(": ").append(HexColours.getShortMessage(HexColours.Colour.RED, Utils.formatMillionAmount(totalPrice))).append(" coins.");
				if (item.getAmount() > 1)
					builder.append(" (").append(HexColours.getShortMessage(HexColours.Colour.RED, Utils.getFormattedNumber(EconomyPrices.getPrice(item.getId()), ','))).append(" coins each)");
				builder.append("\n");
			} else {
				builder.append(item.getDefinitions().getName());
				builder.append(": ").append(HexColours.getShortMessage(HexColours.Colour.RED, Utils.formatMillionAmount(totalPrice))).append(" coins.\n");
			}
			player.message(builder.toString());
		}
		/*
		new ChatLine
		 */
		if (isTradeable && !isFree) {
			int bestBuyOffer = GrandExchange.getBestBuyPrice(item.getId());
			int bestSellOffer = GrandExchange.getCheapestSellPrice(item.getId());
			builder = new StringBuilder();
			builder.append("Grand Exchange: ");
			if (bestBuyOffer == 0 && bestSellOffer == 0) {
				builder.append("There is no grand exchange offers for this item.");
			} else {
				builder.append("Buy Offer: ").append(bestBuyOffer == 0 ? (HexColours.getShortMessage(HexColours.Colour.RED, "None") + ", ") : HexColours.getShortMessage(HexColours.Colour.GREEN, Utils.getFormattedNumber(bestBuyOffer, ',')) + " coins. ");
				player.message(builder.toString());
				builder = new StringBuilder();
				builder.append("                                Sell Offer: ").append(bestSellOffer == 0 ? (HexColours.getShortMessage(HexColours.Colour.RED, "None") + ", ") : HexColours.getShortMessage(HexColours.Colour.GREEN, Utils.getFormattedNumber(bestSellOffer, ',')) + " coins.");
			}
			player.message(builder.toString());
		}
				/*
		Alchemy price
		 */
		if (item.getDefinitions().getHighAlchPrice() > 0) {
			builder = new StringBuilder();
			builder.append("High Alch: ").append(HexColours.getShortMessage(HexColours.Colour.RED, Utils.getFormattedNumber(item.getDefinitions().getHighAlchPrice(), ','))).append(" coins, ");
			builder.append("Low Alch: ").append(HexColours.getShortMessage(HexColours.Colour.RED, Utils.getFormattedNumber(item.getDefinitions().getLowAlchPrice(), ','))).append(" coins.");
			player.message(builder.toString());
		}
		builder = new StringBuilder();
		builder.append("Description: ").append(ItemExamines.getExamine(item));
		player.message(builder.toString());
		if (player.isDeveloperMode()) {
			builder = new StringBuilder();
			builder.append("FileId: ").append(item.getDefinitions().getFileId());
			builder.append(", ArchiveId: ").append(item.getDefinitions().getArchiveId());
			builder.append(", ItemId: " ).append(item.getId());
			player.message(builder.toString());
		}
		if (item.getMetadata() != null) {
			StringBuilder metaBuilder = new StringBuilder("Metadata: ");
			metaBuilder
					.append(MetaDataType.fromId(item.getMetadata().getType()))
					.append(" (").append(item.getMetadata().getType()).append("), ")
					.append(item.getMetadata().getValue());
			player.message(metaBuilder.toString());
		}
	}

	public void refresh() {
		Item[] finalised = new Item[32];
		for (int i = 0; i < 28; i++) {
			finalised[i] = items.get(i);
		}
		if (player.getInventory().containsOneItem(RUNE_POUCH)) {
			ItemsContainer<Item> pouchRunes = player.getRunePouch();
			for (int i = 0; i < 3; i++) {
				Item rune = pouchRunes.get(i);
				finalised[29 + i] = (rune != null) ? rune.clone() : null;
			}
		} else {
			Arrays.fill(finalised, 29, 32, null);
		}
		player.getPackets().sendItems(93, finalised);
		Weights.updateWeight(player);
	}

	public void replaceItem(int id, int amount, int slot) {
		Item item = items.get(slot);
		if (item == null)
			return;
		item.setId(id);
		item.setAmount(amount);
		refresh(slot);
	}
	
	public boolean containsItem(Item... items) {
		for (Item item : items) {
			if (item != null && !containsItem(item.getId(), item.getAmount())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean contains(Item... items) {
		for (Item item : items) {
			if (item != null && !containsItem(item.getId(), item.getAmount())) {
				return false;
			}
		}
		return true;
	}
}
