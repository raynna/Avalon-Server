package com.rs.java.game.player.npcdrops;

import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

/**
 * 
 * @author Andreas, Dennis - AvalonPK
 * 
 */

public class CasketDrops {

	public enum Casket {

		CASKET(3600, 100);

		private int itemId;

		private double chance;

		private Casket(int itemId, double chance) {
			this.itemId = itemId;
			this.chance = chance;
		}

		public int getItemId() {
			return itemId;
		}

		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

		public double getChance() {
			return chance;
		}

		public void setChance(double chance) {
			this.chance = chance;
		}

	}

	public static void sendDrop(Player player, NPC npc) {
		Casket drop = calculateDrop(player, npc);
		int size = npc.getSize();
		if (drop == null)
			return;
		player.getPackets().sendGameMessage("You recieve a casket loot!");
		World.addGroundItem(new Item(drop.getItemId(), 1),
				new WorldTile(npc.getCoordFaceX(size), npc.getCoordFaceY(size), npc.getPlane()), player, true, 60, 0);
	}

	public static Casket calculateDrop(Player player, NPC npc) {
		final double DROP_CHANCE = ((0.125 * (npc.getCombatLevel() / 5)) * (player.isMember() ? 1.2 : 1))
				* (player.isAtWild() ? 1.2 : 1);
		if (Utils.getRandom(100) > DROP_CHANCE)
			return null;
		if (npc.getCombatLevel() < 10)
			return null;
		while (true) {
			double chance = Utils.getRandomDouble(100);
			Casket items = Casket.values()[Utils.getRandom(Casket.values().length - 1)];
			if ((items.getChance()) > chance) {
				return items;
			} else
				continue;
		}
	}

}
