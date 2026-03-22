package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.actions.skills.thieving.Thieving;

public class HamTrapdoor extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 5492, 5493 };
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		if (object.getId() == 5493) {
			player.useStairs(828, new WorldTile(3165, 3251, 0), 1, 2);
			return true;
		}
		Integer config = player.getTemporaryVarBits().get(object.getConfigByFile());
		if (config == null)
			player.getTemporaryVarBits().put(object.getConfigByFile(), 0);
		int configValue = player.getTemporaryVarBits().get(object.getConfigByFile()).intValue();
		if (configValue == 0)
			player.message("This trapdoor is locked.");
		else {
			player.useStairs(-1, new WorldTile(3149, 9652, 0), 0, 0);
			player.sendVarBit(object.getConfigByFile(), 0);
		}
		return true;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		player.sendVarBit(object.getConfigByFile(), 0);
		return true;
	}

	@Override
	public boolean processObject5(Player player, WorldObject object) {
		Thieving.pickHamHideout(player, object);
		return true;
	}
}
