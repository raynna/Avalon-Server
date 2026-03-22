package raynna.game.player.actions.skills.hunter;

import raynna.app.Settings;
import raynna.core.cache.defintions.ItemDefinitions;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.item.Item;
import raynna.game.item.ground.GroundItems;
import raynna.game.npc.Drop;
import raynna.game.player.Player;
import raynna.util.Utils;

public class HunterImplings {

	public static void sendDrop(Player player, Drop drop) {
		Item item = ItemDefinitions.getItemDefinitions(drop.getItemId()).isStackable()
				? new Item(drop.getItemId(),
						(drop.getMinAmount() * Settings.DROP_RATE)
								+ Utils.getRandom(drop.getExtraAmount() * Settings.DROP_RATE))
				: new Item(drop.getItemId(), (drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount())));
		GroundItems.updateGroundItem(new Item(item.getId(), item.getAmount()),
				new WorldTile(player.getX() + 1, player.getY(), player.getPlane()), player);
	}
}