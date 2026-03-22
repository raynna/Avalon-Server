package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.actions.skills.thieving.Thieving;

public class ThievingStalls extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 34384, 34383, 14011, 7053, 34387, 34386, 34385 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		Thieving.handleStalls(player, object);
		return true;
	}
}
