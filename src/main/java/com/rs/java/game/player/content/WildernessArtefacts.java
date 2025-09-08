package com.rs.java.game.player.content;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

/**
 *
 * @Author Tristam <Hassan>
 * @Project - Rain
 * @Improved - 11 Mar 2016
 *
 **/

public class WildernessArtefacts {

	public enum Artefacts {

		ANCIENTSTATTUETE(5000000, 14876, 2), 
		
		SERENSTATTUETE(1000000, 14877, 4), 
		
		ARMADYLSTATTUETE(750000, 14878, 5), 
		
		ZAMORAKSTATTUETE(500000, 14879, 6), 
		
		SARADOMINSTATTUETE(400000, 14880, 7), 
		
		BANDOSSTATTUETE(300000, 14881, 8), 
		
		RUBYCHALICE(250000, 14882, 10), 
		
		GUTHIXIANBRAZIER(200000, 14883, 15), 
		
		ARMADYLTOTEM(150000, 14884, 20), 
		
		ZAMORAKMEDALLION(100000, 14885, 25), 
		
		SARADOMINCARVING(75000, 14886, 30), 
		
		BANDOSSCRIMSHAW(50000, 14887, 35), 
		
		SARADOMINAMPHORA(40000, 14888, 40), 
		
		ANCIENTPSALTARYBRIDGE(30000, 14889, 50), 
		
		BRONZEDDRAGONCLAW(20000, 14890, 55), 
		
		THIRDAGECARAFE(10000, 14891, 55), 
		
		BORKENSTATUEHEADRESS(5000, 14892, 60);

		private int price, id; 
		private double chance;

		private Artefacts(int price, int id, double chance) {
			this.price = price;
			this.id = id;
			this.chance = chance;
		}

		public int getPrice() {
			return price;
		}

		public int getId() {
			return id;
		}

		public double getChance() {
			return chance;
		}

		public String getName() {
			return ItemDefinitions.getItemDefinitions(getId()).getName();
		}

	}

	public static boolean useOnMandrith(Player player) {
		int totalCoins = 0;
		StringBuilder soldItems = new StringBuilder();

		for (Artefacts artefact : Artefacts.values()) {
			int amount = player.getInventory().getNumberOf(artefact.getId());
			if (amount > 0) {
				int value = artefact.getPrice() * amount;
				if (totalCoins + value < 0)
					break;
				totalCoins += value;

				player.getInventory().deleteItem(artefact.getId(), amount);

				if (soldItems.length() > 0) {
					soldItems.append(", ");
				}
				soldItems.append(amount).append(" ").append(artefact.getName());
			}
		}

		if (totalCoins > 0) {
			player.getMoneyPouch().addMoney(totalCoins, false);
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You sold " + soldItems + " for a total of " +
							Utils.getFormattedNumber(totalCoins, ',') + " Coins."
			);
			return true;
		}
		return false;
	}

	public static boolean trade(Player player) {
		int totalCoins = 0;
		StringBuilder soldItems = new StringBuilder();

		for (Artefacts artefact : Artefacts.values()) {
			int amount = player.getInventory().getNumberOf(artefact.getId());
			if (amount > 0) {
				int value = artefact.getPrice() * amount;
				if (totalCoins + value < 0)
					break;
				totalCoins += value;

				player.getInventory().deleteItem(artefact.getId(), amount);

				if (soldItems.length() > 0) {
					soldItems.append(", ");
				}
				soldItems.append(amount).append(" ").append(artefact.getName());
			}
		}

		if (totalCoins > 0) {
			player.getMoneyPouch().addMoney(totalCoins, false);
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You sold " + soldItems + " for a total of " +
							Utils.getFormattedNumber(totalCoins, ',') + " Coins."
			);
			return true;
		}

		return false; // no artefacts
	}


}
