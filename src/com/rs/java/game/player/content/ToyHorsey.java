package com.rs.java.game.player.content;

import com.rs.java.game.Animation;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

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