package raynna.game.npc.others;

import raynna.game.WorldTile;
import raynna.game.npc.NPC;

@SuppressWarnings("serial")
public class MasterOfFear extends NPC {

	public MasterOfFear(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setName("Master of fear");
	}
}
