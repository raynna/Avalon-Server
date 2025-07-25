package com.rs.java.game.player.dialogues.skilling;

import com.rs.java.game.item.Item;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.fletching.Fletching;
import com.rs.java.game.player.actions.skills.fletching.Fletching.FletchingData;
import com.rs.java.game.player.content.SkillsDialogue;
import com.rs.java.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.java.game.player.dialogues.Dialogue;

public class FletchingD extends Dialogue {

	private FletchingData items;

	@Override
	public void start() {
		items = (FletchingData) parameters[0];
		boolean maxQuantityTen = Fletching.maxMakeQuantityTen(new Item(items.getTool()));
		SkillsDialogue.sendSkillsDialogue(player, maxQuantityTen ? SkillsDialogue.MAKE_SETS : SkillsDialogue.MAKE,
				message(items), maxQuantityTen ? 10 : 28,
				items.getProduct(),

				new ItemNameFilter() {

			int count = -1;
					@Override
					public String rename(String name) {
						count++;
						System.out.println(items.name() + " count: " + count + ", itemReq: " + items.getLevel()[count]);
						if (player.getSkills().getLevel(Skills.FLETCHING) < items.getLevel()[count])
							return levelMessage(items, count, name).replace(" (u)", "");
						else
						 	return itemMessage(items, new Item(items.getId()), name).replace(" (u)", "");
					}
				});
	}

	public int levelReqMessage(FletchingData item) {
		return item.getLevel()[0];
	}
	
	public String message(FletchingData item) {
		String name = item.name().toLowerCase();
		if (name.contains("wolf"))
			return "Choose how many sets of 2 - 6 arrowtips you<br>wish to make, then click on the item to begin.";
		if (item.getId() == 2864)
			return "Choose how many sets of 4 arrowshafts you<br>wish to make, then click on the item to begin.";
		if (name.contains("ogre"))
			return "Choose how many sets of 2 - 6 arrows you<br>wish to make, then click on the item to begin.";
		if (name.contains("sagaie"))
			return "Choose how many sets of 5 sagaies you<br>wish to make, then click on the item to begin.";
		if (item.getId() >= 1601 && item.getId() <= 1615 || item.getId() == 6573)
			return "Choose how many sets of " + (item.getId() == 6573 ? "24" : "12") + " bolt tips you<br>wish to make, then click on the item to begin.";
		if (name.contains("bolt"))
			return "Choose how many sets of 10 bolts you<br>wish to make, then click on the item to begin.";
		if (name.contains("arrow"))
			return "Choose how many sets of 15 arrows you<br>wish to make, then click on the item to begin.";
		if (name.contains("dart"))
			return "Choose how many sets of 10 darts you<br>wish to make, then click on the item to begin.";
		return "Choose how many you wish to make<br>then click on the item to begin.";
	}

	public String levelMessage(FletchingData data, int count, String itemName) {
		return "<col=ff0000>" + itemName + "<br><col=ff0000>Level " + data.getLevel()[count];
	}


	public String itemMessage(FletchingData fletchingData, Item item, String itemName) {
		String defs = itemName.toLowerCase();
		if (item.getId() == 2864)
			return itemName + "<br>(Set of 4)";
		if ((fletchingData.getProduct()[0] == 2864 && !defs.contains("bow")) || fletchingData.getProduct()[0] == 2861 || fletchingData.getProduct()[0] == 2866)
			return itemName + "<br>(Set of 2 - 6)";
		if (defs.contains("brutal"))
			return itemName + "<br>(Set of 3)";
		if (defs.contains("bolt tip")) {
			if (item.getId() == 6573)
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

	@Override
	public void run(int interfaceId, int componentId) {
		boolean maxQuantityTen = Fletching.maxMakeQuantityTen(new Item(items.getTool()));
		int option = SkillsDialogue.getItemSlot(componentId);
		if (option > items.getProduct().length) {
			end();
			return;
		}
		int quantity = maxQuantityTen ? 10 : SkillsDialogue.getQuantity(player);
		int invQuantity = player.getInventory().getItems().getNumberOf(items.getId());
		if (!maxQuantityTen) {
			if (quantity > invQuantity)
				quantity = invQuantity;
		}
		player.getActionManager().setAction(new Fletching(items, option, quantity));
		end();
	}

	@Override
	public void finish() {
	}

}