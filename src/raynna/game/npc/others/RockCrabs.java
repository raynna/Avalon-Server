package raynna.game.npc.others;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;

public class RockCrabs extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 1776392517680641886L;

	public RockCrabs(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
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
