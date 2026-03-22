package raynna.game.player.dialogues.skilling;

import raynna.core.cache.defintions.ItemDefinitions;
import raynna.game.item.Item;
import raynna.game.player.actions.skills.crafting.MilestoneCapes;
import raynna.game.player.content.SkillsDialogue;
import raynna.game.player.content.SkillsDialogue.ItemNameFilter;
import raynna.game.player.dialogues.Dialogue;

public class MilestoneD extends Dialogue {

	@Override
	public void start() {
		int[] ids = new int[MilestoneCapes.MilestoneProducts.getEntries().size()];
		for (int i = 0; i < ids.length; i++)
			ids[i] = MilestoneCapes.MilestoneProducts.getEntries().get(i).getProducedItem().getId();
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
				"Choose how many you wish to make,<br>then click on the item to begin.", 28, ids, new ItemNameFilter() {
					int count = 0;

					@Override
					public String rename(String name) {
						MilestoneCapes.MilestoneProducts prod = MilestoneCapes.MilestoneProducts.getEntries().get(count++);
						return itemMessage(prod, new Item(prod.getProducedItem()), name);

					}
				});
	}

	public String itemMessage(MilestoneCapes.MilestoneProducts products, Item item, String itemName) {
		String required = ItemDefinitions.getItemDefinitions(products.getItemsRequired().getId()).getName()
				.toLowerCase();
		return itemName + "<br>(" + products.getItemsRequired().getAmount() + " " + required + ")";

	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new MilestoneCapes(SkillsDialogue.getItemSlot(componentId), SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {
	}
}
