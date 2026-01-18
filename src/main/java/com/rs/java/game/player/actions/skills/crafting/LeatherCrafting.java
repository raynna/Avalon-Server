package com.rs.java.game.player.actions.skills.crafting;

import java.util.HashMap;
import java.util.Map;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemId;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.java.utils.Utils;

/**
 *
 * @Improved Andreas - AvalonPK
 *
 */

public class LeatherCrafting extends Action {

	public static int THREAD = 1734;
	public static int DUNG_NEEDLE = 17446;

	private LeatherData data;
	private int option;
	private int quantity;

	public LeatherCrafting(LeatherData data, int option, int quantity) {
		this.data = data;
		this.option = option;
		this.quantity = quantity;
	}

	@Override
	public boolean start(Player player) {
		return check(player);
	}

	private boolean check(Player player) {
		if (player.getSkills().getLevel(Skills.CRAFTING) < data.getLevels()[option]) {
			player.getPackets().sendGameMessage("You need a Crafting level of " + data.getLevels()[option] + ".");
			return false;
		}
		return true;
	}

	public static LeatherData getLeatherData(Item used, Item usedWith) {
		for (LeatherData data : LeatherData.values()) {
			if (data.getBaseLeather() == used.getId() || data.getBaseLeather() == usedWith.getId())
				return data;
		}
		return null;
	}


	@Override
	public boolean process(Player player) {
		return quantity > 0 && check(player);
	}

	@Override
	public int processWithDelay(Player player) {
		quantity--;

		Item product = data.getProduct(option);

		player.getInventory().deleteItem(data.getBaseLeather(), product.getAmount());
		player.getInventory().deleteItem(LeatherCrafting.THREAD, 1);

		player.getInventory().addItem(product.getId(), 1);
		player.getSkills().addXp(Skills.CRAFTING, data.getXp()[option]);

		return 3;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}

