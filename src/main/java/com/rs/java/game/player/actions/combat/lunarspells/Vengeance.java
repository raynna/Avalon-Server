package com.rs.java.game.player.actions.combat.lunarspells;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.TickManager;

public class Vengeance {
	
	public static boolean cast(Player player, double xp) {
		if (player.getTickManager().isActive(TickManager.TickKeys.VENGEANCE_COOLDOWN)) {
			player.message("You can only cast vengeance every 30 seconds.");
			return false;
		}
		player.addXp(Skills.MAGIC, xp);
		player.gfx(new Graphics(726, 0, 100));
		player.animate(new Animation(4410));
		player.setVengeance(true);
		player.getTickManager().addSeconds(TickManager.TickKeys.VENGEANCE_COOLDOWN, 30, () -> {
			player.message("You can now cast vengeance again.");
		});
		player.message("You cast a vengeance.");
		return true;
	}

}
