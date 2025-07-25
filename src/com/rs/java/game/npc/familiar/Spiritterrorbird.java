package com.rs.java.game.npc.familiar;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.summoning.Summoning.Pouch;

public class Spiritterrorbird extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5259052583696765531L;

	public Spiritterrorbird(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Tireless Run";
	}

	@Override
	public String getSpecialDescription() {
		return "Restores the player's run energy, by half the players agility level rounded up.";
	}

	@Override
	public int getBOBSize() {
		return 12;
	}

	@Override
	public int getSpecialAmount() {
		return 8;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		if (player.getRunEnergy() == 100) {
			player.getPackets().sendGameMessage("This wouldn't effect you at all.");
			return false;
		}
		int newLevel = getOwner().getSkills().getLevel(Skills.AGILITY) + 2;
		int runEnergy = player.getRunEnergy() + (Math.round(newLevel / 2));
		if (newLevel > getOwner().getSkills().getLevelForXp(Skills.AGILITY) + 2)
			newLevel = getOwner().getSkills().getLevelForXp(Skills.AGILITY) + 2;
		animate(new Animation(8229));
		player.gfx(new Graphics(1300));
		player.animate(new Animation(7660));
		player.getSkills().set(Skills.AGILITY, newLevel);
		player.setRunEnergy(runEnergy > 100 ? 100 : runEnergy);
		return true;
	}
}
