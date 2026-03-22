package raynna.game.npc.familiar;

import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.game.player.actions.skills.summoning.Summoning.Pouch;

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
		if (newLevel > player.getSkills().getRealLevel(Skills.DEFENCE) + 9)
			newLevel = player.getSkills().getRealLevel(Skills.DEFENCE) + 9;
		player.gfx(new Graphics(1300));
		player.animate(new Animation(7660));
		player.getSkills().set(Skills.DEFENCE, newLevel);
		return true;
	}
}
