package com.rs.java.game.player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs.Settings;
import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemsContainer;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.item.meta.MetaDataType;
import com.rs.java.game.item.meta.RunePouchMetaData;
import com.rs.java.game.player.content.ItemConstants;
import com.rs.java.game.player.content.grandexchange.GrandExchange;
import com.rs.java.utils.*;
import com.rs.kotlin.Rscm;

public final class Inventory implements Serializable {

	private static final long serialVersionUID = 8842800123753277093L;

	public ItemsContainer<Item> items;

	private transient Player player;

	public static final int INVENTORY_INTERFACE = 679;

	public static final int RUNE_POUCH = 24510;

	public Inventory() {
		items = new ItemsContainer<Item>(28, false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean canHold(Item item, int amount) {
		ItemDefinitions def = ItemDefinitions.getItemDefinitions(item.getId());
		boolean stackableOrNoted = def.isStackable() || def.isNoted();

		if (stackableOrNoted) {
			if (containsOneItem(item.getId())) {
				return true;
			}
			return getFreeSlots() > 0;
		} else {
			return getFreeSlots() >= amount;
		}
	}


	public Item[] createSnapshot() {
		// Returns a deep copy of the inventory items
		Item[] copy = new Item[items.getSize()];
		for (int i = 0; i < items.getSize(); i++) {
			Item item = items.get(i);
			if (item != null) {
				copy[i] = new Item(item.getId(), item.getAmount(),
						item.getMetadata() == null ? null : item.getMetadata().deepCopy());
			}
		}
		return copy;
	}

	public void restoreSnapshot(Item[] snapshot) {
		if (snapshot == null) return;
		reset();
		for (int i = 0; i < snapshot.length; i++) {
			Item item = snapshot[i];
			if (item != null) {
				items.set(i, item);
			}
		}
		refresh();
	}


	public boolean canHold(int itemId, int amount) {
		return canHold(new Item(itemId), amount);
	}

	public void init() {
		int pouchCount = 0;
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item != null && item.getId() == RUNE_POUCH) {
				pouchCount++;
			}
		}

		Item[] finalised = new Item[28 + (pouchCount * 3)];

		for (int i = 0; i < 28; i++) {
			finalised[i] = items.get(i);
		}

		int overlayIndex = 28;
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item == null || item.getId() != RUNE_POUCH) continue;

