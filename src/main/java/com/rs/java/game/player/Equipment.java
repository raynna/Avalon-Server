package com.rs.java.game.player;

import java.io.Serializable;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemsContainer;
import com.rs.java.game.item.meta.MetaDataType;
import com.rs.java.game.player.actions.skills.firemaking.Bonfire;
import com.rs.java.utils.ItemExamines;
import com.rs.kotlin.Rscm;

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

	public Item[] createSnapshot() {
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
		player.getCombatDefinitions().updateBonuses();
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
		if (item.getMetadata() != null) {
			StringBuilder metaBuilder = new StringBuilder("Metadata: ");
			metaBuilder
					.append(MetaDataType.fromId(item.getMetadata().getType()))
					.append(" (").append(item.getMetadata().getType()).append("), ")
					.append(item.getMetadata().getValue());
			player.message(metaBuilder.toString());
		}
	}

	public void refreshConfigs(boolean init) {
		double hpIncrease = 0;
		boolean hasTransformItem = false;

		for (int slot = 0; slot < items.getSize(); slot++) {
			Item item = items.get(slot);
			if (item == null) continue;

			int id = item.getId();

			if (id == 4024) hasTransformItem = true;

			if (slot == Equipment.SLOT_HEAD) {
				if (item.isAnyOf(
						"item.torva_full_helm", "item.torva_full_helm_degraded",
						"item.virtus_mask", "item.virtus_mask_degraded",
						"item.pernix_cowl", "item.pernix_cowl_degraded")) {
					hpIncrease += 66;
				}

			} else if (slot == Equipment.SLOT_CHEST) {
				if (item.isAnyOf(
						"item.torva_platebody", "item.torva_platebody_degraded",
						"item.virtus_robe_top", "item.virtus_robe_top_degraded",
						"item.pernix_body", "item.pernix_body_degraded")) {
					hpIncrease += 200;
				}

			} else if (slot == Equipment.SLOT_LEGS) {
				if (item.isAnyOf(
						"item.torva_platelegs", "item.torva_platelegs_degraded",
						"item.virtus_robe_legs", "item.virtus_robe_legs_degraded",
						"item.pernix_chaps", "item.pernix_chaps_degraded")) {
					hpIncrease += 134;
				}
			}
		}

		int baseMax = player.getSkills().getLevel(Skills.HITPOINTS) * 10;

		if (player.getLastBonfire() > 0) {
			hpIncrease += (baseMax * Bonfire.getBonfireBoostMultiplier(player)) - baseMax;
		}
		if (player.getHpBoostMultiplier() != 0) {
			hpIncrease += baseMax * player.getHpBoostMultiplier();
		}

		if (hasTransformItem) {
			if (!player.getAppearance().isNPC())
				player.getAppearance().transformIntoNPC(4024 - 2544);
		} else if (player.getAppearance().isNPC()) {
			player.getAppearance().transformIntoNPC(-1);
		}

		player.sendDefaultPlayersOptions();

		int newIncrease = (int) hpIncrease;
		if (newIncrease != equipmentHpIncrease) {
			int prevMax = baseMax + equipmentHpIncrease;
			equipmentHpIncrease = newIncrease;
			int newMax = baseMax + equipmentHpIncrease;

			if (!init) {
				if (player.getHitpoints() > newMax)
					player.setHitpoints(newMax);
				player.refreshHitPoints();
			}
		}
	}


	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1)))
				return true;
		}
		return false;
	}

	public boolean containsAll(int... itemIds) {
		for (int itemId : itemIds) {
			if (!items.containsOne(new Item(itemId, 1))) {
				return false;
			}
		}
		return true;
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
		if (item.isItem("item.ancestral_hat"))
			return false;
		if (item.isItem("item.neitiznot_faceguard"))
			return true;
		if (player.isOldItemsLook() && name.contains("void"))
			return true;
		if (name.contains("mime m"))
			return true;
		if (item.getId() == 21389 || item.getId() >= 2910 && item.getId() <= 2940)
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

	public void refreshSlotOnly(int slot) {
		player.getPackets().sendUpdateItems(94, items, new int[]{slot});
	}


	public void decreaseItem(int slot, int amount) {
		Item item = items.get(slot);
		if (item == null)
			return;

		if (item.getAmount() <= amount) {
			items.set(slot, null);
		} else {
			item.setAmount(item.getAmount() - amount);
		}

		refreshSlotOnly(slot);
	}



	public void updateItemWithMeta(int slot, Item itemWithMeta) {
		getItems().set(slot, itemWithMeta.clone()); // preserve metadata
		refresh(slot);
	}

	public void updateItem(int slot, int itemId) {
		Item oldItem = getItem(slot);
		if (oldItem == null) return;

		Item newItem = new Item(
				itemId,
				oldItem.getAmount(),
				oldItem.getMetadata() == null ? null : oldItem.getMetadata().deepCopy()
		);

		items.set(slot, newItem);
		refresh(slot);
		player.getAppearance().generateAppearenceData();
	}


	public void updateItem(int slot, String item) {
		int itemId = Rscm.INSTANCE.item(item);
		Item oldItem = getItem(slot);
		if (oldItem == null) {
			return;
		}
		oldItem.setId(itemId);
		refresh(slot);
		player.getAppearance().generateAppearenceData();
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
