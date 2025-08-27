package com.rs.java.game.player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemsContainer;
import com.rs.java.game.player.content.ItemConstants;
import com.rs.java.utils.EconomyPrices;
import com.rs.java.utils.ItemExamines;

public class Trade {

	private static final int COINS_ID = 995;

	private final transient Player player;
    private transient Player target;
	private final ItemsContainer<Item> items;
	private boolean tradeModified;
	private boolean accepted;

	public Trade(Player player) {
		this.player = player; // player reference
		this.items = new ItemsContainer<>(28, false);
	}

	/*
	 * called to both players
	 */
	public void openTrade(Player target) {
		if (target == null) return;

		Trade a = this;
		Trade b = target.getTrade();

		Trade[] order = ordered(a, b);
		synchronized (order[0]) {
			synchronized (order[1]) {
				this.target = target;
				player.getPackets().sendTextOnComponent(335, 17, "Trading With: " + target.getDisplayName());
				player.getPackets().sendGlobalString(203, target.getDisplayName());
				sendInterItems();
				sendOptions();
				sendTradeModified();
				refreshFreeInventorySlots();
				refreshTradeWealth();
				refreshStageMessage(true);
				player.getInterfaceManager().sendInterface(335);
				player.getInterfaceManager().sendInventoryInterface(336);
				player.setCloseInterfacesEvent(() -> closeTrade(CloseTradeStage.CANCEL));
			}
		}
	}

	public void removeItem(final int slot, int amount) {
		synchronized (this) {
			if (!isTrading())
				return;
			Trade other = safeOtherTrade();
			if (other == null) return;
			synchronized (other) {
				Item item = items.get(slot);
				if (item == null)
					return;
				Item[] itemsBefore = items.getItemsCopy();
				int maxAmount = items.getNumberOf(item);
				Item toRemove = new Item(item.getId(), Math.min(amount, maxAmount));
				items.remove(slot, toRemove);

				if (toRemove.getId() != COINS_ID) {
					player.getInventory().addItem(toRemove);
				} else {
					player.getMoneyPouch().addMoney(toRemove.getAmount(), false);
				}

				refreshItems(itemsBefore);
				cancelAccepted();
				setTradeModified(true);
			}
		}
	}

	public void sendFlash(int slot) {
		player.getPackets().sendInterFlashScript(335, 33, 4, 7, slot);
		target.getPackets().sendInterFlashScript(335, 36, 4, 7, slot);
	}

	public void cancelAccepted() {
		boolean canceled = false;
		if (accepted) {
			accepted = false;
			canceled = true;
		}
		Trade other = safeOtherTrade();
		if (other != null && other.accepted) {
			other.accepted = false;
			canceled = true;
		}
		if (canceled)
			refreshBothStageMessage(canceled);
	}

	public void addItem(int slot, int amount) {
		synchronized (this) {
			if (!isTrading())
				return;
			Trade other = safeOtherTrade();
			if (other == null) return;
			synchronized (other) {
				Item item = player.getInventory().getItem(slot);
				if (item == null)
					return;
				if (!ItemConstants.isTradeable(item) && !player.isDeveloper()) {
					player.getPackets().sendGameMessage("That item isn't tradeable.");
					return;
				}

				Item[] itemsBefore = items.getItemsCopy();

				int maxAmount = player.getInventory().getItems().getNumberOf(item);
				int itemAmount = Math.min(amount, maxAmount);

				// Cap protection (legacy behavior preserved)
				for (Item tradeItems : items.getContainerItems()) {
					if (tradeItems == null)
						continue;
					if (tradeItems.getAmount() == Integer.MAX_VALUE) {
						player.getPackets().sendGameMessage("You can't trade more of that item.");
						return;
					}
					if (tradeItems.getAmount() + itemAmount < 0) { // overflow guard
						itemAmount = Integer.MAX_VALUE - tradeItems.getAmount();
					}
				}

				if (itemAmount <= 0) return;

				items.add(new Item(item.getId(), itemAmount));
				player.getInventory().deleteItem(slot, new Item(item.getId(), itemAmount));

				refreshItems(itemsBefore);
				cancelAccepted();
				setTradeModified(true);
			}
		}
	}

