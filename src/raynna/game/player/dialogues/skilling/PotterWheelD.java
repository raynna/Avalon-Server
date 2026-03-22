package raynna.game.player.dialogues.skilling;

import raynna.core.cache.defintions.ItemDefinitions;
import raynna.game.WorldObject;
import raynna.game.item.Item;
import raynna.game.player.Skills;
import raynna.game.player.actions.skills.crafting.PotterWheel;
import raynna.game.player.content.SkillsDialogue;
import raynna.game.player.content.SkillsDialogue.ItemNameFilter;
import raynna.game.player.dialogues.Dialogue;
import raynna.util.Utils;

public class PotterWheelD extends Dialogue {

	private WorldObject object;

	@Override
	public void start() {
		object = (WorldObject) parameters[0];
		int[] ids = new int[PotterWheel.Products.getEntries().size()];
		for (int i = 0; i < ids.length; i++)
			ids[i] = PotterWheel.Products.getEntries().get(i).getProducedItem().getId();
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
				"Choose how many you wish to make,<br>then click on the item to begin.", 28, ids,
				new ItemNameFilter() {
					int count = 0;

					@Override
					public String rename(String name) {
						PotterWheel.Products prod = PotterWheel.Products.getEntries().get(count++);
						if (player.getSkills().getLevel(Skills.CRAFTING) < prod.getLevelRequired())
							name = "<col=ff0000>" + name + "<br><col=ff0000>Level " + prod.getLevelRequired();
						else
							name = itemMessage(prod, new Item(prod.getProducedItem()), name);
						return name;

					}
				});
	}

	public String itemMessage(PotterWheel.Products products, Item item, String itemName) {
		String itemReq = ItemDefinitions.getItemDefinitions(products.getItemsRequired().getId()).getName()
				.toLowerCase();
		return itemName + "<br>("
				+ (products.getItemsRequired().getAmount() > 1 ? products.getItemsRequired().getAmount() + " " + itemReq : "")
				+ Utils.fixChatMessage(itemReq) + ")";
	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new PotterWheel(SkillsDialogue.getItemSlot(componentId), object, SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {
	}
}
