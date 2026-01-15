package com.rs.java.game.item;

import java.io.Serializable;

/**
 * Container class.
 *
 * @author Graham / edited by Dragonkk(Alex)
 * (metadata-safe patched)
 */
public final class ItemsContainer<T extends Item> implements Serializable {

	private static final long serialVersionUID = 1099313426737026107L;

	private Item[] data;
	private boolean alwaysStackable = false;

	public ItemsContainer(int size, boolean alwaysStackable) {
		data = new Item[size];
		this.alwaysStackable = alwaysStackable;
	}

	public void shift() {
		Item[] oldData = data;
		data = new Item[oldData.length];
		int ptr = 0;
		for (int i = 0; i < data.length; i++) {
			if (oldData[i] != null) {
				data[ptr++] = oldData[i];
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T get(int slot) {
		if (slot < 0 || slot >= data.length) {
			return null;
		}
		return (T) data[slot];
	}

	public void set(int slot, T item) {
		if (slot < 0 || slot >= data.length) {
			return;
		}
		data[slot] = item;
	}

	public void set2(int slot, Item item) {
		if (slot < 0 || slot >= data.length) {
			return;
		}
		data[slot] = item;
	}

	/**
	 * Force add into first free slot.
	 * NOTE: keep behavior, but ensure stored item is not an externally-mutable reference.
	 */
	public boolean forceAdd(T item) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == null) {
				data[i] = item == null ? null : new Item(item); // deep copy metadata
				return true;
			}
		}
		return false;
	}

