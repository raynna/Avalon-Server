package raynna.game.player.combat.magic.modern.spells;

import raynna.game.Animation;
import raynna.game.player.Player;
import raynna.game.player.Skills;

public class BonesToService {
	
	public static boolean cast(Player player, boolean peaches) {
			if (!player.getInventory().containsItem(526, 1)) {
				player.getPackets().sendGameMessage("You don't have any bones.");
				return false;
			}
			int amount = player.getInventory().getNumberOf(526);
			player.getInventory().deleteItem(526, amount);
			player.getInventory().addItem(peaches ? 6883 : 1963, amount);
			player.animate(new Animation(712));
			return true;
	}

}
