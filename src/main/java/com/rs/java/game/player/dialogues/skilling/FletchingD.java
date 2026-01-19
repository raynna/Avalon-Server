package com.rs.java.game.player.dialogues.skilling;

import com.rs.java.game.item.Item;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.fletching.Fletching;
import com.rs.java.game.player.actions.skills.fletching.FletchingData;
import com.rs.java.game.player.actions.skills.fletching.FletchingProduct;
import com.rs.java.game.player.content.SkillsDialogue;
import com.rs.java.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.java.game.player.dialogues.Dialogue;

public class FletchingD extends Dialogue {

	private FletchingData data;

	@Override
	public void start() {
		data = (FletchingData) parameters[0];

		boolean maxQuantityTen = Fletching.maxMakeQuantityTen(new Item(data.getToolId()));

		int[] productIds = new int[data.getProducts().length];
		for (int i = 0; i < data.getProducts().length; i++) {
			productIds[i] = data.getProducts()[i].getProductId();
		}

		SkillsDialogue.sendSkillsDialogue(
				player,
				maxQuantityTen ? SkillsDialogue.MAKE_SETS : SkillsDialogue.MAKE,
				message(data),
				maxQuantityTen ? 10 : 28,
				productIds,
				new ItemNameFilter() {

					int index = -1;

					@Override
					public String rename(String name) {
						index++;
						FletchingProduct product = data.getProducts()[index];

						if (player.getSkills().getLevel(Skills.FLETCHING) < product.getLevel())
							return levelMessage(product, name).replace(" (u)", "");
						else
							return itemMessage(product, data, name).replace(" (u)", "");
					}
				});
	}

	// ---- Messages ----

	public String message(FletchingData data) {
		String name = data.name().toLowerCase();
		int base = data.getBaseId();

		if (name.contains("wolf"))
			return "Choose how many sets of 2 - 6 arrowtips you<br>wish to make, then click on the item to begin.";
		if (base == 2864)
			return "Choose how many sets of 4 arrowshafts you<br>wish to make, then click on the item to begin.";
		if (name.contains("ogre"))
			return "Choose how many sets of 2 - 6 arrows you<br>wish to make, then click on the item to begin.";
		if (name.contains("sagaie"))
			return "Choose how many sets of 5 sagaies you<br>wish to make, then click on the item to begin.";
		if ((base >= 1601 && base <= 1615) || base == 6573)
			return "Choose how many sets of " + (base == 6573 ? "24" : "12") + " bolt tips you<br>wish to make, then click on the item to begin.";
		if (name.contains("bolt"))
			return "Choose how many sets of 10 bolts you<br>wish to make, then click on the item to begin.";
		if (name.contains("arrow"))
			return "Choose how many sets of 15 arrows you<br>wish to make, then click on the item to begin.";
		if (name.contains("dart"))
			return "Choose how many sets of 10 darts you<br>wish to make, then click on the item to begin.";

		return "Choose how many you wish to make<br>then click on the item to begin.";
	}

	public String levelMessage(FletchingProduct product, String itemName) {
		return "<col=ff0000>" + itemName + "<br><col=ff0000>Level " + product.getLevel();
	}

	public String itemMessage(FletchingProduct product, FletchingData data, String itemName) {
		String defs = itemName.toLowerCase();
		int base = data.getBaseId();

		if (base == 2864)
			return itemName + "<br>(Set of 4)";
		if ((data.getProducts()[0].getProductId() == 2864 && !defs.contains("bow")) ||
				data.getProducts()[0].getProductId() == 2861 ||
				data.getProducts()[0].getProductId() == 2866)
			return itemName + "<br>(Set of 2 - 6)";
		if (defs.contains("brutal"))
			return itemName + "<br>(Set of 3)";
		if (defs.contains("bolt tip")) {
			if (base == 6573)
				return itemName + "<br>(Set of 24)";
			return itemName + "<br>(Set of 12)";
		}
		if (defs.contains("stake"))
			return itemName + "<br>(Set of 10)";
		if (defs.contains("bolts"))
			return itemName + "<br>(Set of 10)";
		if (defs.contains("sagaie"))
			return itemName + "<br>(Set of 5)";
		if (defs.contains("arrow"))
			return itemName + "<br>(Set of 15)";
		if (defs.contains("dart"))
			return itemName + "<br>(Set of 10)";

		return itemName;
	}

	// ---- Run ----

	@Override
	public void run(int interfaceId, int componentId) {
		boolean maxQuantityTen = Fletching.maxMakeQuantityTen(new Item(data.getToolId()));

		int option = SkillsDialogue.getItemSlot(componentId);
		if (option >= data.getProducts().length) {
			end();
			return;
		}

		int quantity = maxQuantityTen ? 10 : SkillsDialogue.getQuantity(player);
		int invQuantity = player.getInventory().getNumberOf(data.getBaseId());

		if (!maxQuantityTen && quantity > invQuantity)
			quantity = invQuantity;

		player.getActionManager().setAction(new Fletching(data, option, quantity));
		end();
	}

	@Override
	public void finish() {
	}
}
