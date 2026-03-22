package raynna.game.player.content;

import raynna.game.Animation;
import raynna.game.ForceTalk;
import raynna.game.item.Item;
import raynna.game.player.Player;
import raynna.util.Utils;

/*
 * Tristam
 * - Rain
 * - 25th March 2016
 */

public class ToyHorsey {

	private static final String[] speech = { "Come-on Dobbin, we can win the race!", "Hi-ho Silver, and away!",
			"Neaahhyyy!", "Giddy-up horsey!", "Watch me whip, watch me neigh neigh" };

	public enum Horses {

		BROWN_HORSE(2520, 918), WHITE_HORSE(2522, 919), BLACK_HORSE(2524, 920), GREY_HORSE(2526, 921);

		private final int itemId, animation;

		Horses(final int itemId, final int animation) {
			this.itemId = itemId;
			this.animation = animation;
		}

		public final int getHorseId() {
			return itemId;
		}

		public final int getAnimation() {
			return animation;
		}

	}

	public static String generateSpeech() {
		return speech[Utils.random(0, speech.length)];
	}

	public static boolean play(Player player, Item item) {
		for (Horses horses : Horses.values()) {
			if (item.getId() == horses.getHorseId()) {
				player.setNextForceTalk(new ForceTalk(generateSpeech()));
				player.animate(new Animation(horses.getAnimation()));
				return true;
			}
		}
		return false;
	}

}