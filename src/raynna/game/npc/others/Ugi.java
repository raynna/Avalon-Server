package raynna.game.npc.others;

import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

@SuppressWarnings("serial")
public class Ugi extends NPC {

	private transient Player target;

	public Ugi(Player target, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.target = target;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (target.hasFinished() || !withinDistance(target, 10)) {
			target.getTreasureTrailsManager().setPhase(0);
			finish();
			return;
		}
	}

	public Player getTarget() {
		return target;
	}
}
