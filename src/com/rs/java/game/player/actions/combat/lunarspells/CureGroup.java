package com.rs.java.game.player.actions.combat.lunarspells;

import java.util.concurrent.TimeUnit;

import com.rs.core.thread.CoresManager;
import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;

public class CureGroup {
	
	public static boolean cast(Player player, double xp) {
		Long lastCureGroup = (Long) player.temporaryAttribute().get("LAST_CUREGROUP");
		if (lastCureGroup != null && lastCureGroup + 20000 > Utils.currentTimeMillis()) {
			player.message("You can only cast this every 20 seconds.");
			return false;
		}
		if (!player.isAtMultiArea()) {
			player.message("You need to be in a mutli area for this spell.");
			return false;
		}
		int playersAround = 0;
		for (Player other : World.getPlayers()) {
			if (other.withinDistance(player, 4) && other.isAcceptAid() && other.isAtMultiArea()) {
				if (!other.getUsername().equalsIgnoreCase(player.getUsername())) {
					other.message("You were cured.");
					other.gfx(new Graphics(745, 0, 100));
					other.temporaryAttribute().put("LAST_CUREGROUP", Utils.currentTimeMillis());
					playersAround++;
					other.getPoison().reset();
				}
			}
			player.message("The spell affected " + playersAround + " player(s).");
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					player.getPoison().reset();
					player.message("You were cured.");
				}
			}, 1200, TimeUnit.MILLISECONDS);
		}
		player.getSkills().addXp(Skills.MAGIC, xp);
		player.gfx(new Graphics(744, 0, 100));
		player.animate(new Animation(4409));
		player.temporaryAttribute().put("LAST_CUREGROUP", Utils.currentTimeMillis());
		return true;
	}
}
