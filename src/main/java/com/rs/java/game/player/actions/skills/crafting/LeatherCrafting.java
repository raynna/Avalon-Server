package com.rs.java.game.player.actions.skills.crafting;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.java.game.player.actions.skills.crafting.leather.LeatherData;
import com.rs.java.game.player.actions.skills.crafting.leather.LeatherProduct;
import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;

public class LeatherCrafting extends Action {

	public static final int THREAD = 1734;
	public static final int DUNG_NEEDLE = 17446;
	public static final int NORMAL_NEEDLE = 1733;
	private static final int CRAFT_ANIMATION = 1249;

	private int crafted = 0;

	private LeatherData data;
	private LeatherProduct product;
	private int quantity;

	public LeatherCrafting(LeatherData data, int option, int quantity) {
		this.data = data;
		this.product = data.getProducts()[option];
		this.quantity = quantity;
	}

	@Override
	public boolean start(Player player) {
		return check(player);
	}

	private boolean check(Player player) {

		if (player.getSkills().getLevel(Skills.CRAFTING) < product.getLevel()) {
			player.message("You need a Crafting level of " + product.getLevel() + ".");
			return false;
		}
		if (!player.getToolbelt().contains(NORMAL_NEEDLE) && !player.getInventory().containsOneItem(NORMAL_NEEDLE)) {
			player.message("You don't have a needle to craft with.");
			return false;
		}
		if (!player.getInventory().containsItem("item.thread", 1)) {
			player.message("You don't have any thread to craft with.");
			return false;
		}

		StringBuilder missing = new StringBuilder();

		for (ReqItem req : product.getRequirements()) {
			int have = player.getInventory().getAmountOf(req.getId());

			if (have < req.getAmount()) {
				if (!missing.isEmpty())
					missing.append(", ");

				missing.append(req.getAmount())
						.append(" ")
						.append(ItemDefinitions.getItemDefinitions(req.getId()).getName());
			}
		}

		if (!missing.isEmpty()) {
			player.message("You need: " + missing + ".");
			return false;
		}

		return true;
	}

	public static LeatherData getLeatherData(int itemId) {
		for (LeatherData data : LeatherData.values()) {
			if (data.getBaseLeather() == itemId)
				return data;
		}
		return null;
	}

	public static LeatherData getLeatherData(Item used, Item usedWith) {
		int id1 = used.getId();
		int id2 = usedWith.getId();

		boolean hasNeedle =
				id1 == DUNG_NEEDLE || id2 == DUNG_NEEDLE ||
						id1 == NORMAL_NEEDLE || id2 == NORMAL_NEEDLE;

		if (!hasNeedle)
			return null;

		for (LeatherData data : LeatherData.values()) {
			int leatherId = data.getBaseLeather();
			if (leatherId == id1 || leatherId == id2)
				return data;
		}
		return null;
	}

	@Override
	public boolean process(Player player) {

		if (quantity <= 0)
			return false;
		player.animate(CRAFT_ANIMATION);
		if (player.getSkills().getLevel(Skills.CRAFTING) < product.getLevel()) {
			player.message("You need a Crafting level of " + product.getLevel() + " to continue crafting this.");
			return false;
		}
		if (!player.getInventory().containsItem("item.thread", 1)) {
			player.message("You ran out of thread.");
			return false;
		}

		if (product.getRequirements().length == 0) {
			if (!player.getInventory().containsItem(data.getBaseLeather(), 1)) {
				String name = ItemDefinitions.getItemDefinitions(data.getBaseLeather()).getName();
				player.message("You have run out of " + name + ".");
				return false;
			}
			return true;
		}

		for (ReqItem req : product.getRequirements()) {
			if (!player.getInventory().containsItem(req.getId(), req.getAmount())) {
				String name = ItemDefinitions.getItemDefinitions(req.getId()).getName();
				player.message("You have run out of " + name + ".");
				return false;
			}
		}

		return true;
	}


	@Override
	public int processWithDelay(Player player) {

		Integer crafted = (Integer) player.getTemporaryAttributtes().get("THREAD_CRAFT_PROGRESS");
		if (crafted == null)
			crafted = 0;

		quantity--;

		if (product.getRequirements().length == 0) {
			player.getInventory().deleteItem(data.getBaseLeather(), 1);
		} else {
			for (ReqItem req : product.getRequirements()) {
				player.getInventory().deleteItem(req.getId(), req.getAmount());
			}
		}

		crafted++;

		if (crafted % 5 == 0) {
			player.getInventory().deleteItem(THREAD, 1);
		}

		player.getTemporaryAttributtes().put("THREAD_CRAFT_PROGRESS", crafted);

		player.getInventory().addItem(product.getId(), 1);
		player.getSkills().addXp(Skills.CRAFTING, product.getXp());

		return 3;
	}



	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}
