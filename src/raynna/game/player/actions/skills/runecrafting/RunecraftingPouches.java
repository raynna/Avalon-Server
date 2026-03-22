package raynna.game.player.actions.skills.runecrafting;

import raynna.game.item.Item;
import raynna.game.item.meta.RuneEssencePouchMetaData;
import raynna.game.player.Player;
import raynna.game.player.Skills;

public class RunecraftingPouches extends Runecrafting {

	private static final int PURE_ESSENCE = 7936;

	public static void fillPouch(Player player, Item pouch) {

		RuneEssencePouchType type = RuneEssencePouchType.forItem(pouch.getId());
		if (type == null)
			return;

		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < type.getLevelReq()) {
			player.getPackets().sendGameMessage(
					"You need a runecrafting level of " + type.getLevelReq() + " to fill this pouch."
			);
			return;
		}

		RuneEssencePouchMetaData meta = (RuneEssencePouchMetaData) pouch.getMetadata();

		if (meta == null) {
			meta = new RuneEssencePouchMetaData(type.getCapacity());
			pouch.setMetadata(meta);
		}

		int space = type.getCapacity() - meta.getEssenceAmount();
		if (space <= 0) {
			player.getPackets().sendGameMessage("Your pouch is full.");
			return;
		}

		int inventoryEss = player.getInventory().getItems().getNumberOf(PURE_ESSENCE);
		if (inventoryEss <= 0) {
			player.getPackets().sendGameMessage("You don't have any essence with you.");
			return;
		}

		int toAdd = Math.min(space, inventoryEss);

		player.getInventory().deleteItem(PURE_ESSENCE, toAdd);
		meta.addEssence(toAdd);
	}

	public static void emptyPouch(Player player, Item pouch) {

		RuneEssencePouchType type = RuneEssencePouchType.forItem(pouch.getId());
		if (type == null)
			return;

		RuneEssencePouchMetaData meta = (RuneEssencePouchMetaData) pouch.getMetadata();
		if (meta == null || meta.getEssenceAmount() == 0) {
			player.getPackets().sendGameMessage("Your pouch has no essence left in it.");
			return;
		}

		int freeSlots = player.getInventory().getFreeSlots();
		if (freeSlots == 0) {
			player.getPackets().sendGameMessage("You don't have enough inventory space.");
			return;
		}

		int toRemove = Math.min(meta.getEssenceAmount(), freeSlots);

		player.getInventory().addItem(PURE_ESSENCE, toRemove);
		meta.removeEssence(toRemove);
	}

	public static void checkPouch(Player player, Item pouch) {

		RuneEssencePouchMetaData meta = (RuneEssencePouchMetaData) pouch.getMetadata();

		int amount = meta == null ? 0 : meta.getEssenceAmount();
		int maxAmount = meta == null ? 0 : meta.getMaxValue();

		player.getPackets().sendGameMessage(
				"This pouch has " + amount + "/"+ maxAmount +" essence in it."
		);
	}
}