	public int getThisItemSlot(int itemId) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				if (data[i].getId() == itemId) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * ADD:
	 * Critical fix: stack ONLY if isStackableWith() (id + metadata compatibility),
	 * NOT just same id.
	 */
	public boolean add(T item) {
		if (item == null)
			return false;

		// Handle stackable items
		if (alwaysStackable || item.getDefinitions().isStackable() || item.getDefinitions().isNoted()) {
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null && data[i].isStackableWith(item)) {
					Item oldItem = data[i];

					long total = (long) oldItem.getAmount() + item.getAmount();
					// Overflow check
					if (total > Integer.MAX_VALUE || total < 0) {
						total = Integer.MAX_VALUE;
					}

					Item newItem = new Item(oldItem); // preserves metadata
					newItem.setAmount((int) total);
					data[i] = newItem;
					return true;
				}
			}
		}
		// Handle non-stackable items efficiently
		else if (item.getAmount() >= 1) {
			int freeSlots = freeSlots();
			if (freeSlots < item.getAmount()) {
				return false;
			}

			int remaining = item.getAmount();
			for (int i = 0; i < data.length && remaining > 0; i++) {
				if (data[i] == null) {
					Item singleItem = new Item(item); // keeps metadata
					singleItem.setAmount(1);
					data[i] = singleItem;
					remaining--;
				}
			}
			return true;
		}

		// Single item add
		int index = freeSlot();
		if (index == -1) {
			return false;
		}
		data[index] = new Item(item); // store a copy to preserve metadata + avoid aliasing
		return true;
	}

	public int freeSlots() {
		int j = 0;
		for (Item aData : data) {
			if (aData == null) {
				j++;
			}
		}
		return j;
	}

	/**
	 * REMOVE by id (legacy behavior preserved).
	 * This keeps compatibility with code that does new Item(id, amt) without metadata.
	 * If you want "strict metadata remove", use remove(preferredSlot, item) with slot item.
	 */
	public int remove(T item) {
		if (item == null) return 0;

		int removed = 0, toRemove = item.getAmount();
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				if (data[i].getId() == item.getId()) {
					int amt = data[i].getAmount();
					if (amt > toRemove) {
						removed += toRemove;
						amt -= toRemove;
						toRemove = 0;
						Item oldItem = data[i];
						Item newItem = new Item(oldItem);
						newItem.setAmount(amt);
						data[i] = newItem;
						return removed;
					} else {
						removed += amt;
						toRemove -= amt;
						data[i] = null;
					}
				}
			}
		}
		return removed;
	}

	public void removeAll(T item) {
		if (item == null) return;

		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				if (data[i].getId() == item.getId()) {
					data[i] = null;
				}
			}
		}
	}

	public boolean containsOne(T item) {
		if (item == null) return false;

		for (Item aData : data) {
			if (aData != null) {
				if (aData.getId() == item.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean contains(T item) {
		if (item == null) return false;

		int amtOf = 0;
		for (Item aData : data) {
			if (aData != null) {
				if (aData.getId() == item.getId()) {
					amtOf += aData.getAmount();
				}
			}
		}
		return amtOf >= item.getAmount();
	}

	public int freeSlot() {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public void clear() {
		for (int i = 0; i < data.length; i++) {
			data[i] = null;
		}
	}

	public int getSize() {
		return data.length;
	}

	public int getFreeSlots() {
		int s = 0;
		for (Item aData : data) {
			if (aData == null) {
				s++;
			}
		}
		return s;
	}

	public int getUsedSlots() {
		int s = 0;
		for (Item aData : data) {
			if (aData != null) {
				s++;
			}
		}
		return s;
	}

	public int getNumberOf(Item item) {
		if (item == null) return 0;

		int count = 0;
		for (Item aData : data) {
			if (aData != null) {
				if (aData.getId() == item.getId()) {
					count += aData.getAmount();
				}
			}
		}
		return count;
	}

	public int getNumberOf(int item) {
		int count = 0;
		for (Item aData : data) {
			if (aData != null) {
				if (aData.getId() == item) {
					count += aData.getAmount();
				}
			}
		}
		return count;
	}

	public Item[] getContainerItems() {
		return data;
	}

	/**
	 * IMPORTANT: Keep shallow copy behavior because your refresh code often relies on reference comparison.
	 * (Inventory.refreshItems compares references to detect changed slots.)
	 */
	public Item[] getItemsCopy() {
		Item[] newData = new Item[data.length];
		System.arraycopy(data, 0, newData, 0, newData.length);
		return newData;
	}

	public ItemsContainer<Item> asItemContainer() {
		ItemsContainer<Item> c = new ItemsContainer<Item>(data.length, this.alwaysStackable);
		System.arraycopy(data, 0, c.data, 0, data.length);
		return c;
	}

	public int getFreeSlot() {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public int getThisItemSlot(T item) {
		if (item == null) return getFreeSlot();

		// preserve legacy behavior: slot by id
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				if (data[i].getId() == item.getId()) {
					return i;
				}
			}
		}
		return getFreeSlot();
	}

	public Item lookup(int id) {
		for (Item aData : data) {
			if (aData == null) {
				continue;
			}
			if (aData.getId() == id) {
				return aData;
			}
		}
		return null;
	}

	public int lookupSlot(int id) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == null) {
				continue;
			}
			if (data[i].getId() == id) {
				return i;
			}
		}
		return -1;
	}

	public void reset() {
		data = new Item[data.length];
	}

	/**
	 * Slot-aware remove. This is already your "best" method for metadata items.
	 * We keep behavior but make sure metadata doesn’t get lost when shrinking stacks.
	 */
	public int remove(int preferredSlot, Item item) {
		if (item == null) return 0;

		int removed = 0;
		int toRemove = item.getAmount();

		Item slotItem = data[preferredSlot];
		if (slotItem != null) {
			if (slotItem == item || !item.getDefinitions().isStackable()) {
				// Exact object match or unstackable: match by reference
				int amt = slotItem.getAmount();
				if (amt > toRemove) {
					removed += toRemove;
					amt -= toRemove;
					Item remaining = new Item(slotItem);
					remaining.setAmount(amt);
					set2(preferredSlot, remaining);
					return removed;
				} else {
					removed += amt;
					toRemove -= amt;
					set(preferredSlot, null);
				}
			} else if (slotItem.isStackableWith(item)) {
				int amt = slotItem.getAmount();
				if (amt > toRemove) {
					removed += toRemove;
					amt -= toRemove;
					Item remaining = new Item(slotItem);
					remaining.setAmount(amt);
					set2(preferredSlot, remaining);
					return removed;
				} else {
					removed += amt;
					toRemove -= amt;
					set(preferredSlot, null);
				}
			}
		}

		// Go through rest of inventory
		for (int i = 0; i < data.length; i++) {
			if (data[i] == null || i == preferredSlot)
				continue;

			Item current = data[i];
			if (current == item || !item.getDefinitions().isStackable()) {
				// Match unstackables directly by reference
				int amt = current.getAmount();
				if (amt > toRemove) {
					removed += toRemove;
					amt -= toRemove;
					Item remaining = new Item(current);
					remaining.setAmount(amt);
					set2(i, remaining);
					return removed;
				} else {
					removed += amt;
					toRemove -= amt;
					set(i, null);
				}
			} else if (current.isStackableWith(item)) {
				int amt = current.getAmount();
				if (amt > toRemove) {
					removed += toRemove;
					amt -= toRemove;
					Item remaining = new Item(current);
					remaining.setAmount(amt);
					set2(i, remaining);
					return removed;
				} else {
					removed += amt;
					toRemove -= amt;
					set(i, null);
				}
			}
		}

		return removed;
	}

	public void addAll(ItemsContainer<T> container) {
		for (int i = 0; i < container.getSize(); i++) {
			T item = container.get(i);
			if (item != null) {
				this.add(item);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void addAll(Item[] container) {
		for (int i = 0; i < container.length; i++) {
			Item item = container[i];
			if (item != null) {
				this.add((T) item);
			}
		}
	}

	public boolean hasSpaceFor(ItemsContainer<T> container) {
		for (int i = 0; i < container.getSize(); i++) {
			T item = container.get(i);
			if (item != null) {
				if (!this.hasSpaceForItem(item)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean hasSpaceForItem(T item) {
		if (alwaysStackable || item.getDefinitions().isStackable() || item.getDefinitions().isNoted()) {
			for (Item aData : data) {
				if (aData != null) {
					// IMPORTANT: space exists only if it can stack with existing item (metadata-safe)
					if (aData.isStackableWith(item)) {
						return true;
					}
				}
			}
		} else {
			if (item.getAmount() > 1) {
				return freeSlots() >= item.getAmount();
			}
		}
		int index = freeSlot();
		return index != -1;
	}

	public Item[] toArray() {
		return data;
	}
}
