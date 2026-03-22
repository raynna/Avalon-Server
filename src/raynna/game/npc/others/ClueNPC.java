package raynna.game.npc.others;

import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

@SuppressWarnings("serial")
public class ClueNPC extends NPC {

	private transient Player target;

	public ClueNPC(Player target, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
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

	@Override
	public void drop() {
		target.getTreasureTrailsManager().setPhase(2);
	}

	public Player getTarget() {
		return target;
	}
}
