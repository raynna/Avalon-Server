package com.rs.java.game.npc.familiar;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.summoning.Summoning.Pouch;

public class Granitecrab extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 649164679697311630L;

	public Granitecrab(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Stony Shell";
	}

	@Override
	public String getSpecialDescription() {
		return "Increases your restance to all attacks by four.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		int newLevel = player.getSkills().getLevel(Skills.DEFENCE) + 4;
		if (newLevel > player.getSkills().getLevelForXp(Skills.DEFENCE) + 4)
			newLevel = player.getSkills().getLevelForXp(Skills.DEFENCE) + 4;
		player.gfx(new Graphics(1300));
		player.animate(new Animation(7660));
		gfx(new Graphics(8108));
		animate(new Animation(1326));
		player.getSkills().set(Skills.DEFENCE, newLevel);
		return true;
	}

}
