package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;

public class SkeletalWyvernEntrance extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 33173, 33174 };
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.setNextWorldTile(object.getId() == 33173 ? new WorldTile(3056, 9555, 0) : new WorldTile(3056, 9562, 0));
		return true;
	}
}
