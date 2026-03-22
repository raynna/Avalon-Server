package raynna.game.npc.familiar;

import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.player.Player;
import raynna.game.player.actions.skills.summoning.Summoning.Pouch;
import raynna.game.player.skills.woodcutting.TreeDefinition;
import raynna.game.player.skills.woodcutting.Woodcutting;

public class Beaver extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9181393770444014076L;

	public Beaver(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Multichop";
	}

	@Override
	public String getSpecialDescription() {
		return "Chops a tree, giving the owner its logs. There is also a chance that random logs may be produced.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 3;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.OBJECT;
	}

	@Override
	public boolean submitSpecial(Object context) {
		WorldObject object = (WorldObject) context;
		//TODO special for beaver
		return true;
	}
}
