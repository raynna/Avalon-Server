package raynna.game.npc.familiar;

import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.game.player.actions.skills.summoning.Summoning.Pouch;

public class Icetitan extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8361882652135063488L;

	public Icetitan(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Titan's Constitution ";
	}

	@Override
	public String getSpecialDescription() {
		return "Defence by 12.5%, and it can also increase a player's Life Points 80 points higher than their max Life Points.";
	}

	@Override
	public int getBOBSize() {
		return 0;
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
		int newLevel = getOwner().getSkills().getLevel(Skills.DEFENCE)
				+ (getOwner().getSkills().getRealLevel(Skills.DEFENCE) / (int) 12.5);
		if (newLevel > getOwner().getSkills().getRealLevel(Skills.DEFENCE) + (int) 12.5)
			newLevel = getOwner().getSkills().getRealLevel(Skills.DEFENCE) + (int) 12.5;
		getOwner().gfx(new Graphics(2011));
		getOwner().animate(new Animation(7660));
		getOwner().getSkills().set(Skills.DEFENCE, newLevel);
		return true;
	}
}
