package com.rs.java.game.player.content;

import java.io.Serial;
import java.io.Serializable;

import com.rs.java.game.World;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

/**
 * MoneyPouch handles coin storage for the player.
 * Safely manages overflow and coin transfers between pouch, inventory, bank, and ground.
 *
 * @Improved Raynna
 */
public class MoneyPouch implements Serializable {

	@Serial
	private static final long serialVersionUID = -3847090682601697992L;

	private final transient Player player;

	public MoneyPouch(Player player) {
		this.player = player;
	}

	/**
	 * Gets the current amount in money pouch.
	 */
	public int getTotal() {
		return player.getMoneyPouchValue();
	}

	/**
	 * Sets the total money in the pouch.
	 */
	public void setTotal(int amount) {
		player.setMoneyPouchValue(amount);
	}

	/**
	 * Refreshes the pouch display.
	 */
	public void refresh() {
		player.getPackets().sendRunScript(5560, getTotal());
	}

	/**
	 * Checks if pouch is full.
	 */
	public boolean isFull() {
		return getTotal() == Integer.MAX_VALUE;
	}

	/**
	 * Adds money from player's inventory to pouch.
	 * If delete is true, coins are removed from inventory.
	 */
	public void addMoneyFromInventory(int amount, boolean delete) {
		if (amount <= 0) {
			return;
		}

		int total = getTotal();

		// Check for overflow
		if (total + amount < 0) {
			// Cap pouch at max
			int spaceLeft = Integer.MAX_VALUE - total;
			if (spaceLeft > 0) {
				setTotal(Integer.MAX_VALUE);
				sendAddMessage(spaceLeft);
				refresh();
			} else {
				player.getPackets().sendGameMessage("Your money pouch is already full.");
			}

			// Add leftover coins to inventory or drop on ground
			addLeftoverCoins(spaceLeft, delete);
			return;
		}

		// Safe add
		setTotal(total + amount);
		sendAddMessage(amount);
		refresh();

		if (delete) {
			player.getInventory().deleteItem(new Item(995, amount));
		}
	}

	/**
	 * Adds money from bank to pouch or inventory with proper overflow handling.
	 * bankSlot is used to remove coins from bank.
	 */
	public void addMoneyFromBank(int amount, int bankSlot) {
		if (amount <= 0) {
			return;
		}

		int total = getTotal();

		// Check if adding amount to pouch overflows
		if (total + amount < 0) {
			int spaceLeft = Integer.MAX_VALUE - total;

			if (spaceLeft > 0) {
				// Fill pouch to max
				setTotal(Integer.MAX_VALUE);
				player.getPackets().sendRunScript(5561, 1, spaceLeft);
				refresh();
				// Remove coins added to pouch from bank
				player.getBank().removeItem2(bankSlot, spaceLeft, true, false);
			} else {
				player.getPackets().sendGameMessage("Your money pouch is already full.");
				// No coins removed from bank here because nothing moved to pouch
				spaceLeft = 0;
			}

			int leftover = amount - spaceLeft;

			if (leftover <= 0) {
				return; // No leftover coins to handle
			}

			int inventoryCoins = player.getInventory().getNumberOf(995);

			// Check if inventory can hold leftover coins safely without overflow
			if (inventoryCoins + leftover < 0) {
				// Inventory overflow scenario
				int inventorySpaceLeft = Integer.MAX_VALUE - inventoryCoins;

				if (inventorySpaceLeft > 0) {
					// Add as many coins as inventory can hold
					player.getInventory().addItem(995, inventorySpaceLeft);
					player.getBank().removeItem2(bankSlot, inventorySpaceLeft, true, false);
					leftover -= inventorySpaceLeft;
				}

				// After partial add, if leftover coins remain, **do NOT drop on floor**,
				// leave them in bank (do nothing)
				if (leftover > 0) {
					player.getPackets().sendGameMessage("Your inventory is full. Some coins remain in your bank.");
				}
			} else {
				// Inventory can hold all leftover coins safely
				player.getInventory().addItem(995, leftover);
				player.getBank().removeItem2(bankSlot, leftover, true, false);
			}

			return;
		}

		// Safe add without overflow
		player.getBank().removeItem2(bankSlot, amount, true, false);
		setTotal(total + amount);
		player.getPackets().sendRunScript(5561, 1, amount);
		sendAddMessage(amount);
		refresh();
	}


	/**
	 * Adds money directly to pouch.
	 * If delete is true, removes coins from inventory.
	 */
	public void addMoney(int amount, boolean delete) {
		if (amount <= 0) {
			return;
		}

		int total = getTotal();

		if (total + amount < 0) { // overflow
			int spaceLeft = Integer.MAX_VALUE - total;

			if (spaceLeft > 0) {
				setTotal(Integer.MAX_VALUE);
				player.getPackets().sendRunScript(5561, 1, spaceLeft);
				sendAddMessage(spaceLeft);
				refresh();
			} else {
				player.getPackets().sendGameMessage("Your money pouch is already full.");
			}

			int leftover = amount - spaceLeft;

			addLeftoverCoins(leftover, delete);
			return;
		}

		// Safe add
		setTotal(total + amount);
		player.getPackets().sendRunScript(5561, 1, amount);
		sendAddMessage(amount);
		refresh();

		if (delete) {
			player.getInventory().deleteItem(new Item(995, amount));
		}
	}

