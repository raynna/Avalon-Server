package com.rs.java.game.player.dialogues.skilling;

import com.rs.java.game.WorldObject;
import com.rs.java.game.player.actions.skills.cooking.Cooking;
import com.rs.java.game.player.actions.skills.cooking.Cooking.Cookables;
import com.rs.java.game.player.content.SkillsDialogue;
import com.rs.java.game.player.dialogues.Dialogue;

public class CookingD extends Dialogue {

	private Cookables cooking;
	private WorldObject object;

	@Override
	public void start() {
		this.cooking = (Cookables) parameters[0];
		this.object = (WorldObject) parameters[1];

		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.COOK,
				"Choose how many you wish to cook,<br>then click on the item to begin.",
				player.getInventory().getItems().getNumberOf(cooking.getRawItem()),
				new int[] { cooking.getProduct().getId() }, null);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager()
				.setAction(new Cooking(object, cooking.getRawItem(), SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {

	}

}
