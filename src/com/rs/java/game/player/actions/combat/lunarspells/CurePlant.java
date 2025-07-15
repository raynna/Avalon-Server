package com.rs.java.game.player.actions.combat.lunarspells;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldObject;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

/**
 * @author -Andreas 20 feb. 2020 10:32:53
 * @project source
 * 
 */

public class CurePlant {

	public static boolean cast(Player player, double xp, WorldObject object) {
		player.animate(new Animation(4432));
		player.gfx(new Graphics(748));
		player.getSkills().addXp(Skills.MAGIC, xp);
		player.getVarsManager().forceSendVarBit(object.getConfigByFile(), 4);
		return true;
	}

}
