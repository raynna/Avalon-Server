package raynna.game.npc.fightcaves;

import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;

@SuppressWarnings("serial")
public class FightCavesNPC extends NPC {

	public FightCavesNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
		setForceAgressive(true);
		setForceTargetDistance(64);
		setForceAgressiveDistance(64);
	}

	@Override
	public void sendDeath(Entity source) {
		gfx(new Graphics(2924 + getSize()));
		super.sendDeath(source);
	}
}
