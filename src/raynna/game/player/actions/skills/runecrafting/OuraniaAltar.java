package raynna.game.player.actions.skills.runecrafting;

import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.item.Item;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.game.player.actions.skills.runecrafting.Runecrafting.RunecraftingStore;
import raynna.util.Utils;

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
		player.getSkills().addXp(Skills.RUNECRAFTING, totalXp);
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