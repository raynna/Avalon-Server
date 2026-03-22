package raynna.game.npc.others;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.minigames.castlewars.CastleWars;
import raynna.game.npc.NPC;
import raynna.util.Utils;

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
