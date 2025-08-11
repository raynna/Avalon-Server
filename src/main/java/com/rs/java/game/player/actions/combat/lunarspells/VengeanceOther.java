package com.rs.java.game.player.actions.combat.lunarspells;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;

public class VengeanceOther {
	
	public static boolean cast(Player player, Entity target, double xp) {
		if (target instanceof Player) {
			Player other = (Player) target;
			if (player.getVengDelay() >= Utils.currentTimeMillis()) {
				player.message("You can only cast vengeance every 30 seconds.");
				return false;
			}
			if (other.getVengDelay() >= Utils.currentTimeMillis()) {
				player.message(other.getDisplayName() + " can only cast vengeance every 30 seconds.");
				return false;
			}
			if (!other.isAcceptAid()) {
				player.message(other.getDisplayName() + " doesn't have aid on.");
				return false;
			}
			if (!other.isAtMultiArea()) {
				player.message("You can only cast this spell in a multi-area.");
				return false;
			}
			player.animate(new Animation(4411));
			other.gfx(new Graphics(725, 0, 100));
			other.message(player.getDisplayName() + " cast an vengeance spell on you.");
			other.setVengeance(true);
			other.setVengeance(30000);
			player.setVengeance(30000);
			player.addXp(Skills.MAGIC, xp);
			return true;
		}
		return false;
	}

}
