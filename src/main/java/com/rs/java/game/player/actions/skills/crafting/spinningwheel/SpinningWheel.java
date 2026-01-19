package com.rs.java.game.player.actions.skills.crafting.spinningwheel;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.WorldObject;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;

public class SpinningWheel extends Action {

	private static final int SPIN_ANIM = 896;

	private final SpinningProduct product;
	private final WorldObject object;
	private int quantity;

	public SpinningWheel(int slotId, WorldObject object, int quantity) {
		this.product = SpinningData.values()[slotId].getProduct();
		this.object = object;
		this.quantity = quantity;
	}

	@Override
	public boolean start(Player player) {
		return check(player);
	}

	private boolean check(Player player) {

		if (product == null)
			return false;

		if (player.getSkills().getLevel(Skills.CRAFTING) < product.getLevel()) {
			player.message("You need a Crafting level of at least " + product.getLevel() + ".");
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

	@Override
	public boolean process(Player player) {

		if (quantity <= 0)
			return false;

		if (player.getSkills().getLevel(Skills.CRAFTING) < product.getLevel()) {
			player.message("You need a Crafting level of at least " + product.getLevel() + " to continue spinning.");
			return false;
		}

		for (ReqItem req : product.getRequirements()) {
			if (!player.getInventory().containsItem(req.getId(), req.getAmount())) {
				String name = ItemDefinitions.getItemDefinitions(req.getId()).getName();
				player.message("You have run out of " + name + ".");
				return false;
			}
		}

		player.faceObject(object);
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		quantity--;

		player.animate(new Animation(SPIN_ANIM));

		for (ReqItem req : product.getRequirements()) {
			player.getInventory().deleteItem(req.getId(), req.getAmount());
		}

		player.getInventory().addItem(product.getId(), 1);

		if (product.getXp() > 0)
			player.getSkills().addXp(Skills.CRAFTING, product.getXp());
		String productName = ItemDefinitions.getItemDefinitions(product.getId()).getName();
		player.message("You make a " + productName.toLowerCase() + ".", true);

		return quantity > 0 ? 3 : -1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}
