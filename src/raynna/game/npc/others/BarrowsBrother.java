package raynna.game.npc.others;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

import static raynna.game.world.activity.BarrowsAreaKt.*;

@SuppressWarnings("serial")
public class BarrowsBrother extends NPC {

	private final Player owner;

	public BarrowsBrother(int id, WorldTile tile, Player owner) {
		super(id, tile, -1, true, true);
		this.owner = owner;
	}

	@Override
	public void sendDeath(Entity source) {
		if (owner != null) {
			if (getBarrowsTarget(owner) == this) {
				clearBarrowsTarget(owner, this);
				markBrotherSlain(owner, getId());
			}
		}
		super.sendDeath(source);
	}

	public void disapear() {
		finish();
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		if (owner != null) {
			if (getBarrowsTarget(owner) == this) {
				clearBarrowsTarget(owner, this);
			}
		}
		super.finish();
	}
}
