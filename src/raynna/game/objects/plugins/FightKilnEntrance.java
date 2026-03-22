package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.controllers.FightKiln;

public class FightKilnEntrance extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 68107 };
	}

    @Override
	public boolean processObject(Player player, WorldObject object) {
		FightKiln.enterFightKiln(player, false);
		return true;
	}
	
	@Override
	public boolean processObject2(Player player, WorldObject object) {
		FightKiln.enterFightKiln(player, true);
		return true;
	}
}
