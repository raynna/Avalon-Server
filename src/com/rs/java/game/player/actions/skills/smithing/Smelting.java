package com.rs.java.game.player.actions.skills.smithing;

import java.util.HashMap;
import java.util.Map;

import com.rs.java.game.Animation;
import com.rs.java.game.WorldObject;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.java.utils.Utils;

public class Smelting extends Action {

	public enum SmeltingBar {

		BRONZE(1, 6.2, new Item[] { new Item(436), new Item(438) }, new Item(2349), 0),

		BLURITE(8, 8.0, new Item[] { new Item(668) }, new Item(9467), 1),

		IRON(15, 12.5, new Item[] { new Item(440) }, new Item(2351), 2),

		SILVER(20, 13.7, new Item[] { new Item(442) }, new Item(2355), 3),

		STEEL(30, 17.5, new Item[] { new Item(440), new Item(453, 2) }, new Item(2353), 4),

		CANNONBALL(35, 25.6, new Item[] { new Item(4), new Item(2353) }, new Item(2, 4), 5),

		GOLD(40, 22.5, new Item[] { new Item(444) }, new Item(2357), 6),

		MITHRIL(50, 30, new Item[] { new Item(447), new Item(453, 4) }, new Item(2359), 7),

		ADAMANT(70, 37.5, new Item[] { new Item(449), new Item(453, 6) }, new Item(2361), 8),

		RUNE(85, 50, new Item[] { new Item(451), new Item(453, 8) }, new Item(2363), 9),

		DRAGONBANE(80, 50, new Item[] { new Item(21779) }, new Item(21783, 1), 10),

		WALLASALKIBANE(80, 50, new Item[] { new Item(21780) }, new Item(21784, 1), 10),

		BASILISKBANE(80, 50, new Item[] { new Item(21781) }, new Item(21785, 1), 11),

		ABYSSSALBANE(80, 50, new Item[] { new Item(21782) }, new Item(21786, 1), 12);

		private static Map<Integer, SmeltingBar> bars = new HashMap<Integer, SmeltingBar>();

		public static SmeltingBar forId(int buttonId) {
			return bars.get(buttonId);
		}

		static {
			for (SmeltingBar bar : SmeltingBar.values()) {
				bars.put(bar.getButtonId(), bar);
			}
		}

		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private int buttonId;
		private Item producedBar;

		private SmeltingBar(int levelRequired, double experience, Item[] itemsRequired, Item producedBar,
				int buttonId) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.itemsRequired = itemsRequired;
			this.producedBar = producedBar;
			this.buttonId = buttonId;
		}

		public Item[] getItemsRequired() {
			return itemsRequired;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item getProducedBar() {
			return producedBar;
		}

		public double getExperience() {
			return experience;
		}

		public int getButtonId() {
			return buttonId;
		}
	}

	public SmeltingBar bar;
	public WorldObject object;
	public int ticks;

	public Smelting(int slotId, WorldObject object, int ticks) {
		this.object = object;
		this.bar = SmeltingBar.forId(slotId);
		this.ticks = ticks;
	}

	public Smelting(SmeltingBar smeltingBar, WorldObject object2, int quantity) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean start(Player player) {
		if (bar == null || player == null || object == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.SMITHING) < bar.getLevelRequired()) {
			player.getPackets().sendGameMessage("You need a Smithing level of at least " + bar.getLevelRequired()
					+ " to make " + bar.getProducedBar().getDefinitions().getName());
			return false;
		}
		if (bar.getItemsRequired().length > 1) {
			if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(),
					bar.getItemsRequired()[1].getAmount())) {
				player.getPackets()
						.sendGameMessage("You need atleast " + bar.getItemsRequired()[0].getAmount() + " "
								+ bar.getItemsRequired()[0].getDefinitions().getName() + " and "
								+ bar.getItemsRequired()[1].getAmount() + " "
								+ bar.getItemsRequired()[1].getDefinitions().getName() + " to create a "
								+ bar.getProducedBar().getDefinitions().getName() + ".");
				return false;
			}
		} else {
			if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(),
					bar.getItemsRequired()[0].getAmount())) {
				player.getPackets()
						.sendGameMessage("You need atleast " + bar.getItemsRequired()[0].getAmount() + " "
								+ bar.getItemsRequired()[0].getDefinitions().getName() + " to create a "
								+ bar.getProducedBar().getDefinitions().getName() + ".");
				return false;
			}
		}
		player.getPackets().sendGameMessage("You place the required materials and attempt to create "
				+ (bar.producedBar.getId() == 2 ? "some cannonballs."
						: "a bar of "
								+ bar.getProducedBar().getDefinitions().getName().toLowerCase().replace(" bar", "")
								+ "."),
				true);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (bar == null || player == null || object == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.SMITHING) < bar.getLevelRequired()) {
			player.getPackets().sendGameMessage("You need a Smithing level of at least " + bar.getLevelRequired()
					+ " to make " + bar.getProducedBar().getDefinitions().getName());
			return false;
		}
		if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(),
				bar.getItemsRequired()[0].getAmount())) {
			player.getPackets().sendGameMessage("You need " + bar.getItemsRequired()[0].getDefinitions().getName()
					+ " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
			return false;
		}
		if (bar.getItemsRequired().length > 1) {
			if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(),
					bar.getItemsRequired()[1].getAmount())) {
				player.getPackets().sendGameMessage("You need " + bar.getItemsRequired()[1].getDefinitions().getName()
						+ " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
				return false;
			}
		}
		player.faceObject(object);
		return true;
	}

	public boolean isSuccessFull(Player player) {
		if (bar == SmeltingBar.IRON) {
			if (player.getEquipment().getItem(Equipment.SLOT_RING) != null
					&& player.getEquipment().getItem(Equipment.SLOT_RING).getId() == 2568) {
				return true;
			} else {
				return Utils.getRandom(100) <= (player.getSkills().getLevel(Skills.SMITHING) >= 45 ? 80 : 50);
			}
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		ticks--;
		player.animate(new Animation(3243));
		player.getSkills().addXp(Skills.SMITHING, bar.getExperience());
		for (Item required : bar.getItemsRequired()) {
			if (required.getId() == 4)
				continue;
			player.getInventory().deleteItem(required.getId(), required.getAmount());
		}
		if (isSuccessFull(player)) {
			player.getInventory().addItem(bar.getProducedBar());
			player.getPackets()
					.sendGameMessage("You retrieve " + (bar.getProducedBar().getId() == 2 ? "some cannonballs."
							: "a bar of "
									+ bar.getProducedBar().getDefinitions().getName().toLowerCase().replace(" bar", "")
									+ "."),
							true);
		} else {
			player.getPackets().sendGameMessage("The ore is too impure and you fail to refine it.", true);
		}
		if (ticks > 0)
			return 2;
		return -1;
	}

	@Override
	public void stop(Player player) {
		this.setActionDelay(player, 3);
	}
}
