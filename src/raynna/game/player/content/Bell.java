package raynna.game.player.content;

import raynna.game.Animation;
import raynna.game.player.Player;

public class Bell {

	public static void play(Player player) {
		player.animate(new Animation(6083));
	}

}
