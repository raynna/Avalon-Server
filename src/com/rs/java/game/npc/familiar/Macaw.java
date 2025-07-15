package com.rs.java.game.npc.familiar;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.herblore.HerbCleaning.Herbs;
import com.rs.java.game.player.actions.skills.summoning.Summoning.Pouch;
import com.rs.java.utils.Utils;

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
		World.addGroundItem(new Item(herb.getHerbId(), 1), player);
		return true;
	}
}
