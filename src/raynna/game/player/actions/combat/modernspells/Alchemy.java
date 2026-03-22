package raynna.game.player.actions.combat.modernspells;

import raynna.core.cache.defintions.ItemDefinitions;
import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.item.Item;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.game.player.content.ItemConstants;

public class Alchemy {

	public static boolean castSpell(Player player, int itemId, int slotId, boolean fireStaff, boolean lowAlch) {

		if (player.isLocked() || player.hasSpellDelay())
			return false;

		if (!player.getInventory().containsItem(itemId, 1))
			return false;
		Item item = player.getInventory().getItem(slotId);
		if (!ItemConstants.isTradeable(item)) {
			player.getPackets().sendGameMessage("You cannot cast an alchemy spell on untradeables.");
			return false;
		}

		if (itemId == 995) {
			player.getPackets().sendGameMessage("You cannot cast an alchemy spell on coins.");
			return false;
		}

		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);

		if (defs.isNoted())
			defs = ItemDefinitions.getItemDefinitions(defs.getCertId());

		player.animate(fireStaff ? 9633 : 713);
		player.gfx(new Graphics(fireStaff ? 1693 : 113));

		player.castSpellDelay(3);

		player.getInventory().deleteItem(slotId, new Item(itemId));

		int coins = lowAlch ? defs.getLowAlchPrice() : defs.getHighAlchPrice();
		player.getMoneyPouch().addMoney(coins, false);

		player.getPackets().sendGlobalVar(168, 7);

		player.getSkills().addXp(Skills.MAGIC, lowAlch ? 31 : 65);

		player.lock(1);

		return true;
	}

}
