package com.rs.java.game.player.actions.skills.runecrafting;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.runecrafting.Runecrafting.RunecraftingStore;
import com.rs.java.utils.Utils;

public class OuraniaAltar {

	public final static int RUNE_ESSENCE = 1436, PURE_ESSENCE = 7936;

	private static RunecraftingStore[] store = RunecraftingStore.values();

	public static void craftRune(Player player) {
		int runes = player.getInventory().getItems().getNumberOf(PURE_ESSENCE);
		if (runes > 0) {
			player.getInventory().deleteItem(PURE_ESSENCE, runes);
		}
		if (runes == 0) {
			player.getPackets().sendGameMessage("You don't have any pure essence.");
			return;
		}
		int actualLevel = player.getSkills().getLevel(Skills.RUNECRAFTING);
		double totalXp = 5 * runes;
		totalXp *= Runecrafting.getRunecraftingBoost(player);
		player.getSkills().addSkillingXp(Skills.RUNECRAFTING, totalXp, Runecrafting.getRunecraftingBoost(player));
		player.gfx(new Graphics(186));
		player.animate(new Animation(791));
		player.lock(1);
		player.getInventory().deleteItem(PURE_ESSENCE, runes);
		for (int rune = 0; rune < runes; rune++) {
			RunecraftingStore randomRune = store[Utils.getRandom(store.length)];
			while (actualLevel < randomRune.level) {
				randomRune = store[Utils.getRandom(store.length)];
			}
			Item item = new Item(randomRune.runeId, 1);
			player.getInventory().addItem(item);
			RunecraftingOutfit.addPiece(player, randomRune.level);
		}
		player.getPackets().sendGameMessage("You bind the temple's power into runes.");
	}
}