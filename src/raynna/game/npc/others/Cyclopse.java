package raynna.game.npc.others;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;

@SuppressWarnings("serial")
public class Cyclopse extends NPC {

	public Cyclopse(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}
}
