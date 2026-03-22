package raynna.game.item.plugins;

import raynna.game.item.Item;
import raynna.game.item.ItemId;
import raynna.game.item.ItemPlugin;
import raynna.game.player.Player;

public class ExamplePlugin extends ItemPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] {ItemId.A_KEY_293 };
	}
	@Override
	public boolean processItem(Player player, Item item, int slotId, String option) {
		switch (option) {
			case "activate":
				//do activate
				return true;
			case "drop":
				//do drop
				return true;
		}
		return false;
	}
	@Override
	public boolean processItemOnItem(Player player, Item item, Item item2, int itemUsed, int usedWith) {
		return true;
	}
	@Override
	public boolean processDestroy(Player player, Item item, int slotId) {
		return true;
	}
}