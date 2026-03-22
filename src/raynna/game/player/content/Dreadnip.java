package raynna.game.player.content;

import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.util.Utils;

@SuppressWarnings("serial")
public class Dreadnip extends NPC {

	public static final String[] DREADNIP_MESSAGES = { "Your dreadnip couldn't attack so it left.",
			"The dreadnip gave up as you were too far away.", "Your dreadnip served its purpose and fled." };

	private transient Player owner;
	private int ticks;

	public Dreadnip(Player owner, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.owner = owner;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (owner == null || owner.hasFinished()) {
			finish(-1);
			return;
		} else if (getCombat().getTarget() == null || getCombat().getTarget().isDead()) {
			finish(0);
			return;
		} else if (Utils.getDistance(owner, this) >= 10) {
			finish(1);
			return;
		} else if (ticks++ == 33) {
			finish(2);
			return;
		}
	}

	private void finish(int index) {
		if (index != -1) {
			owner.getPackets().sendGameMessage(DREADNIP_MESSAGES[index]);
			owner.temporaryAttribute().remove("hasDN");
		}
		this.finish();
	}

	public Player getOwner() {
		return owner;
	}

	public int getTicks() {
		return ticks;
	}
}
