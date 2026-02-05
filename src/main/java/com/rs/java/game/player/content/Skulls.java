package com.rs.java.game.player.content;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemsContainer;
import com.rs.java.game.minigames.clanwars.FfaZone;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.grandexchange.GrandExchange;
import com.rs.java.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;

public class Skulls {

	private static final Comparator<ItemValue> VALUE_DESC =
			(a, b) -> Long.compare(b.unitValue, a.unitValue);

	/**
	 * Returns cached risked wealth.
	 * Rebuilds only if player.wealthDirty == true
	 */
	public static long getRiskedWealth(Player player) {

		if (!player.isWealthDirty())
			return player.getRiskedWealth();

		long total = 0;
		long protectedValue = 0;

		int keepAmount =
				(player.hasSkull() || FfaZone.inRiskArea(player)) ? 0 : 3;

		if (player.getPrayer().hasProtectItemPrayerActive())
			keepAmount++;

		ArrayList<ItemValue> stacks = new ArrayList<>(32);

		ItemsContainer<Item> inv = player.getInventory().getItems();
		for (int i = 0; i < inv.getSize(); i++) {
			Item item = inv.get(i);
			if (item == null)
				continue;

			int stage = item.getDefinitions().getStageOnDeath();

			if (stage == 1 || stage == -1 || ItemConstants.keptOnDeath(item)) {
				continue;
			}

			long price = GrandExchange.getPrice(item.getId());
			long value = price * item.getAmount();

			total += value;
			stacks.add(new ItemValue(item.getAmount(), price));
		}

		ItemsContainer<Item> equip = player.getEquipment().getItems();
		for (int i = 0; i < equip.getSize(); i++) {
			Item item = equip.get(i);
			if (item == null)
				continue;

			int stage = item.getDefinitions().getStageOnDeath();

			if (stage == 1 || stage == -1 || ItemConstants.keptOnDeath(item)) {
				continue;
			}

			long price = GrandExchange.getPrice(item.getId());
			long value = price * item.getAmount();

			total += value;
			stacks.add(new ItemValue(item.getAmount(), price));

		}

		stacks.sort(VALUE_DESC);

		int keptUnits = 0;

		for (ItemValue stack : stacks) {
			if (keptUnits >= keepAmount)
				break;

			int take = Math.min(stack.amount, keepAmount - keptUnits);
			protectedValue += take * stack.unitValue;
			keptUnits += take;
		}

		long risk = total - protectedValue;

		player.setCarriedWealth(total);
		player.setProtectedWealth(protectedValue);
		player.setRiskedWealth(risk);
		player.setWealthDirty(false);

		return risk;
	}

	/**
	 * Updates skull colour based on risked wealth.
	 * Safe to call frequently.
	 */
	public static void checkSkulls(Player player, boolean wilderness) {

		long risk = getRiskedWealth(player);
		int newSkull;

		if (risk < 100_000)
			newSkull = 6; // Brown
		else if (risk < 1_000_000)
			newSkull = 5; // Silver
		else if (risk < 10_000_000)
			newSkull = 4; // Green
		else if (risk < 50_000_000)
			newSkull = 3; // Blue
		else
			newSkull = 2; // Red

		if (player.skullId != newSkull) {
			player.skullId = newSkull;
			player.getAppearence().generateAppearenceData();
		}
	}

    /* =====================
       SMALL HELPER CLASS
    ====================== */

	private static final class ItemValue {
		final int amount;
		final long unitValue;

		ItemValue(int amount, long unitValue) {
			this.amount = amount;
			this.unitValue = unitValue;
		}
	}
}
