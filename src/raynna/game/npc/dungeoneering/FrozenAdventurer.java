package raynna.game.npc.dungeoneering;

import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

@SuppressWarnings("serial")
public class FrozenAdventurer extends NPC {

	private transient Player player;

	public FrozenAdventurer(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
	}

	@Override
	public void processNPC() {
		if (player == null || player.isDead() || player.hasFinished()) {
			finish();
			return;
		} else if (!player.getAppearance().isNPC()) {
			//TODO ToKashBloodChillerCombat.removeSpecialFreeze(player);
			finish();
			return;
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getFrozenPlayer() {
		return player;
	}

}
