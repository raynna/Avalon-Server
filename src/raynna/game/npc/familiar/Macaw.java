package raynna.game.npc.familiar;

import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.item.Item;
import raynna.game.item.ground.GroundItems;
import raynna.game.player.Player;
import raynna.game.player.actions.skills.herblore.HerbCleaning.Herbs;
import raynna.game.player.actions.skills.summoning.Summoning.Pouch;
import raynna.util.Utils;

public class Macaw extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7805271915467121215L;

	public Macaw(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Herbcall";
	}

	@Override
	public String getSpecialDescription() {
		return "Creates a random herb.";
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
		Herbs herb;
		player.gfx(new Graphics(1300));
		player.animate(new Animation(7660));
		if (Utils.getRandom(100) == 0)
			herb = Herbs.values()[Utils.random(Herbs.values().length)];
		else
			herb = Herbs.values()[Utils.getRandom(3)];
		GroundItems.addGroundItem(new Item(herb.getHerbId(), 1), player);
		return true;
	}
}
