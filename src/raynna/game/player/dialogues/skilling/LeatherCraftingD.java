package raynna.game.player.dialogues.skilling;

import raynna.game.player.Skills;
import raynna.game.player.actions.skills.crafting.leather.LeatherCrafting;
import raynna.game.player.actions.skills.crafting.leather.LeatherData;
import raynna.game.player.actions.skills.crafting.leather.LeatherProduct;
import raynna.game.player.content.SkillsDialogue;
import raynna.game.player.content.SkillsDialogue.ItemNameFilter;
import raynna.game.player.dialogues.Dialogue;

public class LeatherCraftingD extends Dialogue {

	private LeatherData data;
	private LeatherProduct[] products;

	@Override
	public void start() {
		data = (LeatherData) parameters[0];
		products = data.getProducts();

		int[] productIds = new int[products.length];
		for (int i = 0; i < products.length; i++)
			productIds[i] = products[i].getId();

		SkillsDialogue.sendSkillsDialogue(
				player,
				SkillsDialogue.MAKE,
				"Choose how many you wish to make,<br>then click on the item to begin.",
				28,
				productIds,
				new ItemNameFilter() {

					int index = -1;

					@Override
					public String rename(String name) {
						index++;

						LeatherProduct product = products[index];

						if (player.getSkills().getLevel(Skills.CRAFTING) < product.getLevel())
							return "<col=ff0000>" + name + "<br><col=ff0000>Level " + product.getLevel();

						return name;
					}
				});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int option = SkillsDialogue.getItemSlot(componentId);

		if (option >= products.length) {
			end();
			return;
		}

		int quantity = SkillsDialogue.getQuantity(player);

		player.getActionManager().setAction(
				new LeatherCrafting(data, option, quantity)
		);
		end();
	}



	@Override
	public void finish() {
	}
}
