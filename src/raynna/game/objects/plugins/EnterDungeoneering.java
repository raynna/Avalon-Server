package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;

public class EnterDungeoneering extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 48496 };
	}
	
	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getDungManager().enterDungeon(true, false);
		return true;
	}
}
