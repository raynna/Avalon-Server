package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;

public class RunespanPortal extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 38279 };
	}
	
	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getDialogueManager().startDialogue("RunespanPortalD");
		return true;
	}
}
