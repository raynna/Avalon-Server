package com.rs.java.game.item;

import java.io.Serializable;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.core.cache.defintions.ItemsEquipIds;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.item.meta.CombatBonusType;

/**
 * Represents a single item.
 */
public class Item implements Serializable {

	private static final long serialVersionUID = -6485003878697568087L;

	private short id;
	private int amount;
	private ItemMetadata metadata;

	public Item(int id) {
		this(id, 1, null);
	}

	public Item(String name) {
		this(ItemDefinitions.getId(name), 1, null);
	}

	public Item(String name, int amount) {
		this(ItemDefinitions.getId(name), amount, null);
	}

	public Item(int id, int amount) {
		this(id, amount, null);
	}

	public Item(int id, int amount, ItemMetadata metadata) {
		this(id, amount, false, metadata);
	}

	public Item(int id, int amount, boolean amt0, ItemMetadata metadata) {
		this.id = (short) id;
		this.amount = (!amt0 && amount <= 0) ? 1 : amount;
		this.metadata = metadata;
	}

	public Item(Item item) {
		this(item.id, item.amount, item.metadata != null ? item.metadata.deepCopy() : null);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = (short) id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public ItemMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ItemMetadata metadata) {
		this.metadata = metadata;
	}

	public ItemDefinitions getDefinitions() {
		return ItemDefinitions.getItemDefinitions(id);
	}

	public int getEquipId() {
		return ItemsEquipIds.getEquipId(id);
	}

	public String getName() {
		return getDefinitions().getName();
	}

	public String getDisplayName() {
		String name = getName();
		if (metadata != null) {
			name += " " + metadata.getDisplaySuffix();
		}
		return name;
	}

	public boolean isStackableWith(Item other) {
		if (this.id != other.id) return false;
		if (this.metadata == null && other.metadata == null) return true;
		if (this.metadata == null || other.metadata == null) return false;
		return this.metadata.isStackableWith(other.metadata);
	}

	public long getFixedUniqueId() {
		long metaHash = (metadata != null ? metadata.hashCode() : 0);
		return id * 234111L + amount * 23911L + metaHash;
	}

	@Override
	public Item clone() {
		return new Item(this);
	}
}
