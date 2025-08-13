package com.rs.java.game.npc.others;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.minigames.castlewars.CastleWars;
import com.rs.java.game.npc.NPC;
import com.rs.java.utils.Utils;

@SuppressWarnings("serial")
public class CastleWarBarricade extends NPC {

	private int team;

	public CastleWarBarricade(int team, WorldTile tile) {
		super(1532, tile, -1, true, true);
		setCantFollowUnderCombat(true);
		this.team = team;
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		cancelFaceEntityNoCheck();
		if (getId() == 1533 && Utils.getRandom(20) == 0)
			sendDeath(this);
	}

	public void litFire() {
		transformIntoNPC(1533);
		sendDeath(this);
	}

	public void explode() {
		// TODO gfx
		sendDeath(this);
	}

	@Override
	public void sendDeath(Entity killer) {
		resetWalkSteps();
		getCombat().removeTarget();
		if (this.getId() != 1533) {
			animate(-1);
			reset();
			setLocation(getRespawnTile());
			finish();
		} else {
			super.sendDeath(killer);
		}
		CastleWars.removeBarricade(team, this);
	}

}
