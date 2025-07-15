package com.rs.java.game.npc.others;

import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;

@SuppressWarnings("serial")
public class MasterOfFear extends NPC {

	public MasterOfFear(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setName("Master of fear");
	}
}
