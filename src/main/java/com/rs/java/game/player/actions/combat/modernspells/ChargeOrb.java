package com.rs.java.game.player.actions.combat.modernspells;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.kotlin.game.player.combat.magic.RuneService;
import com.rs.kotlin.game.player.combat.magic.Spell;

public class ChargeOrb extends Action {

	public enum Orbs {

		AIR_ORB(573, 137.5, 66, 150),
		WATER_ORB(571, 100, 54, 149),
		EARTH_ORB(575, 112.5, 58, 151),
		FIRE_ORB(569, 125, 62, 152);

		private final double experience;
		private final int levelRequired;
		private final int newId;
		private final int gfxId;

		Orbs(int newId, double experience, int levelRequired, int gfxId) {
			this.newId = newId;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.gfxId = gfxId;
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
	}

	private final Spell spell;
	private int itemId;
	private int quantity;

	public ChargeOrb(Spell spell, int itemId, int quantity) {
		this.spell = spell;
		this.itemId = itemId;
		this.quantity = quantity;
	}

	private boolean checkAll(Player player) {

		if (!RuneService.INSTANCE.hasRunes(player, spell.getRunes())) {
			player.getPackets().sendGameMessage("You don't have the required runes to charge this orb.");
			return false;
		}

		Orbs orb = getOrb(itemId);

		if (orb != null) {
			if (player.getSkills().getLevel(Skills.MAGIC) < orb.getLevelRequired()) {
				player.getPackets().sendGameMessage(
						"You need at least level " + orb.getLevelRequired() + " Magic to charge this orb.");
				return false;
			}
		}

		if (!player.getInventory().containsItem("item.unpowered_orb")) {
			player.message("You need an unpowered orb to charge.");
			return false;
		}

		return true;
	}

	public static Orbs getOrb(int id) {
		for (Orbs orb : Orbs.values()) {
			if (orb.getNewId() == id) {
				return orb;
			}
		}
		return null;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player)) {
			return false;
		}

		setActionDelay(player, 1);
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {

		Orbs orb = getOrb(itemId);

		if (orb == null) {
			return -1;
		}

		if (!RuneService.INSTANCE.hasRunes(player, spell.getRunes())) {
			player.getPackets().sendGameMessage("You have run out of runes.");
			return -1;
		}

		RuneService.INSTANCE.consumeRunes(player, spell.getRunes());

		player.getInventory().deleteItem("item.unpowered_orb", 1);

		player.getInventory().addItem(orb.getNewId(), 1);

		player.getSkills().addXp(Skills.CRAFTING, orb.getExperience());
		player.getSkills().addXp(Skills.MAGIC, spell.getXp());

		player.getPackets().sendGameMessage(
				"You charge the orb into an " +
						ItemDefinitions.getItemDefinitions(orb.getNewId()).getName() + ".", true);

		player.gfx(new Graphics(orb.getGfxId(), 0, 150));
		player.animate(new Animation(723));

		quantity--;

		if (quantity <= 0) {
			return -1;
		}

		return 4;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}