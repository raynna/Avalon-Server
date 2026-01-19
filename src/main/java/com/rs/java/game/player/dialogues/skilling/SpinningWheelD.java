package com.rs.java.game.player.dialogues.skilling;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.WorldObject;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.crafting.spinningwheel.SpinningWheel;
import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;
import com.rs.java.game.player.actions.skills.crafting.spinningwheel.SpinningData;
import com.rs.java.game.player.actions.skills.crafting.spinningwheel.SpinningProduct;
import com.rs.java.game.player.content.SkillsDialogue;
import com.rs.java.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.java.game.player.dialogues.Dialogue;
import com.rs.java.utils.HexColours;
import com.rs.java.utils.Utils;

public class SpinningWheelD extends Dialogue {

	private WorldObject object;
	private SpinningData[] data = SpinningData.values();

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

						SpinningProduct prod = data[index].getProduct();

						if (player.getSkills().getLevel(Skills.CRAFTING) < prod.getLevel())
							return "<col=ff0000>" + name + "<br><col=ff0000>Level " + prod.getLevel();

						if (!hasRequiredItems(prod)) {
							ReqItem r = prod.getRequirements()[0];
							String reqName = ItemDefinitions.getItemDefinitions(r.getId()).getName();
							return name + "<br>" + HexColours.Colour.RED.getHex() +
									"(" + (r.getAmount() > 1 ? r.getAmount() + " " + reqName : reqName) + ")";
						}

						return itemMessage(prod, name);
					}
				});
	}

	private boolean hasRequiredItems(SpinningProduct product) {
		for (ReqItem r : product.getRequirements()) {
			if (!player.getInventory().containsItem(r.getId(), r.getAmount()))
				return false;
		}
		return true;
	}

	private String itemMessage(SpinningProduct product, String itemName) {
		ReqItem r = product.getRequirements()[0];
		String itemReq = ItemDefinitions.getItemDefinitions(r.getId()).getName().toLowerCase();

		return itemName + "<br>(" +
				(r.getAmount() > 1 ? r.getAmount() + " " + itemReq : "") +
				Utils.fixChatMessage(itemReq) + ")";
	}

	@Override
	public void run(int interfaceId, int componentId) {

		int option = SkillsDialogue.getItemSlot(componentId);

		if (option >= data.length) {
			end();
			return;
		}

		player.getActionManager().setAction(
				new SpinningWheel(option, object, SkillsDialogue.getQuantity(player))
		);
		end();
	}

	@Override
	public void finish() {
	}
}
