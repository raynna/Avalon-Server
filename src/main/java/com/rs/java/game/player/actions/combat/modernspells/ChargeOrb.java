package com.rs.java.game.player.actions.combat.modernspells;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;

public class ChargeOrb extends Action {

	public enum Orbs {
		AIR_ORB(573, 137.5, 66, 150),

		WATER_ORB(571, 100, 54, 149),

		EARTH_ORB(575, 112.5, 58, 151),

		FIRE_ORB(569, 125, 62, 152);

		private double experience;
		private int levelRequired;
		private int newId;
		private int gfxId;

		private Orbs(int newId, double experience, int levelRequired, int gfxId) {
			this.newId = newId;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.setGfxId(gfxId);
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public double getExperience() {
			return experience;
		}

		public int getNewId() {
			return newId;
		}

		public int getGfxId() {
			return gfxId;
		}

		public void setGfxId(int gfxId) {
			this.gfxId = gfxId;
		}
	}

	private int itemId;
	private int quantity;

	public ChargeOrb(int itemId, int quantity) {
		this.setItemId(itemId);
		this.setQuantity(quantity);
	}

	public boolean checkAll(Player player) {
		if (!hasRunes(player)) {
			player.getPackets().sendGameMessage("You don't have required items to charge this orb.");
			return false;
		}
		Orbs orb = getOrb(getItemId());
		if (orb != null) {
			if (player.getSkills().getLevel(Skills.MAGIC) < orb.getLevelRequired()) {
				player.getPackets().sendGameMessage(
						"You need at least a level of " + orb.getLevelRequired() + " magic to charge this orb.");
				return false;
			}
		}
		return true;
	}

	public static void charge(Player player, int itemId) {
		// int itemId = -1;
		if (player.getTemporaryAttributtes().get("spell_itemid") != null)
			itemId = (int) player.getTemporaryAttributtes().get("spell_itemid");
		player.getDialogueManager().startDialogue("ChargeOrbD", itemId);
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			setActionDelay(player, 1);
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		if (!hasRunes(player)) {
			player.getPackets().sendGameMessage("You don't have required items to charge this orb.");
			return false;
		}
		if (!player.inventory.containsItem("item.unpowered_orb")) {
			player.message("You need an unpowered orb to charge.");
			return false;
		}
		return checkAll(player);
	}

	public static Orbs getOrb(int i) {
		for (Orbs orb : Orbs.values()) {
			if (orb.getNewId() == i)
				return orb;
		}
		return null;
	}

	public static boolean hasRunes(Player player) {
		Item[] runes = (Item[]) player.getTemporaryAttributtes().get("spell_runes");

		if (runes == null)
			return false;

		for (Item item : runes) {
			if (item == null)
				continue;
			if (!player.getInventory().containsItem(item.getId(), item.getAmount()))
				return false;
		}

		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		Orbs orb = getOrb(getItemId());
		if (orb != null) {
			Item[] runes = (Item[]) player.getTemporaryAttributtes().get("spell_runes");
			if (runes == null)
				return -1;

			for (Item item : runes) {
				if (item == null)
					continue;
				player.getInventory().deleteItem(item.getId(), item.getAmount());
			}
			player.getSkills().addXp(Skills.CRAFTING, orb.getExperience());
			if (player.getTemporaryAttributtes().get("spell_xp") != null)
			player.getSkills().addXp(Skills.MAGIC, (double) player.getTemporaryAttributtes().get("spell_xp"));
			player.getInventory().deleteItem("item.unpowered_orb", 1);
			player.getInventory().addItem(orb.getNewId(), 1);
			player.getPackets().sendGameMessage("You charge the unpowered orb into an "
					+ ItemDefinitions.getItemDefinitions(orb.getNewId()).getName() + ".", true);
			player.gfx(new Graphics(orb.getGfxId(), 0, 150));
			player.animate(new Animation(723));
			setQuantity(getQuantity() - 1);
			if (getQuantity() <= 0)
				return -1;
			return 4;
		}
		return -1;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
}
