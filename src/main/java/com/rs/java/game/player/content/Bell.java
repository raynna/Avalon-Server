package com.rs.java.game.player.content;

import com.rs.java.game.Animation;
import com.rs.java.game.player.Player;

public class Bell {

	public static void play(Player player) {
		player.animate(new Animation(6083));
	}

}