	public void addPouch(int amount) {
		synchronized (this) {
			if (!isTrading())
				return;
			Trade other = safeOtherTrade();
			if (other == null) return;
			synchronized (other) {
				Item[] itemsBefore = items.getItemsCopy();
				int pouchTotal = player.getMoneyPouch().getTotal();
				if (pouchTotal == 0) {
					player.getPackets().sendGameMessage("You don't have enough money to do that.");
					return;
				}

				int itemAmount = amount;

				// Cap protection (legacy behavior preserved)
				for (Item tradeItem : items.getContainerItems()) {
					if (tradeItem == null)
						continue;
					if (tradeItem.getAmount() == Integer.MAX_VALUE) {
						player.getPackets().sendGameMessage("You can't trade more of that item.");
						return;
					}
					if (tradeItem.getAmount() + amount < 0) { // overflow guard
						itemAmount = Integer.MAX_VALUE - tradeItem.getAmount();
						player.getPackets().sendGameMessage("You can't trade more of that item.");
					}
				}

				if (itemAmount > pouchTotal)
					itemAmount = pouchTotal;

				if (itemAmount <= 0) return;

				items.add(new Item(COINS_ID, itemAmount));
				player.getMoneyPouch().removeMoneyMisc(itemAmount);

				refreshItems(itemsBefore);
				cancelAccepted();
				setTradeModified(true);
			}
		}
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = items.getContainerItems()[index];
			if (itemsBefore[index] != item) {
				if (itemsBefore[index] != null && (item == null || item.getId() != itemsBefore[index].getId()
						|| item.getAmount() < itemsBefore[index].getAmount()))
					sendFlash(index);
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		refreshFreeInventorySlots();
		refreshTradeWealth();
	}

	public void sendOptions() {
		player.getPackets().sendInterSetItemsOptionsScript(336, 0, 93, 4, 7, "Offer", "Offer-5", "Offer-10",
				"Offer-All", "Offer-X", "Value<col=FF9040>", "Lend");
		player.getPackets().sendComponentSettings(336, 0, 0, 27, 1278);
		player.getPackets().sendInterSetItemsOptionsScript(335, 32, 90, 4, 7, "Remove", "Remove-5", "Remove-10",
				"Remove-All", "Remove-X", "Value");
		player.getPackets().sendComponentSettings(335, 32, 0, 27, 1150);
		player.getPackets().sendInterSetItemsOptionsScript(335, 35, 90, true, 4, 7, "Value");
		player.getPackets().sendComponentSettings(335, 35, 0, 27, 1026);

	}

	public boolean isTrading() {
		return target != null;
	}

	public void setTradeModified(boolean modified) {
		if (modified == tradeModified)
			return;
		tradeModified = modified;
		sendTradeModified();
	}

	public void sendInterItems() {
		player.getPackets().sendItems(90, items);
		target.getPackets().sendItems(90, true, items);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(90, items, slots);
		target.getPackets().sendUpdateItems(90, true, items.getContainerItems(), slots);
	}

	public void accept(boolean firstStage) {
		synchronized (this) {
			if (!isTrading())
				return;
			Trade other = safeOtherTrade();
			if (other == null) return;
			synchronized (other) {
				if (firstStage) {
					nextStage();
				} else {
					player.setCloseInterfacesEvent(null);
					player.closeInterfaces();
					closeTrade(CloseTradeStage.DONE);
				}
				accepted = true;
				refreshBothStageMessage(firstStage);
			}
		}
	}

	public void sendValue(int slot, boolean traders) {
		if (!isTrading())
			return;
		Item item = traders ? target.getTrade().items.get(slot) : items.get(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		int price = EconomyPrices.getPrice(item.getId());
		player.getPackets().sendGameMessage(item.getDefinitions().getName() + ": market price is " + price + " coins.");
	}

	public void sendValue(int slot) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		int price = EconomyPrices.getPrice(item.getId());
		player.getPackets().sendGameMessage(item.getDefinitions().getName() + ": market price is " + price + " coins.");
	}

	public void sendExamine(int slot, boolean traders) {
		if (!isTrading())
			return;
		Item item = traders ? target.getTrade().items.get(slot) : items.get(slot);
		if (item == null)
			return;
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void nextStage() {
		if (!isTrading())
			return;

		Player otherPlayer = target;
		if (otherPlayer == null) return;

		boolean iFit = canFitIncoming(player, otherPlayer.getTrade().items);
		boolean theyFit = canFitIncoming(otherPlayer, this.items);

		if (!iFit || !theyFit) {
			if (!iFit) {
				notifyNoSpace(player, "You don't have enough space in your inventory to continue the trade.");
				notifyNoSpace(otherPlayer, player.getDisplayName() + " doesn't have enough space in their inventory to continue the trade.");
				outOfSpaceMessage(player, otherPlayer.getDisplayName() + " doesn't have enough space in their inventory to continue the trade.");
			}
			if (!theyFit) {
				notifyNoSpace(player, otherPlayer.getDisplayName() + " doesn't have enough space in their inventory to continue the trade.");
				notifyNoSpace(otherPlayer, "You don't have enough space in your inventory to continue the trade.");
				outOfSpaceMessage(otherPlayer, otherPlayer.getDisplayName() + " doesn't have enough space in their inventory to continue the trade.");
			}
			return;
		}

		accepted = false;
		player.getInterfaceManager().sendInterface(334);
		player.getInterfaceManager().closeInventoryInterface();
		player.getPackets().sendHideIComponent(334, 55, !(tradeModified || target.getTrade().tradeModified));
		refreshBothStageMessage(false);
	}

	public void refreshBothStageMessage(boolean firstStage) {
		refreshStageMessage(firstStage);
		target.getTrade().refreshStageMessage(firstStage);
	}

	public void outOfSpaceMessage(Player player, String message) {
		player.getPackets().sendTextOnComponent(335, 39, message);
	}

	public void refreshStageMessage(boolean firstStage) {
		player.getPackets().sendTextOnComponent(firstStage ? 335 : 334, firstStage ? 39 : 34,
				getAcceptMessage(firstStage));
	}

	public String getAcceptMessage(boolean firstStage) {
		if (accepted)
			return "Waiting for other player...";
		if (target.getTrade().accepted)
			return "Other player has accepted.";
		return firstStage ? "" : "Are you sure you want to make this trade?";
	}

	public void sendTradeModified() {
		player.getPackets().sendVar(1042, tradeModified ? 1 : 0);
		target.getPackets().sendVar(1043, tradeModified ? 1 : 0);
	}

	public void refreshTradeWealth() {
		long wealthLong = getTradeWealth(); // compute in long
		int wealth = wealthLong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) wealthLong;
		player.getPackets().sendGlobalVar(729, wealth);
		target.getPackets().sendGlobalVar(697, wealth);
	}

	public void refreshFreeInventorySlots() {
		int freeSlots = player.getInventory().getFreeSlots();
		target.getPackets().sendTextOnComponent(335, 23,
				"has " + (freeSlots == 0 ? "no" : freeSlots) + " free" + "<br>inventory slots");
	}

	private static boolean canFitIncoming(Player receiver, ItemsContainer<Item> incoming) {
		if (receiver == null || incoming == null) return false;
		int invUsed = receiver.getInventory().getItems().getUsedSlots();
		int incomingStacks = incoming.getUsedSlots();
		return invUsed + incomingStacks <= 28;
	}

	private void notifyNoSpace(Player p, String msg) {
		if (p != null && p.getPackets() != null) {
			p.getPackets().sendGameMessage(msg);
		}
	}


	public long getTradeWealth() {
		long wealth = 0L;
		for (Item item : items.getContainerItems()) {
			if (item == null)
				continue;
			wealth += (long) EconomyPrices.getPrice(item.getId()) * (long) item.getAmount();
		}
		return wealth;
	}

	public enum CloseTradeStage {
		CANCEL, NO_SPACE, DONE
	}

	public static String currentTime(String dateFormat) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static void archiveTrade(Player player, Player p2, ItemsContainer<Item> items, ItemsContainer<Item> items2) {
		if (player == null || p2 == null) return;
		String safeUser = sanitizeUsername(player.getUsername());
		Path dir = Path.of("data", "logs", "trade");
		Path file = dir.resolve(safeUser + ".txt");

		try {
			Files.createDirectories(dir);
		} catch (IOException ignore) {
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), true))) {
			writer.write("[" + currentTime("yyyy-MM-dd HH:mm:ss 'UTC'") + "] - " + player.getUsername() + " traded "
					+ p2.getUsername());
			writer.newLine();
			writer.write(player.getUsername() + " items:");
			writer.newLine();
			for (Item item : items.getContainerItems()) {
				if (item == null)
					continue;
				writer.write(item.getDefinitions().getName() + " x " + item.getAmount());
				writer.newLine();
			}
			writer.write("for " + p2.getUsername() + "'s:");
			writer.newLine();
			for (Item item : items2.getContainerItems()) {
				if (item == null)
					continue;
				writer.write(item.getDefinitions().getName() + " x " + item.getAmount());
				writer.newLine();
			}
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void closeTrade(CloseTradeStage stage) {
		// Snapshot the other player/trade early & null-check
		Player oldTarget = this.target;
		if (oldTarget == null) {
			// Nothing to do; already closed on this side.
			return;
		}
		Trade otherTrade = oldTarget.getTrade();

		Trade[] order = ordered(this, otherTrade);
		synchronized (order[0]) {
			// Log once while we still have both snapshots
			archiveTrade(player, oldTarget, this.items, otherTrade.items);

			synchronized (order[1]) {
				// Clear local state / perform transfer
				this.target = null;
				this.tradeModified = false;
				this.accepted = false;

				if (CloseTradeStage.DONE != stage) {
					// Return my offered items
					for (Item tradedItems : items.getContainerItems()) {
						if (tradedItems == null)
							continue;
						if (tradedItems.getId() == COINS_ID)
							player.getMoneyPouch().addMoney(tradedItems.getAmount(), false);
						else
							player.getInventory().addItem(tradedItems);
					}
					//player.getInventory().getItems().addAll(items);
					player.getInventory().init();
					items.clear();
				} else {
					player.getPackets().sendGameMessage("Accepted trade.");
                    for (Item tradedItems : otherTrade.items.getContainerItems()) {
						if (tradedItems == null)
							continue;
						if (tradedItems.getId() == COINS_ID)
							player.getMoneyPouch().addMoney(tradedItems.getAmount(), false);
						else
							player.getInventory().addItem(tradedItems);
					}
					player.getInventory().init();
					otherTrade.items.clear();
				}

				if (otherTrade.isTrading()) {
					oldTarget.setCloseInterfacesEvent(null);
					oldTarget.closeInterfaces();
					// This will re-enter but locks are ordered and 'synchronized' is reentrant
					otherTrade.closeTrade(stage);

					if (CloseTradeStage.CANCEL == stage)
						oldTarget.getPackets().sendGameMessage("<col=ff0000>Other player declined trade!");
					else if (CloseTradeStage.NO_SPACE == stage) {
						player.getPackets()
								.sendGameMessage("You don't have enough space in your inventory for this trade.");
						oldTarget.getPackets().sendGameMessage(
								"Other player doesn't have enough space in their inventory for this trade.");
					}
				}
			}
		}
	}

	// ---------- helpers ----------

	private static Trade[] ordered(Trade a, Trade b) {
		if (a == b) return new Trade[]{a, b};
		// Prefer stable name-based ordering; if names are equal, fall back to identity hash
		String an = a.player != null ? a.player.getUsername() : "";
		String bn = b.player != null ? b.player.getUsername() : "";
		int cmp = an.compareToIgnoreCase(bn);
		if (cmp < 0) return new Trade[]{a, b};
		if (cmp > 0) return new Trade[]{b, a};
		// Equal usernames (unlikely) -> identityHashCode
		return (System.identityHashCode(a) < System.identityHashCode(b))
				? new Trade[]{a, b}
				: new Trade[]{b, a};
	}

	private Trade safeOtherTrade() {
		Player t = this.target;
		return (t != null) ? t.getTrade() : null;
	}

	private static String sanitizeUsername(String username) {
		if (username == null) return "unknown";
		// Allow only safe filename chars
		return username.replaceAll("[^A-Za-z0-9._-]", "_");
	}
}
