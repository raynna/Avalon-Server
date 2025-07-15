package com.rs.java.game.player.actions.combat.lunarspells;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;

public class DisruptionShield {
	
	public static boolean cast(Player player, double xp) {
		if (player.getDisruptionDelay() >= Utils.currentTimeMillis()) {
			player.message("You can't cast this spell again yet.");
			return false;
		}
		player.addXp(Skills.MAGIC, xp);
		player.gfx(new Graphics(1320, 0, 100));
		player.animate(new Animation(8770));
		player.setDisruption(true);
		player.setDisruption(96);
		player.message("You cast a Disruption Shield.");
		return true;
	}
}
