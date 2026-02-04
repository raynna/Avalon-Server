package com.rs.java.game.npc.familiar;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ground.GroundItems;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.summoning.Summoning.Pouch;
import com.rs.java.utils.Utils;

public class Spiritspider extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5995661005749498978L;

	public Spiritspider(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Egg Spawn";
	}

	@Override
	public String getSpecialDescription() {
		return "Spawns a random amount of red eggs around the familiar.";
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
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		animate(new Animation(8267));
		player.animate(new Animation(7660));
		player.gfx(new Graphics(1316));
		WorldTile tile = this;
		// attemps to randomize tile by 4x4 area
		for (int trycount = 0; trycount < Utils.getRandom(10); trycount++) {
			tile = new WorldTile(this, 2);
			if (World.canMoveNPC(this.getPlane(), tile.getX(), tile.getY(), player.getSize()))
				return true;
			for (Entity entity : this.getPossibleTargets()) {
				if (entity instanceof Player) {
					Player players = (Player) entity;
					players.getPackets().sendGraphics(new Graphics(1342), tile);
				}
				GroundItems.addGroundItem(new Item(223, 1), tile, player, false, 120, 0);
			}
		}
		return true;
	}
}
