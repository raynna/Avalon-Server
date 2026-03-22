package raynna.game.npc.others;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;

@SuppressWarnings("serial")
public class Strykewyrms extends NPC {

	public Strykewyrms(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(16);
		setIntelligentRouteFinder(false);
		setCantFollowUnderCombat(false);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}

}
