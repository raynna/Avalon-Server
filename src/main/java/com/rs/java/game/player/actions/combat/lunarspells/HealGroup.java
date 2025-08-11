package com.rs.java.game.player.actions.combat.lunarspells;

import java.util.concurrent.TimeUnit;

import com.rs.core.thread.CoresManager;
import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.World;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.utils.Utils;

public class HealGroup {

	public static boolean cast(Player player, double xp) {
		Long lastHealGroup = (Long) player.temporaryAttribute().get("LAST_HEALGROUP");
		if (lastHealGroup != null && lastHealGroup + 20000 > Utils.currentTimeMillis()) {
			player.message("You can only cast this every 20 seconds.");
			return false;
		}
		if (!player.isAtMultiArea()) {
			player.message("You need to be in a mutli area for this spell.");
			return false;
		}
		int playersAround = 0;
		for (Player other : World.getPlayers()) {
			if (other == null)
				continue;
			if (other.withinDistance(player, 4) && other.isAcceptAid()) {
				if (!other.getUsername().equalsIgnoreCase(player.getUsername())) {
					other.message("Your health has been healed.");
					other.gfx(new Graphics(745, 0, 100));
					other.temporaryAttribute().put("LAST_HEALGROUP", Utils.currentTimeMillis());
					playersAround++;
					other.heal((int) (player.getHitpoints() * .75 / playersAround));
				}
			}
			player.message("The spell affected " + playersAround + " player(s).");
			CoresManager.getSlowExecutor().schedule(() -> {
                player.applyHit(new Hit(player, ((int) (player.getHitpoints() * .75)), HitLook.REGULAR_DAMAGE));
                player.animate(new Animation(Combat.getDefenceEmote(player)));
            }, 1200, TimeUnit.MILLISECONDS);
		}
		player.getSkills().addXp(Skills.MAGIC, xp);
		player.gfx(new Graphics(745, 0, 100));
		player.animate(new Animation(4411));
		player.temporaryAttribute().put("LAST_HEALGROUP", Utils.currentTimeMillis());
		return true;
	}
}
