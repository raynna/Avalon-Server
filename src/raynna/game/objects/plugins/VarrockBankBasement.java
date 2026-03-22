package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;

public class VarrockBankBasement extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 24360, 24365 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.movePlayer(object.getId() == 24360 ? new WorldTile(3190, 9833, 0) : new WorldTile(3188, 3432, 0), 1, 2);
		return true;
	}
}
