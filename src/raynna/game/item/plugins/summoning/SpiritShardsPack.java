package raynna.game.item.plugins.summoning;

import raynna.game.item.Item;
import raynna.game.item.ItemPlugin;
import raynna.game.player.Player;

public class SpiritShardsPack extends ItemPlugin {

	int SHARDS_PER_PACK = 5000;

	@Override
	public Object[] getKeys() {
		return new Object[] { "item.spirit_shard_pack" };
	}
	@Override
	public boolean processItem(Player player, Item item, int slotId, String option) {
		switch (option) {
			case "open":
				if (player.getInventory().getNumberOf(item.getId()) > 1 && !player.getInventory().containsOneItemFlexible("item.spirit_shards") && !player.getInventory().hasFreeSlots()) {
					player.message("You don't have enough inventory space to open this pack.");
					return true;
				}
				if (player.getInventory().getNumberOf("item.spirit_shard_pack") + SHARDS_PER_PACK < 0) {
					player.getPackets().sendGameMessage("You don't have enough inventory space to open this pack.");
					return true;
				}
				player.getInventory().deleteItem(item.getId(), 1);
				player.getInventory().addItem("item.spirit_shards", SHARDS_PER_PACK);
				return true;
			case "open-all":
				int packs = player.getInventory().getAmountOf("item.spirit_shard_pack");
				int amount = packs * SHARDS_PER_PACK;
				int shards = player.getInventory().getAmountOf("item.spirit_shards");
				if (shards + SHARDS_PER_PACK == Integer.MAX_VALUE || (!player.getInventory().containsOneItemFlexible("item.spirit_shards") && !player.getInventory().hasFreeSlots() && packs > 1)) {
					player.getPackets().sendGameMessage("You don't have enough inventory space to open any packs.");
					return true;
				}
				if (shards + amount < 0) {
					packs = (Integer.MAX_VALUE - shards) / SHARDS_PER_PACK;
					amount = packs * SHARDS_PER_PACK;
					player.getPackets().sendGameMessage("You don't have enough inventory space to open all packs.");
				}
				player.getInventory().deleteItem("item.spirit_shard_pack", packs);
				player.getInventory().addItem("item.spirit_shards", amount);
				return true;
		}
		return false;
	}
}