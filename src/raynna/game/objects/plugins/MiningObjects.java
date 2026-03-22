package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.skills.mining.Mining;
import raynna.game.player.skills.mining.RockDefinition;
import raynna.game.player.skills.woodcutting.TreeDefinition;
import raynna.game.player.skills.woodcutting.Woodcutting;

//TODO: Not done
public class MiningObjects extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return RockDefinition.Companion.getAllObjectIds().toArray();
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		RockDefinition def = RockDefinition.Companion.forObjectId(object.getId());
		if (def == null)
			return false;

		player.getActionManager().setAction(
				new Mining(player, object, def)
		);
		player.faceObject(object);
		return true;
	}
}
