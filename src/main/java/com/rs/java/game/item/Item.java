package com.rs.java.game.item;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.core.cache.defintions.ItemsEquipIds;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.kotlin.Rscm;

/**
 * Represents a single item.
 */
public class Item implements Serializable {

	@Serial
	private static final long serialVersionUID = -6485003878697568087L;

	private short id;
	private int amount;
	private ItemMetadata metadata;

	// -------------------- Static helpers (unchanged) --------------------

	private static String normalizeItemKey(String name) {
		return name.startsWith("item.") ? name : "item." + name;
	}

	private static int resolveId(Object obj) {
		if (obj instanceof Integer) {
			return (Integer) obj;
		} else if (obj instanceof String name) {
			return Rscm.lookup(normalizeItemKey(name));
		} else {
			throw new IllegalArgumentException("Item must be Integer or String, got: " + obj.getClass());
		}
	}

	public static List<Integer> getIds(Object... ids) {
		return Arrays.stream(ids)
				.map(Item::resolveId)
				.toList();
	}

	public static int[] getIdsArray(String... names) {
		return Arrays.stream(names)
				.map(Item::normalizeItemKey)
				.mapToInt(Rscm::lookup)
				.toArray();
	}

	public static int getId(String name) {
		return Rscm.lookup(normalizeItemKey(name));
	}

	private static boolean checkItemById(int id, String name) {
		return id == getId(name);
	}

	public static boolean isItem(int id, String name) {
		return checkItemById(id, name);
	}

	public static boolean isItem(int id, String... names) {
		return Arrays.stream(names).anyMatch(name -> checkItemById(id, name));
	}

	public boolean isItem(String name) {
		return checkItemById(this.id, name);
	}

	public boolean isAnyOf(String... itemNames) {
		return isItem(this.id, itemNames);
	}

	public String getNameKey() {
		return Rscm.reverseLookup(id);
	}

	// -------------------- Constructors (mostly unchanged) --------------------

	public Item(int id) {
		this(id, 1, null);
	}

	public Item(String name) {
		this(getId(name), 1, null);
	}

	public Item(String name, int amount) {
		this(getId(name), amount, null);
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

	/**
	 * Copy ctor (deep copies metadata).
	 */
	public Item(Item item) {
		this(item.id, item.amount, item.metadata != null ? item.metadata.deepCopy() : null);
	}

	// -------------------- Getters / setters --------------------

	public int getId() {
		return id;
	}

	public void setId(String item) {
		this.setId(Rscm.lookup(item));
	}

	/**
	 * IMPORTANT: changing the item id invalidates metadata.
	 * This prevents "wrong metadata attached to new id" corruption.
	 */
	public void setId(int id) {
		this.id = (short) id;
		this.metadata = null;
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

	public boolean hasMetadata() {
		return metadata != null;
	}

	/**
	 * Convenience: safe deep copy of the Item, including metadata.
	 */
	public Item copy() {
		return new Item(this);
	}

	// -------------------- Definitions / display --------------------

	public ItemDefinitions getDefinitions() {
		return ItemDefinitions.getItemDefinitions(id);
	}

	public int getEquipId() {
		return ItemsEquipIds.getEquipId(id);
	}

	public int getEquipSlot() {
		return getDefinitions().equipSlot;
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

	// -------------------- Metadata-aware identity / stacking --------------------

	/**
	 * True if these are the same item identity (ID + metadata compatibility).
	 * Use this when deciding whether to stack/merge or treat as different items.
	 */
	public boolean sameItemIdentity(Item other) {
		if (other == null) return false;
		if (this.id != other.id) return false;

		if (this.metadata == null && other.metadata == null) return true;
		if (this.metadata == null || other.metadata == null) return false;

		// metadata types must match and be stack-compatible
		return this.metadata.isStackableWith(other.metadata);
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

	// -------------------- Object methods --------------------

	@Override
	public Item clone() {
		// Safer: always use copy ctor so metadata is deep copied
		return new Item(this);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Item other)) return false;
		return this.id == other.id
				&& this.amount == other.amount
				&& Objects.equals(this.metadata, other.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, amount, metadata);
	}
}
