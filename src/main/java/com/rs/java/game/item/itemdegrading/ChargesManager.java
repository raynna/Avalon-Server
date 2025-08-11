package com.rs.java.game.item.itemdegrading;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.itemdegrading.ItemDegrade.DegradeType;
import com.rs.java.game.item.itemdegrading.ItemDegrade.DegradeData;
import com.rs.java.game.item.meta.DegradeHitsMetaData;
import com.rs.java.game.item.meta.DegradeTicksMetaData;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

/**
 * @author -Andreas 1 feb. 2020 13:58:09
 * @project 1. Avalon
 * 
 */

public class ChargesManager implements Serializable {

	@Serial
	private static final long serialVersionUID = -5978513415281726450L;

	private transient Player player;

	private final Map<Integer, Integer> charges;

	public ChargesManager() {
		charges = new HashMap<>();
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	private static final DegradeData[] data = DegradeData.values();

	private final double DEGRADE_MESSAGE_INTERVAL = 0.05;
	private final int CRUMBLE_DUST_HEAD = 1859, CRUMBLE_DUST_CHEST = 1861, CRUMBLE_DUST_LEGS = 1860;

	public void process() {
		Item[] items = player.getEquipment().getItems().getContainerItems();
		for (int slot = 0; slot < items.length; slot++) {
			Item item = items[slot];
			if (item == null)
				continue;
			DegradeData data = getDegradeData(item.getId());
			if (data == null || data.getCurrentItem().getId() != item.getId())
				continue;
			if (data.getType() == DegradeType.WEAR)
				degrade(item, slot);
			else if (data.getType() == DegradeType.IN_COMBAT
					&& player.getAttackedByDelay() > Utils.currentTimeMillis())
				degrade(item, slot);
		}
	}

	public void processOutgoingHit() {
		Item[] items = player.getEquipment().getItems().getContainerItems();
		for (int slot = 0; slot < items.length; slot++) {
			Item item = items[slot];
			if (item == null)
				continue;
			DegradeData data = getDegradeData(item.getId());
			if (data == null) continue;
			if (data.getCurrentItem().getId() != item.getId()) {
				continue;
			}
			if (data.getType() == DegradeType.AT_OUTGOING_HIT)
				degrade(item, slot);
			}
	}

	public void processIncommingHit() {
		Item[] items = player.getEquipment().getItems().getContainerItems();
		for (int slot = 0; slot < items.length; slot++) {
			Item item = items[slot];
			if (item == null)
				continue;
			DegradeData data = getDegradeData(item.getId());
			if (data == null) continue;
			if (data.getType() == DegradeType.AT_INCOMMING_HIT)
				degrade(item, slot);
		}
	}

	public void processHit(Hit hit) {
		Item[] items = player.getEquipment().getItems().getContainerItems();
		for (int slot = 0; slot < items.length; slot++) {
			Item item = items[slot];
			if (item == null)
				continue;
			DegradeData data = getDegradeData(item.getId());
			if (data == null) continue;
			if (data.getType() == DegradeType.HITS)
				degrade(item, slot, hit);
		}
	}

	public int getCharges(int id) {
		Integer c = charges.get(id);
		return c == null ? 0 : c;
	}

	public void resetCharges(int id) {
		charges.remove(id);
	}

	public void setCharges(int id, int amount) {
		charges.put(id, amount);
	}

	public int breakItem(Item item) {
		DegradeData data = getDegradeData(item.getId());
		if (data == null) return -1;
		int newId = -1;
		if (data.getCurrentItem().getId() == item.getId()) {
			newId = data.getBrokenItem() != null ? data.getBrokenItem().getId()
					: data.getDegradedItem() != null ? data.getDegradedItem().getId() : -1;
		}
		return newId != -1 ? newId : item.getId();
	}

	public void checkPercentage(String message, int itemId, boolean reverse) {
		int charges = getCharges(itemId);
		int maxCharges = 0;
		DegradeData data = getDegradeData(itemId);
		if (data == null) return;
		if (data.getCurrentItem().getId() == itemId) {
			maxCharges = (data.getType() == DegradeType.AT_INCOMMING_HIT
					|| data.getType() == DegradeType.AT_OUTGOING_HIT) ? data.getHits() : data.getTime().getTicks();
		}
		int percentage = reverse ? (charges == 0 ? 0 : (100 - (charges * 100 / maxCharges)))
				: charges == 0 ? 100 : (charges * 100 / maxCharges);
		player.message(message.replace("##", String.valueOf(percentage)));
	}

	public int getPercentage(Item item, boolean reverse) {
		int charges = getCharges(item.getId());
		int maxCharges = 0;
		DegradeData data = getDegradeData(item.getId());
		if (data == null) return -1;
		if (data.getCurrentItem().getId() == item.getId()) {
			maxCharges = (data.getType() == DegradeType.AT_INCOMMING_HIT
					|| data.getType() == DegradeType.AT_OUTGOING_HIT) ? data.getHits() : data.getTime().getTicks();
		}
		return reverse ? (charges == 0 ? 0 : (100 - (charges * 100 / maxCharges)))
				: charges == 0 ? 100 : (charges * 100 / maxCharges);
	}

	public void checkCharges(String message, int id) {
		player.getPackets().sendGameMessage(message.replace("##", String.valueOf(getCharges(id))));
	}

	private void degrade(Item item, int slot, Hit hit) {
		DegradeData data = getDegradeData(item.getId());
		if (data == null || data.getCurrentItem().getId() != item.getId())
			return;

		// Only initialize charges if they don't exist
		charges.computeIfAbsent(item.getId(), k -> {
			player.message("Your " + ItemDefinitions.getItemDefinitions(item.getId()).getName() + " has started degrading.");
			return data.getHits(); // Initialize with default charges (400)
		});

		// No charge removal or degradation logic here - handled in handleRingOfRecoil
	}

	private void degrade(Item item, int slot) {
		ItemDefinitions definitions = ItemDefinitions.getItemDefinitions(item.getId());
		ItemMetadata metaData = item.getMetadata();
		DegradeData degradeData = getDegradeData(item.getId());

		if (degradeData == null) return;

		if (metaData == null) {
			switch (degradeData.getType()) {
				case WEAR:
				case IN_COMBAT:
					item.setMetadata(new DegradeTicksMetaData(degradeData.getTime().getTicks()));
					break;
				case AT_INCOMMING_HIT:
				case AT_OUTGOING_HIT:
				case HITS://TODO MIGHT NOT NEED ANYMORE
					item.setMetadata(new DegradeHitsMetaData(degradeData.getHits()));
					break;
			}
			metaData = item.getMetadata();
		}
		int charges = (int) metaData.getValue();
		charges--;
		if (charges > 0) {
			metaData.setValue(charges);
			item.setMetadata(metaData);
			int total = getTotalCharges(metaData, degradeData);
			if (charges % Math.floor(total * DEGRADE_MESSAGE_INTERVAL) == 0) {
				checkPercentage("Your " + definitions.getName() + " has degraded ##%.", item.getId(), true);
			}
			return;
		}
		Item newItem = degradeData.getDegradedItem() != null ? degradeData.getDegradedItem() : degradeData.getBrokenItem();
		if (newItem == null) {
			if (definitions.getName().contains("(deg)")) {
				if (slot == Equipment.SLOT_HEAD) player.gfx(new Graphics(CRUMBLE_DUST_HEAD));
				if (slot == Equipment.SLOT_CHEST) player.gfx(new Graphics(CRUMBLE_DUST_CHEST));
				if (slot == Equipment.SLOT_LEGS) player.gfx(new Graphics(CRUMBLE_DUST_LEGS));
			}
			player.message("Your " + definitions.getName() + " turned into dust.");
			player.getEquipment().getItems().set(slot, null);
		} else {
			player.message("Your " + definitions.getName() + " degraded.");
			Item copy = newItem.clone();
			player.getEquipment().getItems().set(slot, copy);
		}
		player.getEquipment().refresh(slot);
		player.getAppearence().generateAppearenceData();
	}

	public DegradeData getDegradeData(int itemId) {
		for (DegradeData degradeData : data) {
			if (degradeData != null && degradeData.getCurrentItem().getId() == itemId)
				return degradeData;
		}
		return null;
	}

	private int getValueFromMetaData(ItemMetadata metadata) {
		if (metadata instanceof DegradeHitsMetaData)
			return ((DegradeHitsMetaData) metadata).getValue();
		if (metadata instanceof DegradeTicksMetaData)
			return ((DegradeTicksMetaData) metadata).getValue();
		return -1;
	}

	private void setValueFromMetaData(ItemMetadata metadata, int value) {
		if (metadata instanceof DegradeHitsMetaData)
			metadata.setValue(value);
		if (metadata instanceof DegradeTicksMetaData)
			metadata.setValue(value);
	}

	private int getTotalCharges(ItemMetadata metadata, DegradeData store) {
		if (metadata instanceof DegradeTicksMetaData && store.getTime() != null)
			return store.getTime().getTicks();
		if (metadata instanceof DegradeHitsMetaData && store.getHits() > 0)
			return store.getHits();
		return 100;
	}
}