	/**
	 * Adds leftover coins to inventory or drops them on ground if no space.
	 */
	private void addLeftoverCoins(int amount, boolean deleteFromInventory) {
		if (amount <= 0) {
			return;
		}

		if (!deleteFromInventory) {
			int inventoryCoins = player.getInventory().getNumberOf(995);

			if (inventoryCoins + amount < 0) {
				int spaceLeft = Integer.MAX_VALUE - inventoryCoins;

				if (spaceLeft > 0) {
					player.getInventory().addItem(new Item(995, spaceLeft));
				}
				amount -= spaceLeft;
				if (amount > 0) {
					World.updateGroundItem(new Item(995, amount), player, player);
				}
			} else {
				player.getInventory().addItem(new Item(995, amount));
			}
		}

		if (deleteFromInventory) {
			player.getInventory().deleteItem(new Item(995, amount));
		}
	}

	/**
	 * Adds money from miscellaneous sources (like drops) directly to pouch.
	 * Drops coins on ground if overflow.
	 */
	public void addMoneyMisc(int amount) {
		if (amount <= 0) {
			return;
		}

		int total = getTotal();

		if (total + amount < 0) { // overflow
			player.getPackets().sendGameMessage("Your money pouch can't hold that much cash.");
			World.addGroundItem(new Item(995, amount), player, player, true, 60);
			return;
		}

		setTotal(total + amount);
		sendAddMessage(amount);
		player.getPackets().sendRunScript(5561, 1, amount);
		refresh();
	}

	/**
	 * Removes money from pouch.
	 * Returns true if money was removed.
	 */
	public boolean removeMoneyMisc(int amount) {
		if (amount <= 0 || getTotal() == 0) {
			return false;
		}

		int total = getTotal();

		if (total < amount) {
			amount = total;
		}

		setTotal(total - amount);
		player.getPackets().sendRunScript(5561, 0, amount);
		player.getPackets().sendGameMessage(Utils.getFormattedNumber(amount, ',') + " coins have been removed from your money pouch.");
		refresh();
		return true;
	}

	/**
	 * Withdraws money from pouch to inventory.
	 */
	public void withdrawPouch(int amount) {
		if (amount <= 0) {
			return;
		}

		int total = getTotal();

		if (total == 0) {
			player.getPackets().sendGameMessage("You don't have any money stored in your money pouch.");
			return;
		}

		int freeSlots = player.getInventory().getFreeSlots();
		int inventoryCoins = player.getInventory().getNumberOf(995);

		if (freeSlots == 0 && inventoryCoins == 0 || inventoryCoins == Integer.MAX_VALUE) {
			player.getPackets().sendGameMessage("You don't have enough inventory space.");
			return;
		}

		if (inventoryCoins > Integer.MAX_VALUE - amount) {
			amount = Integer.MAX_VALUE - inventoryCoins;
		}

		if (amount > total) {
			amount = total;
		}

		setTotal(total - amount);
		player.getInventory().addItem(new Item(995, amount));
		player.getPackets().sendRunScript(5561, 0, amount);

		if (amount > 1) {
			player.getPackets().sendGameMessage(Utils.getFormattedNumber(amount, ',') + " coins have been withdrawn from your money pouch.");
		} else {
			player.getPackets().sendGameMessage("One coin has been withdrawn from your money pouch.");
		}

		refresh();
	}

	/**
	 * Attempts to take money from pouch.
	 * Returns true if any money was taken.
	 */
	public void takeMoneyFromPouch(int amount) {
		if (amount <= 0) {
			return;
		}

		int total = getTotal();

		if (total == 0) {
			return;
		}

		if (amount > total) {
			amount = total;
		}

		setTotal(total - amount);
		player.getPackets().sendRunScript(5561, 0, amount);
		player.getPackets().sendGameMessage((amount == 1 ? "One" : Utils.getFormattedNumber(amount, ',')) + " coin" + (amount == 1 ? "" : "s") + " have been withdrawn from your money pouch.");
		refresh();

	}

	/**
	 * Sends a message to player about coins added.
	 */
	private void sendAddMessage(int amount) {
		if (amount <= 0) {
			return;
		}
		if (amount == 1) {
			player.getPackets().sendGameMessage("One coin has been added to your money pouch.");
		} else {
			player.getPackets().sendGameMessage(Utils.getFormattedNumber(amount, ',') + " coins have been added to your money pouch.");
		}
	}

	/**
	 * Sends an examine message to player about pouch coins.
	 */
	public void sendExamine() {
		player.getPackets().sendGameMessage("Your money pouch currently contains " + Utils.getFormattedNumber(getTotal(), ',') + " coins.");
	}
}
