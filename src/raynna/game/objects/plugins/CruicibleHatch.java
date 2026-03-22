package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;

public class CruicibleHatch extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 67051, "Hatch" };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		player.getDialogueManager().startDialogue("Marv", true);
		return true;
	}
}

