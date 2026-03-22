package raynna.game.npc.dungeoneering;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.game.player.content.dungeoneering.RoomReference;

@SuppressWarnings("serial")
public class Guardian extends DungeonNPC {

	private RoomReference reference;

	public Guardian(int id, WorldTile tile, DungeonManager manager, RoomReference reference, double multiplier) {
		super(id, tile, manager, multiplier);
		this.reference = reference;
		setForceAgressive(true);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		getManager().updateGuardian(reference);
	}

}
