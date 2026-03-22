package raynna.game.npc.familiar;

import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.game.player.actions.skills.summoning.Summoning.Pouch;

public class Irontitan extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6059371477618091701L;

	public Irontitan(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Iron Within";
	}

	@Override
	public String getSpecialDescription() {
		return "Inflicts three melee attacks instead of one in the next attack.";
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
		if (getOwner().getFamiliar().hasSpecialActivated()) {
			getOwner().getPackets().sendGameMessage("Special attack is already activated.");
			return false;
		}
		if (specialEnergy < getOwner().getFamiliar().getSpecialAmount()) {
			getOwner().getPackets().sendGameMessage("Your special move bar is too low to use this scroll.");
			return false;
		}
		getOwner().gfx(new Graphics(1300));
		getOwner().animate(new Animation(7660));
		getOwner().getSkills().addXp(Skills.SUMMONING, 1.22);
		return false;
	}
}
