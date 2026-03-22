package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;

public class BlackKnightFortressEntrance extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2337, 2341, 2338 };
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		DoorsAndGates.handleDoorTemporary(player, object, 1200);
		return true;
	}
}
