package com.rs.java.game.player.dialogues.skilling;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.WorldObject;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.crafting.loom.Loom;
import com.rs.java.game.player.actions.skills.crafting.loom.LoomData;
import com.rs.java.game.player.actions.skills.crafting.loom.LoomProduct;
import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;
import com.rs.java.game.player.content.SkillsDialogue;
import com.rs.java.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.java.game.player.dialogues.Dialogue;
import com.rs.java.utils.HexColours;

public class LoomD extends Dialogue {

	private WorldObject object;
	private final LoomData[] data = LoomData.values();

	@Override
	public void start() {
		object = (WorldObject) parameters[0];

		int[] ids = new int[data.length];
		for (int i = 0; i < ids.length; i++)
			ids[i] = data[i].getProduct().getId();

		SkillsDialogue.sendSkillsDialogue(
				player,
				SkillsDialogue.MAKE,
				"Choose how many you wish to make,<br>then click on the item to begin.",
				28,
				ids,
				new ItemNameFilter() {

					int index = -1;

					@Override
					public String rename(String name) {
						index++;

						LoomProduct prod = data[index].getProduct();

						if (player.getSkills().getLevel(Skills.CRAFTING) < prod.getLevel())
							return "<col=ff0000>" + name + "<br><col=ff0000>Level " + prod.getLevel();

						if (!hasRequiredItems(prod)) {
							ReqItem r = prod.getRequirements()[0];
							String itemName = ItemDefinitions.getItemDefinitions(r.getId()).getName();
							return name + "<br>" + HexColours.Colour.RED.getHex() +
									"(" + r.getAmount() + " " + itemName + ")";
						}

						return itemMessage(name);
					}
				});
	}

	private boolean hasRequiredItems(LoomProduct product) {
		for (ReqItem r : product.getRequirements()) {
			if (!player.getInventory().containsItem(r.getId(), r.getAmount()))
				return false;
		}
		return true;
	}

	private String itemMessage(String itemName) {
		String defs = itemName.toLowerCase();

		if (defs.contains("sack"))
			return itemName.replace("Empty s", "S") + "<br>(4 jute fibres)";
		if (defs.contains("basket"))
			return itemName + "<br>(6 willow branches)";
		if (defs.contains("cloth"))
			return itemName.replace("Strip of c", "C") + "<br>(2 balls of wool)";
		if (defs.contains("net"))
			return itemName.replace("Unfinished", "Seaweed") + "<br>(5 flax)";
		if (defs.contains("milestone"))
			return itemName.replace(" (10)", "s") + "<br>(ball of wool)";

		return itemName;
	}

	@Override
	public void run(int interfaceId, int componentId) {

		int option = SkillsDialogue.getItemSlot(componentId);

		if (option >= data.length) {
			end();
			return;
		}

		LoomProduct product = data[option].getProduct();
		if (ItemDefinitions.getItemDefinitions(product.getId()).getName().toLowerCase().contains("milestone")) {
			player.getDialogueManager().startDialogue("MilestoneD");
			return;
		}

		int quantity = SkillsDialogue.getQuantity(player);

		player.getActionManager().setAction(
				new Loom(option, object, quantity)
		);
		end();
	}

	@Override
	public void finish() {
	}
}
