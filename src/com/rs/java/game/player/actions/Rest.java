package com.rs.java.game.player.actions;

import com.rs.java.game.Animation;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

public class Rest extends Action {

	private static int[][] REST_DEFS = { { 5713, 1549, 5748 }, { 11786, 1550, 11788 }, { 5713, 1551, 2921 } // TODO
																											// First
																											// emote

	};

	private int index;

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		index = Utils.random(REST_DEFS.length);
		player.setResting(true);
		player.animate(new Animation(REST_DEFS[index][0]));
		player.getAppearence().setRenderEmote(REST_DEFS[index][1]);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getPoison().isPoisoned()) {
			player.getPackets().sendGameMessage("You can't rest while you're poisoned.");
			return false;
		}
		if (player.getAttackedByDelay() + 10000 > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You can't rest until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(Player player) {
		player.setResting(false);
		player.animate(new Animation(REST_DEFS[index][2]));
		player.getEmotesManager().setNextEmoteEnd();
		player.getAppearence().setRenderEmote(-1);
	}

}
