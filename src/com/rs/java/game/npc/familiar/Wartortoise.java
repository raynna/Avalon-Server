package com.rs.java.game.npc.familiar;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.summoning.Summoning.Pouch;

public class Wartortoise extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5092434230714486203L;

	public Wartortoise(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Testudo";
	}

	@Override
	public String getSpecialDescription() {
		return "Increases defence by nine points.";
	}

	@Override
	public int getBOBSize() {
		return 18;
	}

	@Override
	public int getSpecialAmount() {
		return 20;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		int newLevel = player.getSkills().getLevel(Skills.DEFENCE) + 9;
		if (newLevel > player.getSkills().getLevelForXp(Skills.DEFENCE) + 9)
			newLevel = player.getSkills().getLevelForXp(Skills.DEFENCE) + 9;
		player.gfx(new Graphics(1300));
		player.animate(new Animation(7660));
		player.getSkills().set(Skills.DEFENCE, newLevel);
		return true;
	}
}
