package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.controllers.FightCaves;

public class FightCavesEntrance extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 68107 };
	}

    @Override
	public boolean processObject(Player player, WorldObject object) {
		FightCaves.enterFightCaves(player);
		return true;
	}
}
