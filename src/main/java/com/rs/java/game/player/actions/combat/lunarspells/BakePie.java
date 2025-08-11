package com.rs.java.game.player.actions.combat.lunarspells;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

/**
 * @Andreas
 */

public class BakePie {

	public static enum Pies {

		UNBAKED_MEAT_PIE(2319, 2327, 110.0, 20),
		
		UNBAKED_BERRY_PIE(2321, 2325, 72.0, 10);

		private int baseId;
		private int newId;
		private double xp;
		private int level;

		private Pies(int baseId, int newId, double xp, int level) {
			this.baseId = baseId;
			this.newId = newId;
			this.xp = xp;
			this.level = level;
		}

		public int getBaseId() {
			return baseId;
		}

		public int getNewId() {
			return newId;
		}

		public double getXp() {
			return xp;
		}

		public int getLevel() {
			return level;
		}
	}
	
	public static boolean cast(Player player, double xp) {
		boolean hasPie = false;
		for (Pies pies : Pies.values()) {
			if (pies == null)
				continue;
			if (player.getInventory().containsItem(pies.getBaseId(), 1)) {
				hasPie = true;
				break;
			}
		}
		for (Pies pies : Pies.values()) {
			if (pies == null)
				continue;
			if (!hasPie) {
				player.message("You don't have any pies to bake.");
				return false;
			}
			if (hasPie && !player.getInventory().containsItem(pies.getBaseId(), 1))
				continue;
			if (player.getSkills().getLevel(Skills.COOKING) < pies.getLevel()) {
				player.message("You need a cooking level of " + pies.getLevel() + " to cook this pie.");
				continue;
			}
			player.lock(4);
			player.animate(new Animation(4413));
			player.gfx(new Graphics(746, 0, 96 << 16));
			player.addXp(Skills.MAGIC, xp);
			player.addXp(Skills.COOKING, pies.getXp());
			player.removeItem(pies.getBaseId(), 1);
			player.addItem(pies.getNewId(), 1);
			player.message("Your spell bakes the " + ItemDefinitions.getItemDefinitions(pies.getBaseId()).getName() + ".");
			return true;
		}
		return false;
	}
}
