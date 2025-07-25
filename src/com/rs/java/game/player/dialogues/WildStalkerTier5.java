package com.rs.java.game.player.dialogues;

import com.rs.java.game.player.Player;

public class WildStalkerTier5 extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose which hat to change to:", "Tier 1", "Tier 2", "Tier 3", "Tier 4", "Tier 6");
	}

	private void removeWildStalkers(Player player) {
		for (int itemId = 20801; itemId < 20806; itemId++) {
			player.getInventory().deleteItem(itemId, 1);
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				end();
				removeWildStalkers(player);
				player.getInventory().addItem(20801, 1);
			} else if (componentId == OPTION_2) {
				end();
				removeWildStalkers(player);
				player.getInventory().addItem(20802, 1);
			} else if (componentId == OPTION_3) {
				end();
				removeWildStalkers(player);
				player.getInventory().addItem(20803, 1);
			} else if (componentId == OPTION_4) {
				end();
				removeWildStalkers(player);
				player.getInventory().addItem(20804, 1);
			} else if (componentId == OPTION_5) {
				end();
				if (player.getKillCount() < 5000) {
					player.getPackets().sendGameMessage(
							"You can't change the look of your wildstalker helmet until you earned additional tiers.");
					player.getPackets().sendGameMessage("You need at least 5000 wilderness kills.");
					return;
				}
				removeWildStalkers(player);
				player.getInventory().addItem(20806, 1);
			}
		}

	}

	@Override
	public void finish() {

	}
}