			ItemMetadata meta = item.getMetadata();
			if (meta instanceof RunePouchMetaData) {
				Item[] runes = ((RunePouchMetaData) meta).getRunesToArray();
				for (int i = 0; i < 3; i++) {
					finalised[overlayIndex++] = runes[i];
				}
			}
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
		int pouchCount = 0;
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item != null && item.getId() == RUNE_POUCH) {
				pouchCount++;
			}
		}

		Item[] finalised = new Item[28 + (pouchCount * 3)];

		for (int i = 0; i < 28; i++) {
			finalised[i] = items.get(i);
		}

		int overlayIndex = 28;
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item == null || item.getId() != RUNE_POUCH) continue;

			ItemMetadata meta = item.getMetadata();
			if (meta instanceof RunePouchMetaData) {
				Item[] runes = ((RunePouchMetaData) meta).getRunesToArray();
				for (int i = 0; i < 3; i++) {
					finalised[overlayIndex++] = runes[i];
				}
			}
		}
		if (slots != null && slots.length > 0) {
			player.getPackets().sendUpdateItems(93, finalised, slots);
		} else {
			player.getPackets().sendItems(93, finalised);
		}
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
			World.updateGroundItem(item, new WorldTile(player), player, player.inPkingArea() && ItemConstants.isTradeable(item) ? 0 : 60, 0);
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
				World.updateGroundItem(item, new WorldTile(player), player, player.inPkingArea() ? 0 : 60, 0);
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
		if (fromSlot < 0 || toSlot < 0 || fromSlot >= items.getSize() || toSlot >= items.getSize()) {
			return;
		}
		Item fromItem = items.get(fromSlot);
		Item toItem = items.get(toSlot);
		items.set(fromSlot, toItem);
		items.set(toSlot, fromItem);
		refresh(fromSlot, toSlot);
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
	}

	public void refreshItems(Item[] itemsBefore, boolean updateWeight) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getContainerItems()[index])
				changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		if (updateWeight)
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

	public int getAmountOf(String itemName) {
		int itemId = Rscm.lookup(itemName);
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
			try {
				//dumpAllItemDefinitions();
				//dumpAllItemClientScriptData();
				dumpScripts(item.getId());
			} catch (Exception error) {
				System.out.println(error.toString());
			}
			builder.append("ClientScriptData size: " + item.getDefinitions().getClientScriptSize());
			builder.append(", FileId: ").append(item.getDefinitions().getFileId());
			builder.append(", ArchiveId: ").append(item.getDefinitions().getArchiveId());
			builder.append(", ItemId: " ).append(item.getId());
			builder.append(", EquipmentType: " ).append(item.getDefinitions().getEquipType());
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


	private void dumpScripts(int itemId) throws IOException {
		ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);
		Map<Integer, Object> dumpMap = new HashMap<>();
		if (def.getClientScriptData() != null) {
			for (int i = 0; i <= 5000; i++) {
				Object value = def.getClientScriptData().get(i);
				if (value != null) {
					dumpMap.put(i, value);
				}
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File dir = new File("./data/clientscripts/item");
		File file = new File(dir, def.name + "(" + itemId + ")_clientscriptdata.json");
		try (FileWriter writer = new FileWriter(file)) {
			gson.toJson(dumpMap, writer);
		}
	}

	public void refresh() {
		int pouchCount = 0;
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item != null && item.getId() == RUNE_POUCH) {
				pouchCount++;
			}
		}

		Item[] finalised = new Item[28 + (pouchCount * 3)];

		for (int i = 0; i < 28; i++) {
			finalised[i] = items.get(i);
		}

		int overlayIndex = 28;
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item == null || item.getId() != RUNE_POUCH) continue;

			ItemMetadata meta = item.getMetadata();
			if (meta instanceof RunePouchMetaData) {
				Item[] runes = ((RunePouchMetaData) meta).getRunesToArray();
				for (int i = 0; i < 3; i++) {
					finalised[overlayIndex++] = runes[i];
				}
			}
		}
		player.getPackets().sendItems(93, finalised);
	}

	private static void dumpAllItemDefinitions() throws IOException {
		Map<String, Map<String, Object>> allObjects = new LinkedHashMap<>();

		int itemCount = Utils.getItemDefinitionsSize();
		System.out.println("Starting dump of " + itemCount + " items...");

		int addedCount = 0;
		for (int itemId = 0; itemId < itemCount; itemId++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);
			if (def == null)
				continue;

			Map<String, Object> defData = dumpItemDefinitionFields(def);

			// Sanitize name for JSON key, fallback to ID if null or empty
			String name = def.name != null && !def.name.isEmpty() ? def.name : "Item";
			String safeName = name.replaceAll("[\\\\/:*?\"<>|]", "_") + "(" + itemId + ")";

			allObjects.put(safeName, defData);
			addedCount++;

			if (addedCount % 100 == 0 || itemId == itemCount - 1) {
				System.out.println("Dumped " + addedCount + " items so far (last id: " + itemId + ")");
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File dir = new File("./data/clientscripts/item");
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, "all_item_definitions.json");
		try (FileWriter writer = new FileWriter(file)) {
			gson.toJson(allObjects, writer);
		}

		System.out.println("Finished dumping all item definitions. Total entries: " + addedCount);
		System.out.println("Output file: " + file.getAbsolutePath());
	}

	private static Map<String, Object> dumpItemDefinitionFields(ItemDefinitions def) {
		Map<String, Object> map = new LinkedHashMap<>();

		// Use reflection to get all declared fields including private ones
		Field[] fields = def.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue; // skip static fields

			field.setAccessible(true);
			try {
				Object value = field.get(def);

				// Optional: If you want, you can filter or transform certain field types here,
				// e.g. convert arrays to lists for JSON serialization

				if (value != null && value.getClass().isArray()) {
					// Convert arrays to lists for better Gson output
					int length = java.lang.reflect.Array.getLength(value);
					List<Object> list = new ArrayList<>();
					for (int i = 0; i < length; i++) {
						list.add(java.lang.reflect.Array.get(value, i));
					}
					map.put(field.getName(), list);
				} else {
					map.put(field.getName(), value);
				}

			} catch (IllegalAccessException e) {
				// Failed to access field, skip or log
				map.put(field.getName(), "ACCESS_ERROR");
			}
		}

		return map;
	}

	private static void dumpAllItemClientScriptData() throws IOException {
		Map<String, Map<Integer, Object>> itemScripts = new HashMap<>();

		int itemCount = Utils.getItemDefinitionsSize();
		System.out.println("Starting dump of clientScriptData for " + itemCount + " items...");

		int addedCount = 0;
		for (int itemId = 0; itemId < itemCount; itemId++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);

			if (def == null) {
				System.out.println("Item id " + itemId + " is null, skipping...");
				continue;
			}

			if (def.getClientScriptData() == null || def.getClientScriptData().isEmpty()) {
				continue;  // no clientScriptData, skip
			}

			// Sanitize item name for JSON key
			String safeName = def.getName().replaceAll("[\\\\/:*?\"<>|]", "_");

			itemScripts.put(safeName + "(" + itemId + ")", new HashMap<>(def.getClientScriptData()));
			addedCount++;

			if (addedCount % 100 == 0 || itemId == itemCount - 1) {
				System.out.println("Added " + addedCount + " item clientScriptData entries so far (last item id: " + itemId + ")");
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File dir = new File("./data/clientscripts/items");
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, "all_item_clientscriptdata.json");
		try (FileWriter writer = new FileWriter(file)) {
			gson.toJson(itemScripts, writer);
		}

		System.out.println("Finished dumping all item clientScriptData. Total entries: " + addedCount);
		System.out.println("Output file: " + file.getAbsolutePath());
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

	public boolean hasSpaceFor(int itemId, int amount) {
		ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
		boolean isStackable = itemDef.isStackable() || itemDef.isNoted();

		if (isStackable) {
			return items.containsOne(new Item(itemId, 1)) || items.getFreeSlots() >= 1;
		} else {
			return items.getFreeSlots() >= amount;
		}
	}

	// Alternative more precise version that considers stack limits
	public boolean hasSpaceForPrecise(int itemId, int amount, boolean isNoted) {
		ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
		boolean isStackable = itemDef.isStackable() || isNoted;

		if (!isStackable) {
			return items.getFreeSlots() >= amount;
		}

		// Check existing stacks first
		int remainingAmount = amount;
		for (Item item : items.getContainerItems()) {
			if (item != null && item.getId() == itemId) {
				int spaceAvailable = Integer.MAX_VALUE - item.getAmount(); // Adjust if you have max stack limits
				if (spaceAvailable > 0) {
					remainingAmount -= spaceAvailable;
					if (remainingAmount <= 0) {
						return true;
					}
				}
			}
		}

		// If we still need space, calculate how many new slots we need
		return items.getFreeSlots() >= 1;
	}
}
