package com.rs.java.game.player.dialogues.skilling;

import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.crafting.LeatherCrafting;
import com.rs.java.game.player.actions.skills.crafting.LeatherData;
import com.rs.java.game.player.content.SkillsDialogue;
import com.rs.java.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.java.game.player.dialogues.Dialogue;

public class LeatherCraftingD extends Dialogue {

	private LeatherData data;

	@Override
	public void start() {
		data = (LeatherData) parameters[0];

		SkillsDialogue.sendSkillsDialogue(
				player,
				SkillsDialogue.MAKE,
				"Choose how many you wish to make,<br>then click on the item to begin.",
				28,
				data.getProducts(),
				new ItemNameFilter() {

					int count = -1;

					@Override
					public String rename(String name) {
						count++;
						if (player.getSkills().getLevel(Skills.CRAFTING) < data.getLevels()[count])
							return "<col=ff0000>" + name + "<br><col=ff0000>Level " + data.getLevels()[count];
						return name;
					}
				});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int option = SkillsDialogue.getItemSlot(componentId);
		if (option >= data.getProducts().length) {
			end();
			return;
		}

		int quantity = SkillsDialogue.getQuantity(player);
		int invAmount = player.getInventory().getAmountOf(data.getBaseLeather());

		if (quantity > invAmount)
			quantity = invAmount;

		player.getActionManager().setAction(new LeatherCrafting(data, option, quantity));
		end();
	}

	@Override
	public void finish() {
	}
}
