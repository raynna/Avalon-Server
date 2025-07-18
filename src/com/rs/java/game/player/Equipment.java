package com.rs.java.game.player;

import java.io.Serializable;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemsContainer;
import com.rs.java.game.player.actions.skills.firemaking.Bonfire;
import com.rs.java.utils.ItemExamines;

public final class Equipment implements Serializable {

	private static final long serialVersionUID = -4147163237095647617L;

	public static final byte SLOT_HEAD = 0, SLOT_CAPE = 1, SLOT_AMULET = 2, SLOT_WEAPON = 3, SLOT_CHEST = 4,
			SLOT_SHIELD = 5, SLOT_LEGS = 7, SLOT_HANDS = 9, SLOT_FEET = 10, SLOT_RING = 12, SLOT_ARROWS = 13,
			SLOT_AURA = 14;

	private ItemsContainer<Item> items;

	private transient Player player;
	private transient int equipmentHpIncrease;

	static final int[] DISABLED_SLOTS = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0};

	public Equipment() {
		items = new ItemsContainer<Item>(15, false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		player.getPackets().sendItems(94, items);
		refresh(null);
	}

	public ItemsContainer<Item> getItemsContainer() {
		return items;
	}
	
	public void refresh(int... slots) {
		if (slots != null) {
			player.getPackets().sendUpdateItems(94, items, slots);
			player.getCombatDefinitions().checkAttackStyle();
		}
		player.getCombatDefinitions().refreshBonuses();
		refreshConfigs(slots == null);
	}

	public void refresh() {
		for (byte i = SLOT_HEAD; i < SLOT_AURA; i++) {
			refresh(i);
		}
	}

	public void refreshAmmo(int... slots) {
		refresh(SLOT_ARROWS);
	}

	public long getEquipmentValue() {
		long value = 0;
		for (Item equipment : player.getEquipment().getItems().toArray()) {
			if (equipment == null)
				continue;
			long amount = equipment.getAmount();
			value += equipment.getDefinitions().getTipitPrice() * amount;
		}
		return value;
	}

	public void reset() {
		items.reset();
		init();
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public void sendExamine(int slotId) {
		Item item = items.get(slotId);
		if (item == null)
			return;
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void refreshConfigs(boolean init) {
		double hpIncrease = 0;
		for (int index = 0; index < items.getSize(); index++) {
			Item item = items.get(index);
			if (item == null)
				continue;
			int id = item.getId();
			if (id == 4024) {
				player.getAppearence().transformIntoNPC(4024 - 2544);
				return;
			}
			if (player.getAppearence().isNPC()) {
				player.getAppearence().transformIntoNPC(-1);
			}
			if (index == Equipment.SLOT_HEAD) {
				if (id == 20135 || id == 20137 // torva
						|| id == 20147 || id == 20149 // pernix
						|| id == 20159 || id == 20161 // virtus
				)
					hpIncrease += 66;

			} else if (index == Equipment.SLOT_CHEST) {
				if (id == 20139 || id == 20141 // torva
						|| id == 20151 || id == 20153 // pernix
						|| id == 20163 || id == 20165 // virtus
				)
					hpIncrease += 200;
			} else if (index == Equipment.SLOT_LEGS) {
				if (id == 20143 || id == 20145 // torva
						|| id == 20155 || id == 20157 // pernix
						|| id == 20167 || id == 20169 // virtus
				)
					hpIncrease += 134;
			}
		}
		if (player.getLastBonfire() > 0) {
			int maxhp = player.getSkills().getLevel(Skills.HITPOINTS) * 10;
			hpIncrease += (maxhp * Bonfire.getBonfireBoostMultiplier(player)) - maxhp;
		}
		if (player.getHpBoostMultiplier() != 0) {
			int maxhp = player.getSkills().getLevel(Skills.HITPOINTS) * 10;
			hpIncrease += maxhp * player.getHpBoostMultiplier();
		}
		if (player.getEquipment().getWeaponId() == 10501) {
			player.sendDefaultPlayersOptions();
		} else {
			player.sendDefaultPlayersOptions();
		}
		if (hpIncrease != equipmentHpIncrease) {
			equipmentHpIncrease = (int) hpIncrease;
			if (!init)
				player.refreshHitPoints();
		}
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1)))
				return true;
		}
		return false;
	}

	public static boolean hideArms(Item item) {
		String name = item.getName().toLowerCase();
		if (name.contains("d'hide body") || name.contains("dragonhide body") || name.equals("stripy pirate shirt")
				|| (name.contains("chainbody") && (name.contains("iron") || name.contains("bronze")
				|| name.contains("steel") || name.contains("black") || name.contains("mithril")
				|| name.contains("adamant") || name.contains("rune") || name.contains("white")))
				|| name.equals("leather body") || name.equals("hardleather body") || name.contains("studded body"))
			return false;
		return item.getDefinitions().getEquipType() == 6;
	}

	public boolean hideHair(Item item) {
		String name = item.getName().toLowerCase();
		if (player.isOldItemsLook() && name.contains("void"))
			return true;
		if (name.contains("mime m"))
			return true;
		return item.getDefinitions().getEquipType() == 8;
	}

	public boolean showBear(Item item) {
		String name = item.getName().toLowerCase();
		return !hideHair(item) || name.contains("horns") || name.contains("hat") || name.contains("coif")
				|| name.contains("afro") || name.contains("cowl") || name.contains("mitre")
				|| name.contains("bear mask") || name.contains("tattoo") || name.contains("antlers")
				|| name.contains("chicken head") || name.contains("headdress") || name.contains("hood")
				|| name.contains("bearhead")
				|| (name.contains("mask") && !name.contains("h'ween") && !name.contains("mime m"))
				|| (name.contains("helm") && !name.contains("full") && !name.contains("flaming"));
	}

	public static int getItemSlot(int slot) {
		return ItemDefinitions.getItemDefinitions(slot).getEquipSlot();
	}

	public static boolean isTwoHandedWeapon(Item item) {
		return item.getDefinitions().getEquipType() == 5;
	}

	public int getWeaponRenderEmote() {
		Item weapon = items.get(3);
		if (weapon == null)
			return 1426;
		return weapon.getDefinitions().getRenderAnimId();
	}

	public boolean hasShield() {
		return items.get(5) != null;
	}

	public boolean hasWeapon() {
		return items.get(SLOT_WEAPON) != null;
	}

	@SuppressWarnings("unlikely-arg-type")
	public boolean getSlot(int slot, int itemId) {
		return items.get(slot).equals(itemId);
	}

	public int getWeaponId() {
		Item item = items.get(SLOT_WEAPON);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getChestId() {
		Item item = items.get(SLOT_CHEST);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getHatId() {
		Item item = items.get(SLOT_HEAD);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getShieldId() {
		Item item = items.get(SLOT_SHIELD);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getLegsId() {
		Item item = items.get(SLOT_LEGS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public void removeAmmo(int ammoId, int ammount) {
		if (ammount == -1) {
			items.remove(SLOT_WEAPON, new Item(ammoId, 1));
			refresh(SLOT_WEAPON);
		} else {
			items.remove(SLOT_ARROWS, new Item(ammoId, ammount));
			refresh(SLOT_ARROWS);
		}
	}

	public int getAuraId() {
		Item item = items.get(SLOT_AURA);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getCapeId() {
		Item item = items.get(SLOT_CAPE);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getRingId() {
		Item item = items.get(SLOT_RING);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getAmmoId() {
		Item item = items.get(SLOT_ARROWS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public void deleteItem(int itemId, int amount) {
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void updateItemWithMeta(int slot, Item itemWithMeta) {
		getItems().set(slot, itemWithMeta.clone()); // preserve metadata
		refresh(slot);
	}

	public void updateItem(int slot, int itemId) {
		Item oldItem = getItem(slot);
		if (oldItem == null) {
			return;
		}
		oldItem.setId(itemId);
		refresh(slot);
		player.getAppearence().generateAppearenceData();
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

	public int getBootsId() {
		Item item = items.get(SLOT_FEET);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getGlovesId() {
		Item item = items.get(SLOT_HANDS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public int getEquipmentHpIncrease() {
		return equipmentHpIncrease;
	}

	public void setEquipmentHpIncrease(int hp) {
		this.equipmentHpIncrease = hp;
	}

	public boolean wearingArmour() {
		return getItem(SLOT_HEAD) != null || getItem(SLOT_CAPE) != null || getItem(SLOT_AMULET) != null
				|| getItem(SLOT_WEAPON) != null || getItem(SLOT_CHEST) != null || getItem(SLOT_SHIELD) != null
				|| getItem(SLOT_LEGS) != null || getItem(SLOT_HANDS) != null || getItem(SLOT_FEET) != null;
	}

	public boolean isWearing(final byte SLOT_ID) {
		return getItem(SLOT_ID) != null;
	}

	public int getAmuletId() {
		Item item = items.get(SLOT_AMULET);
		if (item == null)
			return -1;
		return item.getId();
	}

	public boolean hasTwoHandedWeapon() {
		Item weapon = items.get(SLOT_WEAPON);
		return weapon != null && isTwoHandedWeapon(weapon);
	}

}
