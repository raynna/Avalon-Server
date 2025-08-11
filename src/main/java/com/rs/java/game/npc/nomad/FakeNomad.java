package com.rs.java.game.npc.nomad;

import com.rs.java.game.Hit;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;

@SuppressWarnings("serial")
public class FakeNomad extends NPC {

	private Nomad nomad;

	public FakeNomad(WorldTile tile, Nomad nomad) {
		super(8529, tile, -1, true, true);
		this.nomad = nomad;
		setForceMultiArea(true);
	}

	@Override
	public void handleHit(Hit hit) {
		nomad.destroyCopy(this);
	}

}
