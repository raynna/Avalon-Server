package raynna.game.npc.familiar;

import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.player.Player;
import raynna.game.player.actions.skills.summoning.Summoning.Pouch;

public class Bronzeminotaur extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4657392160246588028L;

	public Bronzeminotaur(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Bull Rush";
	}

	@Override
	public String getSpecialDescription() {
		return "A magical attack doing up to 40 life points of damage while stunning an opponent.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 6;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		player.gfx(new Graphics(1316));
		player.animate(new Animation(7660));
		return true;
	}
}
