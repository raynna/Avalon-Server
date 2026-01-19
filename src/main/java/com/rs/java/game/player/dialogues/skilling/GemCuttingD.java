package com.rs.java.game.player.dialogues.skilling;

import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.crafting.gem.GemCutting;
import com.rs.java.game.player.actions.skills.crafting.gem.GemData;
import com.rs.java.game.player.content.SkillsDialogue;
import com.rs.java.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.java.game.player.dialogues.Dialogue;

public class GemCuttingD extends Dialogue {

	private GemData gem;

	@Override
	public void start() {
		gem = (GemData) parameters[0];

		int[] products = { gem.getProduct().getCut() };

		SkillsDialogue.sendSkillsDialogue(
				player,
				SkillsDialogue.CUT,
				"Choose how many you wish to cut,<br>then click on the item to begin.",
				28,
				products,
				new ItemNameFilter() {

					@Override
					public String rename(String name) {
						if (player.getSkills().getLevel(Skills.CRAFTING) < gem.getProduct().getLevel())
							return "<col=ff0000>" + name + "<br><col=ff0000>Level " + gem.getProduct().getLevel();
						return name;
					}
				});
	}

	@Override
	public void run(int interfaceId, int componentId) {

		if (player.getSkills().getLevel(Skills.CRAFTING) < gem.getProduct().getLevel()) {
			player.message("You need a Crafting level of " + gem.getProduct().getLevel() + ".");
			end();
			return;
		}

		int quantity = SkillsDialogue.getQuantity(player);

		player.getActionManager().setAction(
				new GemCutting(gem.getProduct(), quantity)
		);
		end();
	}

	@Override
	public void finish() {
	}
}
