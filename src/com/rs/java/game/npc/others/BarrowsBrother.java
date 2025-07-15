package com.rs.java.game.npc.others;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.controlers.Barrows;
import com.rs.java.utils.Utils;

@SuppressWarnings("serial")
public class BarrowsBrother extends NPC {

	private Barrows barrows;

	public BarrowsBrother(int id, WorldTile tile, Barrows barrows) {
		super(id, tile, -1, true, true);
		this.barrows = barrows;
	}

	@Override
	public void sendDeath(Entity source) {
		if (barrows != null) {
			barrows.targetDied();
			barrows = null;
		}
		super.sendDeath(source);
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return getId() != 2030 ? 0 : Utils.random(3) == 0 ? 1 : 0;
	}

	public void disapear() {
		barrows = null;
		finish();
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		if (barrows != null) {
			barrows.targetFinishedWithoutDie();
			barrows = null;
		}
		super.finish();
	}

}
